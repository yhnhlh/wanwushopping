package com.yhn.shopping_common.interceptor;

import com.yhn.shopping_common.util.JWTUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 拦截器，验证令牌
public class JWTInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的token
        String token = request.getHeader("token");
        // 验证token
        JWTUtil.verify(token);
        return true;
    }
}

