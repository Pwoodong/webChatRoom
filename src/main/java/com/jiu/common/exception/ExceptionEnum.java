package com.jiu.common.exception;

/**
 * Package com.jiu.common.exception
 * ClassName ExceptionEnum.java
 * Description 异常枚举
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-15 10:47
 **/
public enum ExceptionEnum {

    /** 数据操作异常定义 */
    SUCCESS(200, "成功"),
    NO_PERMISSION(403,"你没得权限"),
    NO_AUTH(401,"你能不能先登录一下"),
    NOT_FOUND(404, "未找到该资源!"),
    INTERNAL_SERVER_ERROR(500, "服务器出现异常"),
    ;

    /** 错误码 */
    private Integer errorCode;

    /** 错误信息 */
    private String errorMsg;

    ExceptionEnum(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
