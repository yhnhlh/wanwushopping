package com.yhn.shopping_cart_customer_api.controller;

import com.yhn.shopping_common.pojo.CartGoods;
import com.yhn.shopping_common.result.BaseResult;
import com.yhn.shopping_common.service.CartService;
import com.yhn.shopping_common.util.JWTUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车
 */
@RestController
@RequestMapping("/user/cart")
public class CartController {
    @DubboReference
    private CartService cartService;


    /**
     * 查询用户购物车
     * @param token 用户令牌
     * @return 用户购物车列表
     */
    @GetMapping("/findCartList")
    public BaseResult<List<CartGoods>> findCartList(@RequestHeader String token){
        Long userId = JWTUtil.getId(token); // 获取用户id
        List<CartGoods> cartList = cartService.findCartList(userId);
        return BaseResult.ok(cartList);
    }


    /**
     * 新增商品到购物车
     * @param cartGoods 购物车商品
     * @param token 用户令牌
     * @return 操作结果
     */
    @PostMapping("/addCart")
    public BaseResult addCart(@RequestBody CartGoods cartGoods,@RequestHeader String token){
        Long userId = JWTUtil.getId(token); // 获取用户id
        cartService.addCart(userId,cartGoods);
        return BaseResult.ok();
    }


    /**
     * 修改购物车商品数量
     * @param token 用户令牌
     * @param goodId 商品id
     * @param num 修改后的数量
     * @return 操作结果
     */
    @GetMapping("/handleCart")
    public BaseResult addCart(@RequestHeader String token,Long goodId,Integer num){
        Long userId = JWTUtil.getId(token); // 获取用户id
        cartService.handleCart(userId,goodId,num);
        return BaseResult.ok();
    }


    /**
     * 删除购物车商品
     * @param token 用户令牌
     * @param goodId 商品id
     * @return 操作结果
     */
    @DeleteMapping("/deleteCart")
    public BaseResult addCart(@RequestHeader String token,Long goodId){
        Long userId = JWTUtil.getId(token); // 获取用户id
        cartService.deleteCartOption(userId,goodId);
        return BaseResult.ok();
    }
}

