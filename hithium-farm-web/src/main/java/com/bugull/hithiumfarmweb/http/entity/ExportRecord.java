package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class ExportRecord extends SimpleEntity {
    private Date recordTime;
    private String filename;
    private Boolean exporting;
    private String failReason;
}
