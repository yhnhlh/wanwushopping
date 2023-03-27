package com.yhn.shopping_order_service.service;

import com.yhn.shopping_common.pojo.CartGoods;
import com.yhn.shopping_common.pojo.Orders;
import com.yhn.shopping_common.service.OrderService;
import com.yhn.shopping_order_service.mapper.CartGoodsMapper;
import com.yhn.shopping_order_service.mapper.OrdersMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@DubboService
public class OrdersServiceImpl implements OrderService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private CartGoodsMapper cartGoodsMapper;

    @Override
    public Orders add(Orders orders) {
        // 订单状态未付款
        orders.setStatus(1);
        // 订单创建时间
        orders.setCreateTime(new Date());
        // 计算订单价格，遍历订单所有商品
        List<CartGoods> cartGoods = orders.getCartGoods();
        BigDecimal sum = BigDecimal.ZERO;
        for (CartGoods cartGood : cartGoods) {
            // 数量
            BigDecimal num = new BigDecimal(cartGood.getNum());
            // 单价
            BigDecimal price = cartGood.getPrice();
            // 数量*单价
            BigDecimal multiply = num.multiply(price);
            sum = sum.add(multiply);
        }
        orders.setPayment(sum);
        // 保存订单
        ordersMapper.insert(orders);

        for (CartGoods cartGood : cartGoods) {
            // 购物车商品保存到数据库中
            cartGood.setOrderId(orders.getId());
            cartGoodsMapper.insert(cartGood);
        }
        return orders;
    }

    @Override
    public void update(Orders orders) {
        ordersMapper.updateById(orders);
    }

    @Override
    public Orders findById(String id) {
        return ordersMapper.findById(id);
    }

    @Override
    public List<Orders> findUserOrders(Long userId, Integer status) {
        return ordersMapper.findOrdersByUserIdAndStatus(userId,status);
    }
}
