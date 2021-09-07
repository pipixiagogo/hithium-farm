package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.common.validator.ValidatorUtils;
import com.bugull.hithiumfarmweb.common.validator.group.AddGroup;
import com.bugull.hithiumfarmweb.http.bo.*;
import com.bugull.hithiumfarmweb.http.entity.Device;
import com.bugull.hithiumfarmweb.http.service.ConnetDeviceService;
import com.bugull.hithiumfarmweb.http.service.DeviceService;
import com.bugull.hithiumfarmweb.http.service.RealTimeDataService;
import com.bugull.hithiumfarmweb.http.vo.DeviceInfoVo;
import com.bugull.hithiumfarmweb.http.vo.DeviceVo;
import com.bugull.hithiumfarmweb.http.vo.ProvinceVo;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
    /**
     * 添加EMQ连接白名单设备
     */
    @PostMapping(value = "/registerConnetDevice")
    @ApiOperation(value = "添加EMQ连接白名单设备", response = ResHelper.class)
    @ApiImplicitParam(name = "connetBo", value = "设备白名单实体类", required = true, paramType = "body", dataTypeClass = ConnetDeviceBo.class, dataType = "ConnetDeviceBo")
    public ResHelper<Void> registerConnetDevice(@RequestBody ConnetDeviceBo connetBo) {
        ValidatorUtils.validateEntity(connetBo, AddGroup.class);
        return connetDeviceService.registerConnetDevice(connetBo);
    }

    @GetMapping(value = "/aggrationArea")
    @ApiOperation(value = "统计设备定位信息 地区+数量", httpMethod = "GET", response = ResHelper.class)
    public ResHelper<List<ProvinceVo>> aggrationArea() {
        return deviceService.aggrationArea(getUser());
    }

    @GetMapping(value = "/detailArea")
    @ApiOperation(value = "根据城市查询对应设备的详细经纬度", httpMethod = "GET", response = ResHelper.class)
    @ApiImplicitParam(name = "city", required = true, paramType = "query", value = "城市名称", dataType = "String", dataTypeClass = String.class)
    public ResHelper<List<Device>> detailArea(@ApiIgnore @RequestParam(value = "city", required = false) String city) {
        if (StringUtils.isEmpty(city)) {
            return ResHelper.pamIll();
        }
        return deviceService.queryDetailAreaByCity(city);
    }

    @GetMapping(value = "/queryDevicesByDeviceName")
    @ApiOperation(value = "设备列表 需传入设备码", httpMethod = "GET")
    @ApiImplicitParam(name = "deviceName", required = true, paramType = "query", value = "设备码", dataType = "String", dataTypeClass = String.class)
    public ResHelper<DeviceVo> queryDevice(@ApiIgnore @RequestParam(value = "deviceName", required = false) String deviceName) {
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
    @ApiOperation(value = "设备列表 进行分页", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "name", required = false, paramType = "query", value = "设备名称", dataType = "String", dataTypeClass = String.class)
    })
    public ResHelper<BuguPageQuery.Page<DeviceVo>> queryDeivcesByPage(@ApiIgnore @RequestParam Map<String, Object> params) {
        return deviceService.queryDevicesByPage(params, getUser());
    }
    @RequiresPermissions(value = "sys:user")
    @GetMapping(value = "/queryDeviceWithoutBindByPage")
    @ApiOperation(value = "未绑定设备列表 带分页 ", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "name", required = false, paramType = "query", value = "设备名称", dataType = "String", dataTypeClass = String.class)
    })
    public ResHelper<BuguPageQuery.Page<DeviceInfoVo>> queryDeviceWithoutBindByPage(@ApiIgnore @RequestParam Map<String, Object> params) {
        return deviceService.queryDeviceWithoutBindByPage(params);
    }

    @GetMapping(value = "/deviceAreaList")
    @ApiOperation(value = "设备分页定位、运行状态查询", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "name", required = false, paramType = "query", value = "设备名称", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "country", value = " 中国：china  外国： foreign", paramType = "query"),
            @ApiImplicitParam(name = "province", value = "省份", paramType = "query", required = false, dataType = "string", dataTypeClass = String.class),
            @ApiImplicitParam(name = "city", value = "市区", paramType = "query", required = false, dataTypeClass = String.class, dataType = "string")
    })
    public ResHelper<BuguPageQuery.Page<DeviceInfoVo>> deviceAreaList(@ApiIgnore @RequestParam Map<String, Object> params) {
        return deviceService.deviceAreaList(params, getUser());
    }

    /**
     * @param modifyDeviceBo
     * @return
     */
    @RequiresPermissions(value = "sys:user")
    @PostMapping(value = "/modifyDeviceInfo")
    @ApiOperation(value = "设置设备时间段电量单价", httpMethod = "POST")
    @ApiImplicitParam(name = "modifyDeviceBo", value = "设备修改实体类", required = true, paramType = "body", dataTypeClass = ModifyDeviceBo.class, dataType = "ModifyDeviceBo")
    public ResHelper<Void> modifyDeviceInfo(@RequestBody ModifyDeviceBo modifyDeviceBo) {
        if (modifyDeviceBo != null) {
            if (!StringUtils.isEmpty(modifyDeviceBo.getDeviceNames())) {
                return deviceService.modifyDeviceInfo(modifyDeviceBo, getUser());
            }
        }
        return ResHelper.pamIll();
    }

    @GetMapping(value = "/selectPriceOfTime")
    @ApiOperation(value = "查询设备时间段电量单价", httpMethod = "GET")
    @ApiImplicitParam(name = "deviceName", required = true, paramType = "query", value = "设备码", dataType = "String", dataTypeClass = String.class)
    public ResHelper<List<TimeOfPriceBo>> selectPriceOfTime(@ApiIgnore @RequestParam(value = "deviceName", required = false) String deviceName) {
        if (StringUtils.isEmpty(deviceName)) {
            return ResHelper.pamIll();
        }
        if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
            if (!realTimeDataService.verificationDeviceName(deviceName, getUser())) {
                return ResHelper.success(NO_QUERY_PERMISSION);
            }
        }
        return ResHelper.success("", deviceService.selectPriceOfTimeBydevice(deviceName));
    }

    @GetMapping(value = "/selectPriceOfPercentage")
    @ApiOperation(value = "查询设备 时间段 单价和功率的分布百分比", httpMethod = "GET")
    @ApiImplicitParam(name = "deviceName", required = true, paramType = "query", value = "设备码", dataType = "String", dataTypeClass = String.class)
    public ResHelper<Map<String, Object>> selectPriceOfPercentage(@ApiIgnore @RequestParam(value = "deviceName", required = false) String deviceName) {
        if (StringUtils.isEmpty(deviceName)) {
            return ResHelper.pamIll();
        }
        if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
            if (!realTimeDataService.verificationDeviceName(deviceName, getUser())) {
                return ResHelper.success(NO_QUERY_PERMISSION);
            }
        }
        return ResHelper.success("", deviceService.getPriceOfPercenAndTime(deviceName));
    }

    @GetMapping(value = "/queryDeviceByName")
    @ApiOperation(value = "查询单台设备的总收益、天收益以及充放电量、Pcs运行状态数据", httpMethod = "GET")
    @ApiImplicitParam(name = "deviceName", paramType = "query", value = "设备码", dataType = "String", dataTypeClass = String.class)
    public ResHelper<DeviceInfoVo> selectDevice(@ApiIgnore @RequestParam(value = "deviceName", required = false) String deviceName) {
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
    @RequiresPermissions(value = "sys:user")
    @PostMapping(value = "/modifyDevicePowerInfo")
    @ApiOperation(value = "设置设备时间段功率", httpMethod = "POST")
    @ApiImplicitParam(name = "modifyDevicePowerBo", value = "设备功率修改实体类", required = true, paramType = "body", dataTypeClass = ModifyDevicePowerBo.class, dataType = "ModifyDevicePowerBo")
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

    @PostMapping(value = "/saveDeviceImg")
    @ApiOperation(value = "修改设备图片信息", httpMethod = "POST")
    @ApiImplicitParam(name = "deviceImgBo", value = "修改设备图片信息", required = true, paramType = "body", dataTypeClass = DeviceImgBo.class, dataType = "DeviceImgBo")
    public ResHelper<Void> saveDeviceImg(@RequestBody DeviceImgBo deviceImgBo) {
        if (StringUtils.isEmpty(deviceImgBo.getDeviceNames())) {
            return ResHelper.pamIll();
        }
        String[] deviceNamesSplit = deviceImgBo.getDeviceNames().split(",");
        if (deviceNamesSplit == null || deviceNamesSplit.length == 0) {
            return ResHelper.pamIll();
        }
        return deviceService.saveDeviceImg(deviceImgBo);
    }


}
