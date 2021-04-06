package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.bo.BmsTempMsgBo;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.service.RealTimeDataService;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 储能集装箱实时数据模块
 */
@RestController
@RequestMapping(value = "/realTimeData")
public class RealTimeDataController extends AbstractController {

    @Resource
    private RealTimeDataService realTimeDataService;

    @ApiOperation(value = "ammeter电表数据查询")
    @RequestMapping(value = "/ammeterDataQuery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = true, paramType = "query", value = "设备名称", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<AmmeterDataDic>> ammeterDataQuery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.ammeterDataQuery(params);
    }

    @ApiOperation(value = "Bams数据查询")
    @RequestMapping(value = "/bamsDataquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = true, paramType = "query", value = "设备名称", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<BamsDataDicBA>> bamsDataquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.bamsDataquery(params);
    }

    @ApiOperation(value = "Bms单元温度数据查询")
    @RequestMapping(value = "/bmsTempDataquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = true, paramType = "query", value = "设备名称", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<BmsCellTempDataDic>> bmsTempDataquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.bmsTempDataquery(params);
    }

    @ApiOperation(value = "Bms单元电压数据查询")
    @RequestMapping(value = "/bmsVoltDataquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = true, paramType = "query", value = "设备名称", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<BmsCellVoltDataDic>> bmsVoltDataquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.bmsVoltDataquery(params);
    }

    @ApiOperation(value = "Bcu电池簇状态数据查询")
    @RequestMapping(value = "/bcuDataquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = true, paramType = "query", value = "设备名称", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<BcuDataDicBCU>> bcuDataquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.bcuDataquery(params);
    }

    @ApiOperation(value = "pcs舱主机状态数据查询")
    @RequestMapping(value = "/pcsCabinetquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = true, paramType = "query", value = "设备名称", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<PcsCabinetDic>> pcsCabinetquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.pcsCabinetquery(params);
    }

    @ApiOperation(value = "pcs通道状态数据查询")
    @RequestMapping(value = "/pcsChannelquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = true, paramType = "query", value = "设备名称", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<PcsChannelDic>> pcsChannelquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.pcsChannelquery(params);
    }

    @ApiOperation(value = "空调数据查询")
    @RequestMapping(value = "/airConditionquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = true, paramType = "query", value = "设备名称", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<AirConditionDataDic>> airConditionquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.airConditionquery(params);
    }

    @ApiOperation(value = "温度计数据查询")
    @RequestMapping(value = "/temperatureMeterquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = true, paramType = "query", value = "设备名称", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<TemperatureMeterDataDic>> temperatureMeterquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.temperatureMeterquery(params);
    }

    @ApiOperation(value = "消防控制数据查询")
    @RequestMapping(value = "/fireControlquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = true, paramType = "query", value = "设备名称", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<FireControlDataDic>> fileControlquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.fileControlquery(params);
    }

    @ApiOperation(value = "ups后备电源数据查询")
    @RequestMapping(value = "/upsPowerquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = true, paramType = "query", value = "设备名称", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<UpsPowerDataDic>> upsPowerquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.upsPowerquery(params);
    }

    /**
     * BMS最近一次 平均温度 最高温度 最低温度
     *
     */
    @ApiOperation(value = "BMS最近一次 平均温度 最高温度 最低温度")
    @RequestMapping(value = "/bmsTempMsg", method = RequestMethod.GET)
    @ApiImplicitParam(name = "deviceName", required = true, paramType = "query", value = "设备名称", dataType = "String", dataTypeClass = String.class)
    public ResHelper<List<BmsTempMsgBo>> bmsTempMsg(@ApiIgnore @RequestParam(value = "deviceName", required = true) String deviceName) {
        if (StringUtils.isEmpty(deviceName)) {
            return ResHelper.error("设备名称不能为空");
        }
        return realTimeDataService.bmsTempMsg(deviceName);
    }


}
