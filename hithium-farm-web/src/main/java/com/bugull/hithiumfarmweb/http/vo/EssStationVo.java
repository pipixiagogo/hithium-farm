package com.bugull.hithiumfarmweb.http.vo;

import lombok.Data;

import java.util.List;

@Data
public class EssStationVo {
    private String id;
    private String stationName;
    private List<DeviceInfoVo> deviceInfoVos;
    private String areaCity;
    private List<String> deviceNameList;
    private List<String> stationImgIdWithUrls;
    private String longitude;
    private String latitude;
    public Integer getNumberOfDevice() {
        return deviceNameList.size();
    }
}
