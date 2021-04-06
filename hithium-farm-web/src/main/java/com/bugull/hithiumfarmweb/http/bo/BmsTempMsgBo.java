package com.bugull.hithiumfarmweb.http.bo;

import lombok.Data;

import java.util.Date;

@Data
public class BmsTempMsgBo {

    private String name;

    private String temperature;

    private String humidity;

    private Date createDate;

}
