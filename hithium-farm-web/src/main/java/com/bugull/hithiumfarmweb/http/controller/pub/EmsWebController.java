package com.bugull.hithiumfarmweb.http.controller.pub;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bugull.hithiumfarmweb.config.RedisPoolUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;

@RestController
@RequestMapping("/EmsWebService")
public class EmsWebController {


    @Resource
    private RedisPoolUtil redisPoolUtil;

    /**
     * 设备状态信息  需要有设备才有这些状态
     *
     * @param deviceName
     * @return
     */
    @ApiOperation(value = "获取设备状态信息")
    @RequestMapping(method = RequestMethod.GET, value = "/GetEquipmentRealTimeData")
    public JSONObject GetEquipmentRealTimeData(@RequestParam(value = "deviceName",required = false) String deviceName) {
        Jedis jedis = redisPoolUtil.getJedis();
        try {
            String device="REALDATA-" + deviceName;
            String realData = jedis.get(device);
            if (!StringUtils.isEmpty(realData)) {
                JSONObject realDataJson = JSONObject.parseObject(realData);
                if (realDataJson != null) {
                    return realDataJson;
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("无结果请稍后在试", "");
            return jsonObject;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 设备列表信息
     */
    @ApiOperation(value = "获取设备列表信息")
    @RequestMapping(method = RequestMethod.GET, value = "/GetAllEquipmentData")
    public JSONObject GetAllEquipmentData(@RequestParam(value = "deviceName",required = false) String deviceName) {
        Jedis jedis = redisPoolUtil.getJedis();
        try {
            String device="DEVICEINFO-" + deviceName;
            String deviceInfoStr = jedis.get(device);
            if (!StringUtils.isEmpty(deviceInfoStr)) {
                JSONObject deviceInfoJson = JSONObject.parseObject(deviceInfoStr);
                if (deviceInfoJson != null) {
                    return deviceInfoJson;
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("无结果请稍后在试", "");
            return jsonObject;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 告警日志
     *
     * @param deviceName
     * @return
     */
    @ApiOperation("获取告警日志信息")
    @RequestMapping(method = RequestMethod.GET, value = "/GetAlarmRealTimeData")
    public JSONArray GetAlarmRealTimeData(@RequestParam(value = "deviceName",required = false) String deviceName) {
        Jedis jedis = redisPoolUtil.getJedis();
        try {
            String breakDownLogStr  = jedis.get("BREAKDOWNLOG-" + deviceName);
            if(!StringUtils.isEmpty(breakDownLogStr)){
                JSONArray breakDownLogJson   = JSONArray.parseArray(breakDownLogStr);
                if(!CollectionUtils.isEmpty(breakDownLogJson) && breakDownLogJson.size()>0){
                    return breakDownLogJson;
                }
            }
            JSONArray resultArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("无结果请稍后在试", "");
            resultArray.add(jsonObject);
            return resultArray;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }

    }
}
