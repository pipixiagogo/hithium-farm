package com.bugull.hithiumfarmweb.http.controller.pri;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.bo.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.excelBo.AirConditionExcelBo;
import com.bugull.hithiumfarmweb.http.excelBo.BamsExcelBo;
import com.bugull.hithiumfarmweb.http.service.RealTimeDataService;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.HashMap;
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

    @ApiOperation(value = "ammeter电表数据查询", httpMethod = "GET")
    @RequestMapping(value = "/ammeterDataQuery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<AmmeterDataDic>> ammeterDataQuery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.ammeterDataQuery(params);
    }

    @ApiOperation(value = "Bams数据查询", httpMethod = "GET")
    @RequestMapping(value = "/bamsDataquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<BamsDataDicBA>> bamsDataquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.bamsDataquery(params);
    }

    @ApiOperation(value = "Bms单元温度数据查询", httpMethod = "GET")
    @RequestMapping(value = "/bmsTempDataquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<BmsCellTempDataDic>> bmsTempDataquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.bmsTempDataquery(params);
    }

    @ApiOperation(value = "Bms单元电压数据查询", httpMethod = "GET")
    @RequestMapping(value = "/bmsVoltDataquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<BmsCellVoltDataDic>> bmsVoltDataquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.bmsVoltDataquery(params);
    }

    @ApiOperation(value = "Bcu电池簇状态数据查询", httpMethod = "GET")
    @RequestMapping(value = "/bcuDataquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<BcuDataDicBCU>> bcuDataquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.bcuDataquery(params);
    }

    @ApiOperation(value = "pcs舱主机状态数据查询", httpMethod = "GET")
    @RequestMapping(value = "/pcsCabinetquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<PcsCabinetDic>> pcsCabinetquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.pcsCabinetquery(params);
    }

    @ApiOperation(value = "pcs通道状态数据查询", httpMethod = "GET")
    @RequestMapping(value = "/pcsChannelquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<PcsChannelDic>> pcsChannelquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.pcsChannelquery(params);
    }

    @ApiOperation(value = "空调数据查询", httpMethod = "GET")
    @RequestMapping(value = "/airConditionquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<AirConditionDataDic>> airConditionquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.airConditionquery(params);
    }

    @ApiOperation(value = "温度计数据查询", httpMethod = "GET")
    @RequestMapping(value = "/temperatureMeterquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<TemperatureMeterDataDic>> temperatureMeterquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.temperatureMeterquery(params);
    }

    @ApiOperation(value = "消防控制数据查询", httpMethod = "GET")
    @RequestMapping(value = "/fireControlquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<FireControlDataDic>> fileControlquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.fileControlquery(params);
    }

    @ApiOperation(value = "ups后备电源数据查询", httpMethod = "GET")
    @RequestMapping(value = "/upsPowerquery", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<UpsPowerDataDic>> upsPowerquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.upsPowerquery(params);
    }

    /**
     * BMS最近一次 平均温度 最高温度 最低温度
     */
    @ApiOperation(value = "BMS最近一次 温度 湿度", httpMethod = "GET")
    @RequestMapping(value = "/bmsTempMsg", method = RequestMethod.GET)
    @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class)
    public ResHelper<List<BmsTempMsgBo>> bmsTempMsg(@ApiIgnore @RequestParam(value = "deviceName", required = false) String deviceName) {
        if (StringUtils.isEmpty(deviceName)) {
            return ResHelper.pamIll();
        }
        return realTimeDataService.bmsTempMsg(deviceName);
    }

    /**
     * 获取BAMS最新一条数据更新充放电电量
     */
    @ApiOperation(value = "BAMS最新一条数据更新充放电电量", httpMethod = "GET")
    @RequestMapping(value = "/bamsLatestData", method = RequestMethod.GET)
    @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class)
    public ResHelper<BamsLatestBo> bamsLatestData(@ApiIgnore @RequestParam(value = "deviceName", required = false) String deviceName) {
        if (StringUtils.isEmpty(deviceName)) {
            return ResHelper.pamIll();
        }
        return realTimeDataService.bamsLatestData(deviceName);
    }

    /**
     * BCU簇数据 状态查询
     */
    @ApiOperation(value = "集装箱所有电池簇实时状态查询", httpMethod = "GET")
    @RequestMapping(value = "/bcuDataqueryByName", method = RequestMethod.GET)
    @ApiImplicitParams({@ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识")})
    public ResHelper<List<BcuDataBo>> bcuDataqueryByName(@ApiIgnore @RequestParam(value = "deviceName", required = false) String deviceName) {
        if (StringUtils.isEmpty(deviceName)) {
            return ResHelper.pamIll();
        }
        return realTimeDataService.bcuDataqueryByName(deviceName);
    }

    /**
     * 根据电池簇获取对应 电池电压 跟温度信息
     */
    @ApiOperation(value = "集装箱所有电池簇对应全部电池详细信息查询")
    @RequestMapping(value = "/bmscellDataQuery", method = RequestMethod.GET)
    @ApiImplicitParams({@ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识"),})
    public ResHelper<List<BmsCellDataBo>> bmscellDataQuery(@ApiIgnore @RequestParam(value = "deviceName", required = false) String deviceName) {
        if (StringUtils.isEmpty(deviceName)) {
            return ResHelper.pamIll();
        }
        return realTimeDataService.bmscellDataQuery(deviceName);
    }

    @ApiOperation(value = "集装箱信息设备查询")
    @RequestMapping(value = "/stationInformationQuery", method = RequestMethod.GET)
    @ApiImplicitParams({@ApiImplicitParam(name = "deviceName", required = true, paramType = "query", value = "设备码  唯一标识"),
            @ApiImplicitParam(name = "equipmentId", required = true, paramType = "query", value = "设备ID")})
    public ResHelper<Object> stationInformationQuery(@ApiIgnore @RequestParam(value = "deviceName", required = false) String deviceName,
                                                     @ApiIgnore @RequestParam(value = "equipmentId", required = false) Integer equipmentId) {
        return realTimeDataService.stationInformationQuery(deviceName, equipmentId);
    }

    @ApiOperation(value = "导出电池相关数据 默认为当天数据内容   可选时间 只能为一天的数据")
    @RequestMapping(value = "/exportBamsData", method = RequestMethod.GET)
    @ApiImplicitParams({@ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识"),
            @ApiImplicitParam(name = "time", required = false, paramType = "query", value = "时间日期"),
            @ApiImplicitParam(name = "fileName", required = false, paramType = "query", value = "文件名称"),
            @ApiImplicitParam(name = "type", required = false, paramType = "query", value = "导出数据类型 0:pcs数据 1:电池堆数据 2:电表数据 3:消防、空调 ")})
    public void exportBamsData(HttpServletRequest request, HttpServletResponse response,
                               @ApiIgnore @RequestParam(value = "deviceName", required = false) String deviceName,
                               @ApiIgnore @RequestParam(value = "time", required = false) String time,
                               @ApiIgnore @RequestParam(value = "fileName", required = false) String fileName,
                               @ApiIgnore @RequestParam(value = "type", required = false) Integer type) throws IOException {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        ExcelWriter writer = null;
        try {
            if (StringUtils.isEmpty(deviceName)) {
                throw new RuntimeException("设备名称为空");
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            if (StringUtils.isEmpty(fileName)) {
                fileName = "设备数据导出报表";
            }
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            writer = EasyExcel.write(response.getOutputStream()).build();
            realTimeDataService.exportStationOfBattery(writer, deviceName, time,type);
        } catch (Exception e) {
            // 重置response
            e.printStackTrace();
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = new HashMap<String, String>();
            map.put("status", "failure");
            map.put("message", "下载文件失败" + e.getMessage());
            response.getWriter().println(JSON.toJSONString(map));
        } finally {
            if (writer != null) {
                writer.finish();
            }
        }
    }
}
