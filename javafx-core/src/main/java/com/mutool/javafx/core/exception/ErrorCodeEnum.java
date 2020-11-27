package com.mutool.javafx.core.exception;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2020/11/27 10:28<br>
 */
public enum ErrorCodeEnum implements BaseErrorInfoInterface {

    SUCCESS("200", "成功!"),
    BODY_NOT_MATCH("400","请求的数据格式不符!"),
    SIGNATURE_NOT_MATCH("401","请求的数字签名不匹配!"),
    NOT_FOUND("404", "未找到该资源!"),
    INTERNAL_SERVER_ERROR("500", "服务器内部错误!"),
    SERVER_BUSY("503","服务器正忙，请稍后再试!"),
    BUSIN_ERROR("1001","业务异常!");

    /** 错误码 */
    private String errorCode;
    /** 错误描述 */
    private String errorMsg;

    ErrorCodeEnum(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

}
