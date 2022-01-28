package com.bugull.hithiumfarmweb.http.entity;

import lombok.Data;

import java.util.List;

@Data
public class Pccs  {

    private String id;
    private  Integer pccsId;

    private String name;

    private String description;

    private Integer stationId;

    private String deviceName;

    private List<String> equipmentIds;
}
