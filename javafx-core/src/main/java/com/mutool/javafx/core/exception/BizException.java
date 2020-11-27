package com.mutool.javafx.core.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2020/11/27 10:29<br>
 */
@Getter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = -2838298431677143330L;

    /** 错误码 */
    protected String errorCode;
    /** 错误信息 */
    protected String errorMsg;

    public BizException() {
        super();
    }

    public BizException(BaseErrorInfoInterface errorInfoInterface) {
        super(errorInfoInterface.getErrorMsg());
        this.errorCode = errorInfoInterface.getErrorCode();
        this.errorMsg = errorInfoInterface.getErrorMsg();
    }

    public BizException(BaseErrorInfoInterface errorInfoInterface, Throwable cause) {
        super(errorInfoInterface.getErrorMsg(), cause);
        this.errorCode = errorInfoInterface.getErrorCode();
        this.errorMsg = errorInfoInterface.getErrorMsg();
    }

    public BizException(String errorMsg) {
        super(errorMsg);
        this.errorCode = ErrorCodeEnum.BUSIN_ERROR.getErrorCode();
        this.errorMsg = errorMsg;
    }

    public BizException(String errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BizException(String errorCode, String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
