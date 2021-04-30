package com.bugull.hithiumfarmweb.http.vo;

import lombok.Data;

import java.util.Date;

@Data
public class IncomeStatisticVo {
    private String income="0";

    private String province;

    private String city;
    private Date date=new Date();


}
