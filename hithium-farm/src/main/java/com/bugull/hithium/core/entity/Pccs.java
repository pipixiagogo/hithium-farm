package com.bugull.hithium.core.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Pccs extends SimpleEntity {

    private  Integer pccsId;

    private String name;

    private String description;

    private Integer stationId;

    private String deviceName;

    private List<String> equipmentIds;
}
