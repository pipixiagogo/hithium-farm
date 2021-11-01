package com.bugull.hithiumfarmweb.http.bo;

import lombok.Data;

import java.util.List;

@Data
public class ModifyDeviceBo {

    private String deviceNames;

    private List<TimeOfPriceBo>priceOfTime;
}
