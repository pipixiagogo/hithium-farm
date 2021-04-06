package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.common.validator.ValidatorUtils;
import com.bugull.hithiumfarmweb.common.validator.group.AddGroup;
import com.bugull.hithiumfarmweb.http.bo.ConnetDeviceBo;
import com.bugull.hithiumfarmweb.http.entity.Device;
import com.bugull.hithiumfarmweb.http.entity.User;
import com.bugull.hithiumfarmweb.http.service.ConnetDeviceService;
import com.bugull.hithiumfarmweb.http.service.DeviceService;
import com.bugull.hithiumfarmweb.http.vo.DeviceVo;
import com.bugull.hithiumfarmweb.http.vo.ProvinceVo;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;

/**
 * 储能站模块
 */
@RestController
@RequestMapping(value = "/device")
public class DeviceController extends AbstractController{

    @Resource
    private DeviceService deviceService;
    @Resource
    private ConnetDeviceService connetDeviceService;

    /**
     * 添加EMQ连接白名单设备
     * @return
     */
    @RequestMapping(value = "/registerConnetDevice",method = RequestMethod.POST)
    @ApiOperation(value = "添加EMQ连接白名单设备",response = ResHelper.class)
    @ApiImplicitParam(name = "connetBo", value = "设备白名单实体类", required = true, paramType = "body", dataTypeClass = ConnetDeviceBo.class, dataType = "ConnetDeviceBo")
    public ResHelper<Void> registerConnetDevice(@RequestBody  ConnetDeviceBo connetBo){
        ValidatorUtils.validateEntity(connetBo, AddGroup.class);
        return connetDeviceService.registerConnetDevice(connetBo);
    }

    @RequestMapping(value = "/aggrationArea",method = RequestMethod.GET)
    @ApiOperation(value = "统计设备定位信息 地区+数量",httpMethod = "GET",response = ResHelper.class)
    public ResHelper<List<ProvinceVo>> aggrationArea(){
        return deviceService.aggrationArea();
    }

    @RequestMapping(value = "/detailArea",method = RequestMethod.GET)
    @ApiOperation(value = "根据城市查询对应设备的详细经纬度",httpMethod = "GET",response = ResHelper.class)
    @ApiImplicitParam(name = "city", required = true,paramType = "query",value = "城市名称",dataType = "String",dataTypeClass = String.class)
    public ResHelper<List<Device>> detailArea(@ApiIgnore @RequestParam(value = "city",required = true) String city){
        if(StringUtils.isEmpty(city)){
            return ResHelper.pamIll();
        }
        return deviceService.queryDetailAreaByCity(city);
    }

    @RequestMapping(value = "/queryDevice",method = RequestMethod.GET)
    @ApiOperation(value = "设备列表",httpMethod = "GET")
    @ApiImplicitParam(name = "deviceName", required = true,paramType = "query",value = "设备名称",dataType = "String",dataTypeClass = String.class)
    public ResHelper<DeviceVo> queryDevice(@ApiIgnore @RequestParam(value = "deviceName")String deviceName){
        return deviceService.query(deviceName);
    }
}
