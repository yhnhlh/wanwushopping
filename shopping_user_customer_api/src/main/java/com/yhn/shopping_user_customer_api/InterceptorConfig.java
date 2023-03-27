package com.yhn.shopping_user_customer_api;

import com.yhn.shopping_common.interceptor.JWTInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 拦截器配置
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptor())
                .addPathPatterns("/**") //拦截的接口
                .excludePathPatterns(
                        "/user/shoppingUser/sendMessage",
                        "/user/shoppingUser/registerCheckCode",
                        "/user/shoppingUser/register",
                        "/user/shoppingUser/loginPassword",
                        "/user/shoppingUser/sendLoginCheckCode",
                        "/user/shoppingUser/loginCheckCode"
                ); //放行的接口
    }
}
