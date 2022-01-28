package com.bugull.hithiumfarmweb.http.entity;

import lombok.Data;

import java.util.List;

@Data
public class EssStation {
    private String id;
    private String stationName;
    private String code;
    private List<String> deviceNameList;
    private String province;
    private String city;
    private Integer numberOfDevice;
    private List<String> stationImgIdWithUrls;
    private String longitude;
    private String latitude;

}
