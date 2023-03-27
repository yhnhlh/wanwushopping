package com.yhn.shopping_common.exception;

import com.yhn.shopping_common.result.BaseResult;
import com.yhn.shopping_common.result.CodeEnum;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.AccessDeniedException;

//统一异常处理器
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 处理业务异常
    @ExceptionHandler(BusException.class)
    public BaseResult defaultExceptionHandler(HttpServletRequest req, HttpServletResponse resp, BusException e) {
        BaseResult baseResult = new BaseResult(e.getCode(), e.getMessage(), null);
        return baseResult;
    }

    // 处理系统异常
    @ExceptionHandler(Exception.class)
    public BaseResult defaultExceptionHandler(HttpServletRequest req, HttpServletResponse resp, Exception e) {
        e.printStackTrace();
        BaseResult baseResult = new BaseResult(CodeEnum.SYSTEM_ERROR.getCode(),CodeEnum.SYSTEM_ERROR.getMessage(),null);
        return baseResult;
    }

}
