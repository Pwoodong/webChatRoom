package com.jiu.common.exception;

/**
 * Package com.jiu.common.exception
 * ClassName DefinitionException.java
 * Description 自定义异常
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-15 10:48
 **/
public class DefinitionException extends RuntimeException {

    protected Integer errorCode;
    protected String errorMsg;

    public DefinitionException(){

    }
    public DefinitionException(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}
