package com.bugull.hithiumfarmweb.common.exception;

import lombok.Data;

@Data
public class ExcelExportWithoutDataException extends RuntimeException{
    private String msg;
    public ExcelExportWithoutDataException(String msg) {
        super(msg);
        this.msg = msg;
    }

}
