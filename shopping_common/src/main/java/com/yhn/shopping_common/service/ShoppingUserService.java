package com.yhn.shopping_common.service;

import com.yhn.shopping_common.pojo.ShoppingUser;

/**
 * 商城用户服务
 */
public interface ShoppingUserService {
    // 注册时向redis保存手机号+验证码
    void saveRegisterCheckCode(String phone,String checkCode);
    // 注册时验证手机号
    void registerCheckCode(String phone,String checkCode);
    // 用户注册
    void register(ShoppingUser shoppingUser);


    // 用户名密码登录
    String loginPassword(String username,String password);


    // 登录时向redis保存手机号+验证码
    void saveLoginCheckCode(String phone,String checkCode);
    // 手机号验证码登录
    String loginCheckCode(String phone, String checkCode);

    // 获取登录用户名
    String getName(String token);
    // 获取登录用户
    ShoppingUser getLoginUser(String token);
}

