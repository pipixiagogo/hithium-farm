package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class EssStation extends SimpleEntity {
    private String stationName;
    private String code;
    private List<String> deviceNameList;
    private String province;
    private String city;
    private Integer numberOfDevice;
    /**
     * 电站背景图片   电站的图片url、储能站电气图url 地址
     */
    private String uploadImgId;

    private List<String> stationImgUrls;

}
