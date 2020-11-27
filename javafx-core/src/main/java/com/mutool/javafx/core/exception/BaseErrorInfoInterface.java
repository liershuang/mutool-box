package com.mutool.javafx.core.exception;

/**
 * 描述：基础错误异常接口<br>
 * 作者：les<br>
 * 日期：2020/11/27 10:26<br>
 */
public interface BaseErrorInfoInterface {

    /** 错误码*/
    String getErrorCode();

    /** 错误描述*/
    String getErrorMsg();

}
