package com.yhn.shopping_pay_service.service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.yhn.shopping_common.exception.BusException;
import com.yhn.shopping_common.pojo.Orders;
import com.yhn.shopping_common.pojo.Payment;
import com.yhn.shopping_common.result.CodeEnum;
import com.yhn.shopping_common.service.OrderService;
import com.yhn.shopping_common.service.ZfbPayService;
import com.yhn.shopping_pay_service.ZfbPayConfig;
import com.yhn.shopping_pay_service.mapper.PaymentMapper;
import com.yhn.shopping_pay_service.util.ZfbVerifierUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@DubboService
public class ZfbPayServiceImpl implements ZfbPayService {
    @Autowired
    private ZfbPayConfig zfbPayConfig;
    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private PaymentMapper paymentMapper;


    @Override
    public String pcPay(Orders orders) {
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setNotifyUrl(zfbPayConfig.getNotifyUrl() + zfbPayConfig.getPcNotify());
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orders.getId()); //  订单编号
        bizContent.put("total_amount", orders.getPayment());// 订单金额
        bizContent.put("subject", orders.getCartGoods().get(0).getGoodsName()); // 订单标题
        request.setBizContent(bizContent.toString());

        try {
            AlipayTradePrecreateResponse response = alipayClient.execute(request);
            return response.getQrCode();
        } catch (AlipayApiException e) {
            throw new BusException(CodeEnum.QR_CODE_ERROR);
        }
    }

    @Override
    public void checkSign(Map<String, Object> paramMap) {
        // 获取所有参数
        Map<String, String[]> requestParameterMap = (Map<String, String[]>) paramMap.get("requestParameterMap");
        // 验签
        boolean valid = ZfbVerifierUtils.isValid(requestParameterMap, zfbPayConfig.getPublicKey());
        // 验签失败，抛出异常
        if (!valid) {
            throw new BusException(CodeEnum.CHECK_SIGN_ERROR);
        }
    }

    @Override
    public void addPayment(Payment payment) {
        paymentMapper.insert(payment);
    }
}
