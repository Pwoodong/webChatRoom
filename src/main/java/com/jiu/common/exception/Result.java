package com.jiu.common.exception;

import lombok.Data;

/**
 * Package com.jiu.common.exception
 * ClassName Result.java
 * Description  返回结果
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-15 10:46
 **/
@Data
public class Result<T> {
    /** 是否成功 */
    private Boolean success;
    /** 状态码 */
    private Integer code;
    /** 提示信息 */
    private String msg;
    /** 数据 */
    private T data;

    public Result() {

    }

    /** 自定义返回结果的构造方法 */
    public Result(Boolean success,Integer code, String msg,T data) {
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /** 自定义异常返回的结果 */
    public static Result defineError(DefinitionException de){
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(de.getErrorCode());
        result.setMsg(de.getErrorMsg());
        result.setData(null);
        return result;
    }

    /** 其他异常处理方法返回的结果 */
    public static Result otherError(ExceptionEnum errorEnum){
        Result result = new Result();
        result.setMsg(errorEnum.getErrorMsg());
        result.setCode(errorEnum.getErrorCode());
        result.setSuccess(false);
        result.setData(null);
        return result;
    }

}
