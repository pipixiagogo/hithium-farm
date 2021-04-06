package com.bugull.hithiumfarmweb.common.exception;

public class ParamsValidateException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;

    public ParamsValidateException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public ParamsValidateException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public ParamsValidateException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public ParamsValidateException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
