package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class EssStation extends SimpleEntity {
    @ApiModelProperty(name ="stationName",value = "电站名称")
    private String stationName;
    @ApiModelProperty(name ="code",value = "电站编码")
    private String code;
    @ApiModelProperty(name ="deviceNameList",value = "电站下的设备名称")
    private List<String> deviceNameList;
    @ApiModelProperty(name ="province",value = "电站省份")
    private String province;
    @ApiModelProperty(name ="city",value = "电站市区")
    private String city;
    @ApiModelProperty(name ="numberOfDevice",value = "设备数量")
    private Integer numberOfDevice;
    /**
     * 电站背景图片   电站的图片url、储能站电气图url 地址
     */


}
