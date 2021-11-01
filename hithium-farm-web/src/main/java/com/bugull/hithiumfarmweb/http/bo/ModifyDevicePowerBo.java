package com.bugull.hithiumfarmweb.http.bo;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ModifyDevicePowerBo {

    private String deviceNames;

    private Map<Integer,List<TimeOfPowerBo>> powerOfTime;
}
