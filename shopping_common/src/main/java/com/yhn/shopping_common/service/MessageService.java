package com.yhn.shopping_common.service;

import com.yhn.shopping_common.result.BaseResult;

public interface MessageService {
    /**
     * 发送短信
     * @param phoneNumber 手机号
     * @param code 验证码
     * @return 返回结果
     */
    BaseResult sendMessage(String phoneNumber, String code) ;
}
