package com.qst.login;

import com.qst.domain.entity.Mess;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
//开发过程的问题排查类
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Mess handleAllExceptions(Exception e) {
        e.printStackTrace();
        return Mess.fail().mess("服务器内部错误：" + e.getMessage());
    }
}
