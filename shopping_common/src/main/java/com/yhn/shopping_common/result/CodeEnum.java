package com.yhn.shopping_common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CodeEnum {
    // 正常
    SUCCESS(200, "OK"),
    // 系统异常
    SYSTEM_ERROR(500, "系统异常"),
    // 业务异常
    PARAMETER_ERROR(601, "参数异常"),
    INSERT_PRODUCT_TYPE_ERROR(602,"该商品类型不能添加子类型"),
    DELETE_PRODUCT_TYPE_ERROR(603,"该商品类型有了子类型，无法删除"),
    UPLOAD_FILE_ERROR(604,"文件上传失败"),
    REGISTER_CODE_ERROR(605,"注册验证码错误"),
    REGISTER_REPEAT_PHONE_ERROR(606,"手机号已注册"),
    REGISTER_REPEAT_NAME_ERROR(607,"用户名已注册"),
    LOGIN_NAME_PASSWORD_ERROR(608,"用户名或密码错误"),
    LOGIN_CODE_ERROR(608,"登录验证码错误"),
    VERIFY_TOKEN_ERROR(611,"令牌解析错误"),
    QR_CODE_ERROR(612,"二维码生成错误"),
    CHECK_SIGN_ERROR(613,"支付宝验签异常"),
    NO_STOCK_ERROR(614,"库存不足异常"),
    ORDER_EXPIRED_ERROR(615,"订单过期异常")
    ;

    private final Integer code;
    private final String message;
}
