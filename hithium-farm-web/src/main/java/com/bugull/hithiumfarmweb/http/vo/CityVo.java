package com.bugull.hithiumfarmweb.http.vo;


import com.bugull.hithiumfarmweb.http.vo.entity.DeviceAreaEntity;
import lombok.Data;

import java.util.List;

@Data
public class CityVo {
    private String city;

    private Integer num;


    private List<DeviceAreaEntity> deviceAreaEntities;

}
