package com.example.demo.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException{
    private String code;

    // 构造函数
    public ServiceException(String code, String msg){
        super(msg);
        this.code = code;
    }
}
