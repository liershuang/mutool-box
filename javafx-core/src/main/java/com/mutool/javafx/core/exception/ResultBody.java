package com.mutool.javafx.core.exception;

import lombok.Getter;


/**
 * 描述：结果类<br>
 * 作者：les<br>
 * 日期：2020/11/27 10:45<br>
 */
@Getter
public class ResultBody<T> {

    /** 状态码 */
    private String code;
    /** 响应信息 */
    private String msg;
    /** 响应的具体数据 */
    private T data;

    public ResultBody(T data) {
        this(ErrorCodeEnum.SUCCESS.getErrorCode(), ErrorCodeEnum.SUCCESS.getErrorMsg(), data);
    }

    public ResultBody(ErrorCodeEnum errorCodeEnum, T data){
        this.code = errorCodeEnum.getErrorCode();
        this.msg = errorCodeEnum.getErrorMsg();
        this.data = data;
    }

    public ResultBody(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultBody(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ResultBody success(T data){
        return new ResultBody(data);
    }

    public static ResultBody error(String code, String msg){
        return new ResultBody(code, msg);
    }

    public static ResultBody error(ErrorCodeEnum errorCodeEnum){
        return new ResultBody(errorCodeEnum.getErrorCode(), errorCodeEnum.getErrorMsg());
    }

}
