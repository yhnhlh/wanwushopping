package com.yhn.shopping_seckill_customer_api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhn.shopping_common.pojo.Orders;
import com.yhn.shopping_common.pojo.SeckillGoods;
import com.yhn.shopping_common.result.BaseResult;
import com.yhn.shopping_common.service.OrderService;
import com.yhn.shopping_common.service.SeckillService;
import com.yhn.shopping_common.util.JWTUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.*;

/**
 * 秒杀商品
 */
@RestController
@RequestMapping("/user/seckillGoods")
public class SeckillGoodsController {
    @DubboReference
    private SeckillService seckillService;

    @DubboReference
    private OrderService orderService;


    /**
     * 用户分页查询秒杀商品
     * @param page 页数
     * @param size 每页条数
     * @return 查询结果
     */
    @GetMapping("/findPage")
    public BaseResult<Page<SeckillGoods>> findPage(int page, int size){
        Page<SeckillGoods> seckillGoodsPage = seckillService.findPageByRedis(page, size);
        return BaseResult.ok(seckillGoodsPage);
    }
    /**
     * 用户查询秒杀商品详情
     * @param id 商品Id
     * @return 查询结果
     */
    @GetMapping("/findById")
    public BaseResult<SeckillGoods> findById(Long id){
        SeckillGoods seckillGoods = seckillService.findSeckillGoodsByRedis(id);
        return BaseResult.ok(seckillGoods);
    }
    /**
     * 生成秒杀订单
     * @param orders 订单对象
     * @return 生成的订单
     */
    @PostMapping("/add")
    public BaseResult<Orders> add(@RequestBody Orders orders, @RequestHeader String token){
        Long userId = JWTUtil.getId(token); // 获取登录用户
        orders.setUserId(userId);
        Orders order = seckillService.createOrder(orders);
        return BaseResult.ok(order);
    }

    /**
     * 根据id查询秒杀订单
     * @param id 订单id
     * @return 查询结果
     */
    @GetMapping("/findOrder")
    public BaseResult<Orders> findOrder(String id){
        Orders orders = seckillService.findOrder(id);
        return BaseResult.ok(orders);
    }

    /**
     * 支付秒杀订单
     * @param id 订单id
     */
    @GetMapping("/pay")
    public BaseResult pay(String id){
        // 支付秒杀订单
        Orders orders = seckillService.pay(id);
        // 将订单数据存入数据库
        orderService.add(orders);
        return BaseResult.ok();
    }


}

