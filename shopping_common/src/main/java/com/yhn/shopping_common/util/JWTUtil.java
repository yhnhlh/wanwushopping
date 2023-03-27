package com.yhn.shopping_common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.yhn.shopping_common.exception.BusException;
import com.yhn.shopping_common.pojo.ShoppingUser;
import com.yhn.shopping_common.result.CodeEnum;

import java.util.Date;

public class JWTUtil {
    //token过期时间，一天
    private static final Long EXPIRE_DATE = 1000*60*60*24L;
    // 秘钥
    private static final String SECRET = "baizhan";
    // 签发者
    private static final String ISSUER = "BAIZHAN";


    /**
     * 签名生成
     * @param shoppingUser
     * @return
     */
    public static String sign(ShoppingUser shoppingUser){
        String token = JWT.create()
                .withIssuer(ISSUER) // 签发者
                .withIssuedAt(new Date()) // 签发时间
                .withExpiresAt(new Date(new Date().getTime() + EXPIRE_DATE)) // 过期时间
                .withSubject(shoppingUser.getUsername()) // 保存用户名
                .withClaim("userId",shoppingUser.getId()) // 保存用户id
                .sign(Algorithm.HMAC256(SECRET)); // 秘钥
        return token;
    }


    /**
     * 签名解析
     * @param token 签名字符串
     * @return 解析得出的用户名
     */
    public static String verify(String token){
        try {
            String username = JWT
                    .require(Algorithm.HMAC256(SECRET))
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
            return username;
        } catch (Exception e){
            throw new BusException(CodeEnum.VERIFY_TOKEN_ERROR);
        }
    }
    /**
     * 签名解析，获取用户id
     * @param token 签名字符串
     * @return 用户id
     */
    public static Long getId(String token){
        try {
            Long userId = JWT
                    .require(Algorithm.HMAC256(SECRET))
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getClaim("userId")
                    .asLong();
            return userId;
        } catch (Exception e){
            throw new BusException(CodeEnum.VERIFY_TOKEN_ERROR);
        }
    }
}

