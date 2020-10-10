package com.jiu.common.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Package com.jiu.common.exception
 * ClassName GlobalExceptionHandler.java
 * Description 全局异常处理
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-15 10:49
 **/
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义异常
     *
     */
    @ExceptionHandler(value = DefinitionException.class)
    @ResponseBody
    public Result bizExceptionHandler(DefinitionException e) {
        return Result.defineError(e);
    }

    /**
     * 处理其他异常
     *
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result exceptionHandler( Exception e) {
        return Result.otherError(ExceptionEnum.INTERNAL_SERVER_ERROR);
    }

}
