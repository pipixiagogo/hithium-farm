package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.CallNode;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@ToString
public class Equipment extends SimpleEntity {


    private Integer pccId;
    private Integer cubeId;
    private Integer stationId;
    private Integer cabinId;
    @ApiModelProperty(value = "是否启用")
    private boolean enabled;

    /**
     * 厂家
     */
    @ApiModelProperty(value = "厂家")
    private String manufacturer;
    /**
     * 型号
     */
    @ApiModelProperty(value = "型号")
    private String model;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "设备名称")
    private String name;
    @ApiModelProperty(value = "设备ID")
    private Integer equipmentId;

    private String deviceName;

}
