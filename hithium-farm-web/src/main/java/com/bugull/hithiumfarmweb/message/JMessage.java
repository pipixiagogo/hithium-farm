package com.bugull.hithiumfarmweb.message;

import com.alibaba.fastjson.JSONArray;
import com.bugull.hithiumfarmweb.enumerate.DataType;
import com.bugull.hithiumfarmweb.enumerate.ProjectType;
import lombok.Data;

import java.util.Map;

@Data
public class JMessage {

    public static final String TOPIC_SPLIT = "/";

    private String deviceName;
    private JSONArray payloadRealData;
    private String clientId;

    private String payloadMsg;

    private ProjectType projectType;

    private Map<String,Object> heads;

    private String topic;

    private DataType dataType;

}
