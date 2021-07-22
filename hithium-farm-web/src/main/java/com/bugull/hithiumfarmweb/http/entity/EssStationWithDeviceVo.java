package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.hithiumfarmweb.http.vo.DeviceInfoVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class EssStationWithDeviceVo {

    @ApiModelProperty(name = "id",value = "电站ID")
    private String id;
    @ApiModelProperty(name ="stationName",value = "电站名称")
    private String stationName;
    @ApiModelProperty(name ="code",value = "电站编码")
    private String code;
    @ApiModelProperty(name ="deviceList",value = "电站下的设备列表信息")
    private List<DeviceInfoVo> deviceList;
    @ApiModelProperty(value = "设备所处位置")
    private String areaCity;





}
