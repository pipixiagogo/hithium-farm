package com.bugull.hithiumfarmweb.http.entity;

import com.bugull.mongo.SimpleEntity;
import com.bugull.mongo.annotations.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
public class UploadEntity extends SimpleEntity {

    private List<String> deviceNames;

    private String type;

    /**
     * 多台设备可能有多张图片  生成多个URL
     *
     * 单台电台 只有一张图片 生成一个或多个URL
     *
     * URL 生成规则  stationId+
     */
    private List<String> imgUrl;

    /**
     * 是否图片绑定设备  默认没有确定没有绑定
     */
    private Boolean bindImg=false;

    private List<String> originalFilename;

    private Date uploadTime;

    private List<String> imgOfFullPath;


}
