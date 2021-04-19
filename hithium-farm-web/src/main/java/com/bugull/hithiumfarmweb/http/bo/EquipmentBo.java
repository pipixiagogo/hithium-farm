package com.bugull.hithiumfarmweb.http.bo;

import lombok.Data;

import java.util.List;

@Data
public class EquipmentBo {
    private String id;
    private Integer equipmentId;
    private String name;
    private List<EquipmentBo> equipmentBo;
}
