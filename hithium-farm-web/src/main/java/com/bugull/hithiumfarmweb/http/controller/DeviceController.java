package com.bugull.hithiumfarmweb.http.controller;

import com.bugull.hithiumfarmweb.annotation.SysLog;
import com.bugull.hithiumfarmweb.common.Page;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.bo.*;
import com.bugull.hithiumfarmweb.http.entity.Device;
import com.bugull.hithiumfarmweb.http.service.ConnetDeviceService;
import com.bugull.hithiumfarmweb.http.service.DeviceService;
import com.bugull.hithiumfarmweb.http.service.RealTimeDataService;
import com.bugull.hithiumfarmweb.http.vo.DeviceInfoVo;
import com.bugull.hithiumfarmweb.http.vo.DeviceVo;
import com.bugull.hithiumfarmweb.http.vo.ProvinceVo;
import com.bugull.hithiumfarmweb.utils.PatternUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.bugull.hithiumfarmweb.common.Const.NO_MODIFY_PERMISSION;
import static com.bugull.hithiumfarmweb.common.Const.NO_QUERY_PERMISSION;

/**
 * 储能站模块
 * *!!!--- 根据产线那台设备获取对应EquipmentID ---!!!
 * * 1:储能数据转发器
 * * 2:储能站消防主机
 * * 4:储能站电表1
 * * 5:储能站通信前置机
 * * 7:集装箱电表2
 * * 8:并网口电能质量
 * * 9:集装箱数据转发服务器1
 * * 10:集装箱通信前置机
 * * 11:集装箱配电系统1
 * * 12:集装箱电表1
 * * 13:集装箱UPS电源1
 * * 14:集装箱空调1
 * *15:集装箱PCS主控1
 * * 16:集装箱PCS通道1
 * * 17:集装箱PCS通道2
 * * 18-31:集装箱PCS通道16
 * * 32:集装箱_Pcs舱动环系统1
 * * 33:集装箱_Pcs舱摄像头1
 * * 34:集装箱_电池舱1动环系统1
 * * 35:集装箱_电池舱1摄像头1
 * * 36:集装箱电池堆
 * * 37-52:集装箱电池簇1-16
 * * 53:集装箱_电池舱2动环系统1
 * * 54:集装箱_电池舱2摄像头1
 * * 55:集装箱空调2
 */
@RestController
@RequestMapping(value = "/device")
public class DeviceController extends AbstractController {

    @Resource
    private DeviceService deviceService;
    @Resource
    private ConnetDeviceService connetDeviceService;
    @Resource
    private RealTimeDataService realTimeDataService;
    @Resource
    private PropertiesConfig propertiesConfig;

    /**
     * 添加EMQ连接白名单设备
     */
    @PostMapping(value = "/registerConnetDevice")
    public ResHelper<Void> registerConnetDevice(@RequestBody ConnetDeviceBo connetBo) {
//        ValidatorUtils.validateEntity(connetBo, AddGroup.class);
        return connetDeviceService.registerConnetDevice(connetBo);
    }

    @GetMapping(value = "/aggrationArea")
    public ResHelper<List<ProvinceVo>> aggregationArea() {
        return deviceService.aggregationArea();
    }

    @GetMapping(value = "/detailArea")
    public ResHelper<List<Device>> detailArea(@RequestParam(value = "city", required = false) String city) {
        if (StringUtils.isEmpty(city)) {
            return ResHelper.pamIll();
        }
        return deviceService.queryDetailAreaByCity(city);
    }

    @GetMapping(value = "/queryDevicesByDeviceName")
    public ResHelper<DeviceVo> queryDevice(@RequestParam(value = "deviceName", required = false) String deviceName) {
        if (StringUtils.isEmpty(deviceName)) {
            return ResHelper.pamIll();
        }
        if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
            if (!realTimeDataService.verificationDeviceName(deviceName, getUser())) {
                return ResHelper.success(NO_QUERY_PERMISSION);
            }
        }
        return ResHelper.success("", deviceService.query(deviceName));
    }

    @GetMapping(value = "/queryDevicesByPage")
    public ResHelper<Page<DeviceVo>> queryDevicesByPage(@RequestParam(value = "page", required = false) Integer page,
                                                        @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                        @RequestParam(value = "deviceName", required = false) String deviceName) {
        if (page == null || pageSize == null) {
            return ResHelper.pamIll();
        }
        return deviceService.queryDevicesByPage(page, pageSize, deviceName);
    }

    @RequiresPermissions(value = "sys:user")
    @GetMapping(value = "/queryDeviceWithoutBindByPage")
    public ResHelper<Page<DeviceInfoVo>> queryDeviceWithoutBindByPage(@RequestParam(value = "page", required = false) Integer page,
                                                                                    @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                                    @RequestParam(value = "deviceName", required = false) String deviceName) {
        if (page == null || pageSize == null) {
            return ResHelper.pamIll();
        }
        return deviceService.queryDeviceWithoutBindByPage(page, pageSize, deviceName);
    }

    @GetMapping(value = "/deviceAreaList")
    public ResHelper<Page<DeviceInfoVo>> deviceAreaList(@RequestParam(value = "page", required = false) Integer page,
                                                                      @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                      @RequestParam(value = "name", required = false) String name,
                                                                      @RequestParam(value = "country", required = false) String country,
                                                                      @RequestParam(value = "province", required = false) String province,
                                                                      @RequestParam(value = "city", required = false) String city) {
        if (page == null || pageSize == null) {
            return ResHelper.pamIll();
        }
        if (!propertiesConfig.verifyProvinceOfCity(province, city)) {
            return ResHelper.pamIll();
        }
        return deviceService.deviceAreaList(page, pageSize, name, country, province, city);
    }

    /**
     * @param modifyDeviceBo
     * @return
     */
    @SysLog("设置设备时间段电量单价")
    @RequiresPermissions(value = "sys:user")
    @PostMapping(value = "/modifyDeviceInfo")
    public ResHelper<Void> modifyDeviceInfo(@RequestBody ModifyDeviceBo modifyDeviceBo) {
        if (modifyDeviceBo != null) {
            if (!StringUtils.isEmpty(modifyDeviceBo.getDeviceNames())) {
                return deviceService.modifyDeviceInfo(modifyDeviceBo);
            }
        }
        return ResHelper.pamIll();
    }

    @GetMapping(value = "/selectPriceOfTime")
    public ResHelper<List<TimeOfPriceBo>> selectPriceOfTime(@RequestParam(value = "deviceName", required = false) String deviceName) {
        if (StringUtils.isEmpty(deviceName)) {
            return ResHelper.pamIll();
        }
        if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
            if (!realTimeDataService.verificationDeviceName(deviceName, getUser())) {
                return ResHelper.success(NO_QUERY_PERMISSION);
            }
        }
        return ResHelper.success("", deviceService.selectPriceOfTimeByDevice(deviceName));
    }

    @GetMapping(value = "/selectPriceOfPercentage")
    public ResHelper<Map<String, Object>> selectPriceOfPercentage(@RequestParam(value = "deviceName", required = false) String deviceName) {
        if (StringUtils.isEmpty(deviceName)) {
            return ResHelper.pamIll();
        }
        if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
            if (!realTimeDataService.verificationDeviceName(deviceName, getUser())) {
                return ResHelper.success(NO_QUERY_PERMISSION);
            }
        }
        return ResHelper.success("", deviceService.getPriceOfPercentAndTime(deviceName));
    }

    @GetMapping(value = "/queryDeviceByName")
    public ResHelper<DeviceInfoVo> selectDevice(@RequestParam(value = "deviceName", required = false) String deviceName) {
        if (StringUtils.isEmpty(deviceName)) {
            return ResHelper.pamIll();
        }
        if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
            if (!realTimeDataService.verificationDeviceName(deviceName, getUser())) {
                return ResHelper.success(NO_QUERY_PERMISSION);
            }
        }
        return ResHelper.success("", deviceService.queryDeviceName(deviceName));
    }

    /**
     * @param modifyDevicePowerBo
     * @return
     */
    @SysLog("设置设备时间段功率")
    @RequiresPermissions(value = "sys:user")
    @PostMapping(value = "/modifyDevicePowerInfo")
    public ResHelper<Void> modifyDevicePowerInfo(@RequestBody ModifyDevicePowerBo modifyDevicePowerBo) {
        if (modifyDevicePowerBo != null) {
            if (StringUtils.isEmpty(modifyDevicePowerBo.getDeviceNames())) {
                return ResHelper.pamIll();
            }
            String[] arrayDeviceName = modifyDevicePowerBo.getDeviceNames().split(",");
            if (arrayDeviceName == null || arrayDeviceName.length == 0) {
                return ResHelper.pamIll();
            }
            if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
                if (!deviceService.verificationDeviceNameList(Arrays.asList(arrayDeviceName), getUser())) {
                    return ResHelper.error(NO_MODIFY_PERMISSION);
                }
            }
            return deviceService.modifyDevicePowerInfo(modifyDevicePowerBo, Arrays.asList(arrayDeviceName));
        }
        return ResHelper.pamIll();
    }

    @RequiresPermissions(value = "sys:user")
    @SysLog("修改设备图片信息")
    @PostMapping(value = "/updateDeviceImg")
    public ResHelper<Void> updateDeviceImg(
            @RequestParam(value = "files",required = false) MultipartFile[] files,
            @RequestParam(value = "deviceName",required = false) String deviceName,
            @RequestParam(value = "imgFiles",required = false) List<String> imgFiles) throws Exception {
        if (StringUtils.isEmpty(deviceName)) {
            return ResHelper.pamIll();
        }
        if (files.length > 0) {
            for (MultipartFile file : files) {
                byte[] b = new byte[3];
                file.getInputStream().read(b, 0, b.length);
                String hexString = PatternUtil.bytesToHexString(b);
                String ooo = PatternUtil.checkType(hexString.toUpperCase());
                if (StringUtils.isEmpty(ooo)) {
                    return ResHelper.pamIll();
                }
                if (ooo.equals("0000")) {
                    return ResHelper.pamIll();
                }
            }
        }
        return deviceService.updateDeviceImg(files, deviceName, imgFiles);
    }


}
