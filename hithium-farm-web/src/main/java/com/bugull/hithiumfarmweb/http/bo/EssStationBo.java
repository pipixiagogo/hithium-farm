package com.bugull.hithiumfarmweb.http.bo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class EssStationBo {
    private String stationName;
    private String code;
    private List<String> deviceNameList=new ArrayList<>();
    private String province;
    private String city;
}
