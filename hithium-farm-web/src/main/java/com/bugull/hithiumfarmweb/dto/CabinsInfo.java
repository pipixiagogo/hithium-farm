package com.bugull.hithiumfarmweb.dto;

import lombok.Data;

import java.util.List;

/**
 * 储能舱信息
 */
@Data
public class CabinsInfo {
    private Integer cabinType;
    private Integer cubeId;
    private Integer fireFightId;
    private Integer fireFightRepAdd;
    private Integer diDoId;
    private String diDoInputAddrStr;
    private String diDoOutputAddrStr;
    private boolean enableDiDoOutput;
    private String airHostAddStr;
    private Integer temperatureControlPolicyId;
    private boolean temperatureControlPolicyEnabled;
    private boolean isDebugMode;
    private List<Integer> airHostAddList;
    private List<Integer> didoOutputAddrList;
    private List<Integer> didoInputAddrList;
    private String name;
    private Integer projectId;
    private Integer stationUniqueId;
    private boolean isCloud;

    private Integer id;
}
