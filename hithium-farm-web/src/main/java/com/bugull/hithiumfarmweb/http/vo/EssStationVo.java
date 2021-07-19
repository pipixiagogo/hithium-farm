package com.bugull.hithiumfarmweb.http.vo;

import com.bugull.hithiumfarmweb.http.entity.Device;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class EssStationVo {
    @ApiModelProperty(name = "id", value = "电站ID")
    private String id;
    @ApiModelProperty(name = "stationName", value = "电站名称")
    private String stationName;
    @ApiModelProperty(name = "code", value = "电站编码")
    private String code;
    @ApiModelProperty(name = "deviceList", value = "电站下的设备列表信息")
    private List<DeviceVo> deviceVoList;
    @ApiModelProperty(name = "areaCity", value = "设备所处位置")
    private String areaCity;
    @ApiModelProperty(name = "deviceNameList", value = "电站下的设备名称")
    private List<String> deviceNameList;

    public Integer getNumberOfDevice() {
        return deviceNameList.size();
    }
}
