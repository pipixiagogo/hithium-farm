package com.bugull.hithiumfarmweb.http.vo;

import lombok.Data;

import java.util.List;

@Data
public class ProvinceVo {

    private String province;

    private Integer num;

    private List<CityVo> cityVoList;

}
