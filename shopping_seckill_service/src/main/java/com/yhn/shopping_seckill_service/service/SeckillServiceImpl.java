package com.yhn.shopping_seckill_service.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhn.shopping_common.exception.BusException;
import com.yhn.shopping_common.pojo.CartGoods;
import com.yhn.shopping_common.pojo.Orders;
import com.yhn.shopping_common.pojo.SeckillGoods;
import com.yhn.shopping_common.result.CodeEnum;
import com.yhn.shopping_common.service.SeckillService;
import com.yhn.shopping_seckill_service.mapper.SeckillGoodsMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@DubboService
@Component
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    /**
     * 每分钟查询一次数据库，更新redis中的秒杀商品数据
     * 条件为startTime < 当前时间 < endTime，库存大于0
     */
    @Scheduled(cron = "0/5 * * * * *")
    public void refreshRedis(){
        // 将redis中秒杀商品的库存数据同步到mysql
        List<SeckillGoods> seckillGoodsListOld = redisTemplate.boundHashOps("seckillGoods").values();
        for (SeckillGoods seckillGoods : seckillGoodsListOld) {
            // 在数据库中查询秒杀商品
            SeckillGoods sqlSeckillGoods = seckillGoodsMapper.selectById(seckillGoods.getId());
            // 修改秒杀商品的库存
            sqlSeckillGoods.setStockCount(seckillGoods.getStockCount());
            seckillGoodsMapper.updateById(sqlSeckillGoods);
        }
        System.out.println("同步mysql秒杀商品到redis...");


        // 1.查询数据库中正在秒杀的商品
        QueryWrapper<SeckillGoods> queryWrapper = new QueryWrapper();
        Date date = new Date();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        queryWrapper.le("startTime",now) // 当前时间晚于开始时间
                .ge("endTime",now) // 当前时间早于开始时间
                .gt("stockCount",0); // 库存大于0
        List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.selectList(queryWrapper);


        // 2.删除之前的秒杀商品
        redisTemplate.delete("seckillGoods");


        // 3.保存现在正在秒杀的商品
        for (SeckillGoods seckillGoods : seckillGoodsList) {
            redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getGoodsId(),seckillGoods);
        }
    }

    @Override
    public Page<SeckillGoods> findPageByRedis(int page, int size) {
        // 1.查询所有秒杀商品列表
        List<SeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();

        // 2.获取当前页商品列表
        // 开始截取索引
        int start = (page - 1) * size;
        // 结束截取索引
        int end = start + page > seckillGoodsList.size() ? seckillGoodsList.size() : start + page;
        // 获取当前页结果集
        List<SeckillGoods> seckillGoods = seckillGoodsList.subList(start, end);

        // 3.构造页面对象
        Page<SeckillGoods> seckillGoodsPage = new Page();
        seckillGoodsPage.setCurrent(page) // 当前页
                .setSize(size) // 每页条数
                .setTotal(seckillGoodsList.size()) // 总条数
                .setRecords(seckillGoods); // 结果集
        return seckillGoodsPage;
    }

    @Override
    public SeckillGoods findSeckillGoodsByRedis(Long goodsId) {
        return (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(goodsId);
    }

    @Override
    public Orders createOrder(Orders orders) {
        // 1.生成订单对象
        orders.setId(IdWorker.getIdStr()); // 手动生产订单id
        orders.setStatus(1); // 订单状态未付款
        orders.setCreateTime(new Date()); // 订单创建时间
        orders.setExpire(new Date(new Date().getTime()+1000*60*5));
        // 计算商品价格
        CartGoods cartGoods = orders.getCartGoods().get(0);
        Integer num = cartGoods.getNum();
        BigDecimal price = cartGoods.getPrice();
        BigDecimal sum = price.multiply(BigDecimal.valueOf(num));
        orders.setPayment(sum);

        // 2.减少秒杀商品库存
        // 查询秒杀商品
        SeckillGoods seckillGoods = findSeckillGoodsByRedis(cartGoods.getGoodId());
        // 查询库存，库存不足抛出异常
        Integer stockCount = seckillGoods.getStockCount();
        if (stockCount <= 0){
            throw new BusException(CodeEnum.NO_STOCK_ERROR);
        }
        // 减少库存
        seckillGoods.setStockCount(seckillGoods.getStockCount() - cartGoods.getNum());
        redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getGoodsId(),seckillGoods);

        // 3.保存订单数据
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 设置订单一分钟过期
        redisTemplate.opsForValue().set(orders.getId(),orders,1, TimeUnit.MINUTES);
        /**
         * 给订单创建副本，副本的过期时间长于原订单
         * redis过期后触发过期事件时，redis数据已经过期，此时只能拿到key，拿不到value。
         * 而过期事件需要回退商品库存，必须拿到value即订单详情，才能拿到商品数据，进行回退操作
         * 我们保存一个订单副本，过期时间长于原订单，此时就可以通过副本拿到原订单数据
         */
        redisTemplate.opsForValue().set(orders.getId()+"_copy",orders,2,TimeUnit.MINUTES);
        return orders;
    }

    @Override
    public Orders findOrder(String id) {
        return (Orders) redisTemplate.opsForValue().get(id);
    }

    @Override
    public Orders pay(String orderId) {
        // 1.查询订单，设置数据
        Orders orders = (Orders) redisTemplate.opsForValue().get(orderId);
        if (orders == null) {
            throw new BusException(CodeEnum.ORDER_EXPIRED_ERROR); // 订单过期
        }
        orders.setStatus(2);
        orders.setPaymentTime(new Date());
        orders.setPaymentType(2); // 支付宝支付
        // 2.从redis删除订单数据
        redisTemplate.delete(orderId);
        redisTemplate.delete(orderId+"_copy");
        // 3.返回订单数据
        return orders;
    }
}
