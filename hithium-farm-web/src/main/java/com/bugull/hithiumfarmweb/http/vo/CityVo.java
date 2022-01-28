package com.bugull.hithiumfarmweb.http.vo;


import com.bugull.hithiumfarmweb.http.entity.DeviceAreaEntity;
import lombok.Data;

import java.util.List;

@Data
public class CityVo {
    private String city;

    private Long num;


    private List<DeviceAreaEntity> deviceAreaEntities;

}
