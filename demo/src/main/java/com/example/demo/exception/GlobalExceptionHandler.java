package com.example.demo.exception;

import com.example.demo.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


// 全局异常捕获器
@ControllerAdvice
public class GlobalExceptionHandler {

//    如果抛出的是ServiceException，则调用该方法
//    @param se 业务异常
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Result handle(ServiceException se)
    {
        return Result.error(se.getCode(), se.getMessage());
    }
}
