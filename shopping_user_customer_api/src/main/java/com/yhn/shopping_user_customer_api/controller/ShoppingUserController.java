package com.yhn.shopping_user_customer_api.controller;

import com.yhn.shopping_common.pojo.ShoppingUser;
import com.yhn.shopping_common.result.BaseResult;
import com.yhn.shopping_common.service.MessageService;
import com.yhn.shopping_common.service.ShoppingUserService;
import com.yhn.shopping_common.util.RandomUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * 商城用户
 */
@RestController
@RequestMapping("/user/shoppingUser")
public class ShoppingUserController {
    @DubboReference
    private ShoppingUserService shoppingUserService;

    @DubboReference
    private MessageService messageService;
    /**
     * 发送注册短信
     * @param phone 注册手机号
     * @return 操作结果
     */
    @GetMapping("/sendMessage")
    public BaseResult sendMessage(String phone){
        // 1.生成随机四位数
        String checkCode = RandomUtil.buildCheckCode(4);
        // 2.发送短信
        BaseResult result = messageService.sendMessage(phone, checkCode);
        // 3.发送成功，将验证码保存到redis中,发送失败，返回发送结果
        if (200 == result.getCode()) {
            shoppingUserService.saveRegisterCheckCode(phone, checkCode);
            return BaseResult.ok();
        } else {
            return result;
        }
    }

    /**
     * 验证用户注册验证码
     * @param phone 手机号
     * @param checkCode 验证码
     * @return 200验证成功，605验证码不正确
     */
    @GetMapping("/registerCheckCode")
    public BaseResult register(String phone,String checkCode){
        shoppingUserService.registerCheckCode(phone,checkCode);
        return BaseResult.ok();
    }

    /**
     * 用户注册
     * @param shoppingUser 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public BaseResult register(@RequestBody ShoppingUser shoppingUser){
        shoppingUserService.register(shoppingUser);
        return BaseResult.ok();
    }

    /**
     * 用户名密码登录
     * @param shoppingUser 用户对象
     * @return 登录结果
     */
    @PostMapping("/loginPassword")
    public BaseResult loginPassword(@RequestBody ShoppingUser shoppingUser){
        String sign = shoppingUserService.loginPassword(shoppingUser.getUsername(), shoppingUser.getPassword());
        return BaseResult.ok(sign);
    }

    /**
     * 发送登录短信验证码
     *
     * @param phone 手机号
     * @return 操作结果
     */
    @GetMapping("/sendLoginCheckCode")
    public BaseResult sendLoginCheckCode(String phone) {
        // 1.生成随机四位数
        String checkCode = RandomUtil.buildCheckCode(4);
        // 2.发送短信
        BaseResult result = messageService.sendMessage(phone, checkCode);
        // 3.发送成功，将验证码保存到redis中,发送失败，返回发送结果
        if (200 == result.getCode()) {
            shoppingUserService.saveLoginCheckCode(phone, checkCode);
            return BaseResult.ok();
        } else {
            return result;
        }
    }
    /**
     * 手机号验证码登录
     * @param phone 手机号
     * @param checkCode 验证码
     * @return 登录结果
     */
    @PostMapping("/loginCheckCode")
    public BaseResult loginCheckCode(String phone, String checkCode){
        String sign = shoppingUserService.loginCheckCode(phone, checkCode);
        return BaseResult.ok(sign);
    }
    /**
     * 获取登录的用户名
     * @param token 令牌
     * @return 用户名
     */
    @GetMapping("/getName")
    public BaseResult<String> getName(@RequestHeader("token") String token){
        String name = shoppingUserService.getName(token);
        return BaseResult.ok(name);
    }








}
