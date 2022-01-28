package com.bugull.hithiumfarmweb.http.controller;

import com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity.AlarmTime;
import com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity.CorpResponse;
import com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity.PowerInstructions;
import com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity.RemovePlanCurve;
import com.bugull.hithiumfarmweb.http.service.BreakDownLogService;
import com.bugull.hithiumfarmweb.http.service.DeviceService;
import com.bugull.hithiumfarmweb.http.service.RealTimeDataService;
import com.bugull.hithiumfarmweb.http.service.ThirdPartyService;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * 2022-01-27
 * TODO 第三方平台测试
 */
@RestController
@RequestMapping(value = "/access/")
public class ThirdPartyPlatformController {


    @Resource
    private DeviceService deviceService;
    @Resource
    private RealTimeDataService realTimeDataService;
    @Resource
    private BreakDownLogService breakDownLogService;
    @Resource
    private ThirdPartyService thirdPartyService;

    @GetMapping(value = "getAccessToken")
    public CorpResponse getAccessToken(@RequestParam(value = "corpAccessId") String corpAccessId,
                                       @RequestParam(value = "corpAccessSecret") String corpAccessSecret) {
        if (StringUtils.isEmpty(corpAccessId) || StringUtils.isEmpty(corpAccessSecret)) {
            return CorpResponse.error(0, "公司标识/秘钥为空");
        }
        Map<String, Object> result =
                thirdPartyService.createAccessToken(corpAccessSecret, corpAccessId);
        if (result.get("accessToken") != null) {
            return CorpResponse.ok(result);
        }
        return CorpResponse.error(0, "公司标识/秘钥错误");
    }

    @GetMapping(value = "allEquipmentData")
    public CorpResponse allEquipmentData(@RequestParam(value = "accessToken") String accessToken) {
        if (StringUtils.isEmpty(accessToken)) {
            return CorpResponse.error(0, "无效accessToken");
        }
        if (thirdPartyService.verifyAccessToken(accessToken)) {
            return CorpResponse.ok(deviceService.allEquipmentData(accessToken));
        }
        return CorpResponse.error(0, "无效accessToken");
    }

    @GetMapping(value = "equipmentRealTimeData")
    public CorpResponse equipmentRealTimeData(@RequestParam(value = "accessToken") String accessToken) {
        if (StringUtils.isEmpty(accessToken)) {
            return CorpResponse.error(0, "无效accessToken");
        }
        if (thirdPartyService.verifyAccessToken(accessToken)) {
            return CorpResponse.ok(realTimeDataService.equipmentRealTimeData(accessToken));
        }
        return CorpResponse.error(0, "无效accessToken");
    }

    @PostMapping(value = "equipmentAlarmData")
    public CorpResponse equipmentAlarmData(@RequestBody AlarmTime alarmTime) {
        if (alarmTime == null || alarmTime.getStartTime().getTime() > alarmTime.getEndTime().getTime()) {
            return CorpResponse.error(0, "参数错误");
        }
        if (StringUtils.isEmpty(alarmTime.getAccessToken())) {
            return CorpResponse.error(0, "无效accessToken");
        }
        if (thirdPartyService.verifyAccessToken(alarmTime.getAccessToken())) {
            return CorpResponse.ok(breakDownLogService.queryBreakDownLogByTime(alarmTime));
        }
        return CorpResponse.error(0, "无效accessToken");
    }


    @PostMapping(value = "setUpPlanCurve")
    public CorpResponse setUpPlanCurve(@RequestBody PowerInstructions powerInstructions) {
        if (StringUtils.isEmpty(powerInstructions.getAccessToken())) {
            return CorpResponse.error(0, "无效accessToken");
        }
        if (StringUtils.isEmpty(powerInstructions.getOrderId()) || StringUtils.isEmpty(powerInstructions.getDeviceName())) {
            return CorpResponse.error(0, "设备名称/指令标识为空");
        }
        if (CollectionUtils.isEmpty(powerInstructions.getPowerStrategy()) || powerInstructions.getPowerStrategy().size() != 288 || thirdPartyService.verifyOrderId(powerInstructions)) {
            return CorpResponse.error(0, "曲线功率输入错误/该指令标识orderId已存在");
        }
        if (powerInstructions.getStrategyTime() != null) {
            if (powerInstructions.getStrategyTime().getTime() < DateUtils.getStartTime(new Date(System.currentTimeMillis())).getTime()) {
                return CorpResponse.error(0, "曲线功率指定日期错误");
            }
        }
        if (thirdPartyService.verifyAccessToken(powerInstructions.getAccessToken())) {
            if (!thirdPartyService.verifyPlanCurve(powerInstructions)) {
                if (thirdPartyService.verifyDeviceName(powerInstructions.getAccessToken(), powerInstructions.getDeviceName())) {
                    return thirdPartyService.setUpPlanCurve(powerInstructions);
                }
                return CorpResponse.error(0, "验证deviceName失败");
            }
            return CorpResponse.error(0, "参数错误");
        }
        return CorpResponse.error(0, "无效accessToken");
    }

    @PostMapping(value = "deletePlanCurve")
    public CorpResponse deletePlanCurve(@RequestBody RemovePlanCurve removePlanCurve) {
        if (StringUtils.isEmpty(removePlanCurve.getAccessToken())) {
            return CorpResponse.error(0, "无效accessToken");
        }
        if (StringUtils.isEmpty(removePlanCurve.getOrderId()) || StringUtils.isEmpty(removePlanCurve.getDeviceName())) {
            return CorpResponse.error(0, "设备名称/指令标识为空");
        }
        if (removePlanCurve.getStrategyTime() != null) {
            if (removePlanCurve.getStrategyTime().getTime() < DateUtils.getStartTime(new Date(System.currentTimeMillis())).getTime()) {
                return CorpResponse.error(0, "曲线功率指定日期错误");
            }
        }
        if (thirdPartyService.verifyAccessToken(removePlanCurve.getAccessToken())) {
            if (!thirdPartyService.verifyRemoveOrderId(removePlanCurve)) {
                if (thirdPartyService.verifyDeviceName(removePlanCurve.getAccessToken(), removePlanCurve.getDeviceName())) {
                    return thirdPartyService.deletePlanCurve(removePlanCurve);
                }
                return CorpResponse.error(0, "验证deviceName失败");
            }
            return CorpResponse.error(0, "orderId已存在");
        }
        return CorpResponse.error(0, "无效accessToken");
    }

    @GetMapping(value = "readPlanCurve")
    public CorpResponse readPlanCurve(@RequestParam(value = "accessToken", required = false) String accessToken,
                                      @RequestParam(value = "deviceName", required = false) String deviceName,
                                      @RequestParam(value = "orderId", required = false) String orderId,
                                      @RequestParam(value = "strategyTime", required = false) String strategyTime) throws Exception {
        if (StringUtils.isEmpty(accessToken)) {
            return CorpResponse.error(0, "无效accessToken");
        }
        if (StringUtils.isEmpty(deviceName) || StringUtils.isEmpty(orderId)) {
            return CorpResponse.error(0, "设备名称/orderId为空");
        }
        if (thirdPartyService.verifyAccessToken(accessToken)) {
            if (thirdPartyService.verifyDeviceName(accessToken, deviceName)) {
                return thirdPartyService.readPlanCurve(deviceName, orderId, strategyTime);
            }
            return CorpResponse.error(0, "参数错误");
        }
        return CorpResponse.error(0, "无效accessToken");
    }


}
