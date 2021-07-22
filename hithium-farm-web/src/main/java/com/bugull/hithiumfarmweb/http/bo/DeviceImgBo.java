package com.bugull.hithiumfarmweb.http.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceImgBo {
    @ApiModelProperty(name = "deviceNames",value = "设备码 多台设备 使用,分隔")
    private String deviceNames;
    @ApiModelProperty(name = "uploadImgId",value = "上传图片ID")
    private String uploadImgId;



}
