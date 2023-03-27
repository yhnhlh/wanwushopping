package com.yhn.shopping_cart_service.service;

import com.yhn.shopping_common.pojo.CartGoods;
import com.yhn.shopping_common.service.CartService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@DubboService
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;
    // 监听修改购物车商品队列
    @RabbitListener(queues = "sync_cart_queue")
    public void listenSyncQueue(CartGoods cartGoods){
        refreshCartGoods(cartGoods);
    }

    // 监听删除购物车商品队列
    @RabbitListener(queues = "del_cart_queue")
    public void listenDelQueue(CartGoods cartGoods){
        deleteCartGoods(cartGoods);
    }

    @Override
    public void addCart(Long userId, CartGoods cartGoods) {
        // 1.根据用户id获取用户购物车列表
        List<CartGoods> cartList = findCartList(userId);
        // 2.查询购物车是否有该商品，如果有商品，添加商品数量
        for (CartGoods cartGoods1 : cartList) {
            if (cartGoods.getGoodId().equals(cartGoods1.getGoodId())){
                int newNum = cartGoods1.getNum() + cartGoods.getNum();
                cartGoods1.setNum(newNum);
                redisTemplate.boundHashOps("cartList").put(userId,cartList);
                return;
            }
        }
        // 3.如果购物车没有该商品，将商品添加到购物车列表
        cartList.add(cartGoods);
        redisTemplate.boundHashOps("cartList").put(userId,cartList);
    }

    @Override
    public void handleCart(Long userId, Long goodId, Integer num) {
        // 获取用户购物车列表
        List<CartGoods> cartList = findCartList(userId);
        // 遍历列表找到对应商品
        for (CartGoods cartGoods : cartList) {
            if (goodId.equals(cartGoods.getGoodId())) {
                // 改变商品数量
                cartGoods.setNum(num);
                break;
            }
        }
        // 将新的购物车列表保存到redis中
        redisTemplate.boundHashOps("cartList").put(userId, cartList);
    }

    @Override
    public void deleteCartOption(Long userId, Long goodId) {
        // 获取用户购物车列表
        List<CartGoods> cartList = findCartList(userId);
        // 将商品移出列表
        for (CartGoods cartGoods : cartList) {
            if (goodId.equals(cartGoods.getGoodId())) {
                cartList.remove(cartGoods);
                break;
            }
        }
        // 将新的购物车列表保存到redis中
        redisTemplate.boundHashOps("cartList").put(userId, cartList);
    }

    @Override
    public List<CartGoods> findCartList(Long userId) {
        Object cartList = redisTemplate.boundHashOps("cartList").get(userId);
        if (cartList == null) {
            return new ArrayList<CartGoods>();
        } else {
            return (List<CartGoods>) cartList;
        }

    }

    @Override
    public void refreshCartGoods(CartGoods cartGoods) {
        // 获取所有用户购物车商品
        BoundHashOperations cartList = redisTemplate.boundHashOps("cartList");
        Map<Long,List<CartGoods>> allCartGoods = cartList.entries();
        Set<Map.Entry<Long, List<CartGoods>>> entries = allCartGoods.entrySet();

        // 遍历所有用户的购物车
        for (Map.Entry<Long, List<CartGoods>> entry : entries) {
            List<CartGoods> goodsList = entry.getValue();
            // 遍历一个用户购物车的所有商品
            for (CartGoods goods : goodsList) {
                // 如果该商品是被更新的商品，修改商品数据
                if (cartGoods.getGoodId().equals(goods.getGoodId())){
                    goods.setGoodsName(cartGoods.getGoodsName());
                    goods.setHeaderPic(cartGoods.getHeaderPic());
                    goods.setPrice(cartGoods.getPrice());
                }
            }
        }
        // 将改变后所有用户购物车重新放入redis
        redisTemplate.delete("cartList");
        redisTemplate.boundHashOps("cartList").putAll(allCartGoods);
    }

    @Override
    public void deleteCartGoods(CartGoods cartGoods) {
        BoundHashOperations cartList = redisTemplate.boundHashOps("cartList");
        // 所有用户的购物车
        Map<String,List<CartGoods>> allCartGoods = cartList.entries();
        Set<Map.Entry<String, List<CartGoods>>> entries = allCartGoods.entrySet();
        // 遍历所有用户的购物车
        for (Map.Entry<String, List<CartGoods>> entry : entries) {
            List<CartGoods> goodsList = entry.getValue();
            // 遍历一个用户购物车的所有商品
            for (CartGoods goods : goodsList) {
                // 如果该商品是被删除的商品
                if (cartGoods.getGoodId().equals(goods.getGoodId())){
                    goodsList.remove(goods);
                    break;
                }
            }
        }

        // 将改变后的map重新放入redis
        redisTemplate.delete("cartList");
        redisTemplate.boundHashOps("cartList").putAll(allCartGoods);
    }
}
