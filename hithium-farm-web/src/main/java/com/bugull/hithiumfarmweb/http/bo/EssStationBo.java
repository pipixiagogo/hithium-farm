package com.bugull.hithiumfarmweb.http.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EssStationBo {
    @ApiModelProperty(name ="stationName",value = "电站名称")
    private String stationName;
    @ApiModelProperty(name ="code",value = "电站编码")
    private String code;
    @ApiModelProperty(name ="deviceNameList",value = "电站下的设备名称")
    private List<String> deviceNameList=new ArrayList<>();
    @ApiModelProperty(name ="province",value = "电站省份")
    private String province;
    @ApiModelProperty(name ="city",value = "电站市区")
    private String city;
    @ApiModelProperty(name = "uploadImgId",value = "上传图片的ID")
    private String uploadImgId;

    @ApiModelProperty(name = "id",value = "电站唯一ID")
    private String id;

}
