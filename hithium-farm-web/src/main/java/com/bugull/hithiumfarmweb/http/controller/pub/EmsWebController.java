package com.bugull.hithiumfarmweb.http.controller.pub;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.bugull.hithiumfarmweb.common.Const.CACHEMAP;

@RestController
@RequestMapping("/EmsWebService")
public class EmsWebController {


    /**
     * 设备状态信息
     * @param deviceName
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/GetEquipmentRealTimeData")
    public JSONObject GetEquipmentRealTimeData(@RequestParam(value = "deviceName")String deviceName){
        JSONObject result    = (JSONObject) CACHEMAP.get(deviceName + "-REALDATA");
        if(result != null){
            return result;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("无结果请稍后在试","");
        return jsonObject;
    }

    /**
     * 设备列表信息
     */
    @RequestMapping(method = RequestMethod.GET,value = "/GetAllEquipmentData")
    public JSONObject GetAllEquipmentData(@RequestParam(value = "deviceName")String deviceName){
        JSONObject result = (JSONObject) CACHEMAP.get(deviceName + "-DEVICEINFO");
        if(result != null){
            return result;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("无结果请稍后在试","");
        return jsonObject;
    }

    /**
     * 告警日志
     * @param deviceName
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/GetAlarmRealTimeData")
    public JSONArray GetAlarmRealTimeData(@RequestParam(value = "deviceName")String deviceName){
        JSONArray jsonArray= (JSONArray) CACHEMAP.get(deviceName+"-BREAKDOWNLOG");
        if(jsonArray.size()>0){
            return jsonArray;
        }
        JSONArray jsonArray2 = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("无结果请稍后在试","");
        return jsonArray2;
    }
}
