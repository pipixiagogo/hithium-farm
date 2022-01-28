package com.bugull.hithiumfarmweb.http.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class ImgUpload  {

    private String type;
    private Date uploadTime=new Date(System.currentTimeMillis());
    private List<String> imgOfFullPath;
    private String originalFilename;

    private String imgId;

}
