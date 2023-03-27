package com.yhn.shopping_common.service;

import com.yhn.shopping_common.pojo.Orders;
import com.yhn.shopping_common.pojo.Payment;

import java.util.Map;

/**
 * 支付宝服务接口
 */
public interface ZfbPayService {
    /**
     * 生成二维码
     * @param orders 订单对象
     * @return 二维码字符串
     */
    String pcPay(Orders orders);


    /**
     * 验签
     * @param paramMap 支付相关参数
     */
    void checkSign(Map<String,Object> paramMap);


    /**
     * 生成交易记录
     * @param payment 交易记录对象
     */
    void addPayment(Payment payment);
}

