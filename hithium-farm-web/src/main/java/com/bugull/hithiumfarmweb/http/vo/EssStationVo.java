package com.bugull.hithiumfarmweb.http.vo;

import lombok.Data;

import java.util.List;

@Data
public class EssStationVo {
    private String id;
    private String stationName;
    private String code;
    private List<DeviceInfoVo> deviceInfoVos;
    private String areaCity;
    private List<String> deviceNameList;
    private List<String> stationImgUrls;
    private String uploadImgId;

    public Integer getNumberOfDevice() {
        return deviceNameList.size();
    }
}
