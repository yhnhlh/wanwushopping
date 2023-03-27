package com.yhn.shopping_manager_api.security;

import com.alibaba.fastjson.JSON;
import com.yhn.shopping_common.result.BaseResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
// 未登录处理器
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("text/json;charset=utf-8");
        BaseResult result = new BaseResult(401, "用户未登录", null);
        response.getWriter().write(JSON.toJSONString(result));
    }
}
