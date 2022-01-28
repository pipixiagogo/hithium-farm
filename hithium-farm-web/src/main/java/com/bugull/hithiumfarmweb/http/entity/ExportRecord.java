package com.bugull.hithiumfarmweb.http.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ExportRecord  {
    private String id;
    private Date recordTime;
    private String filename;
    private Boolean exporting;
    private String failReason;


}
