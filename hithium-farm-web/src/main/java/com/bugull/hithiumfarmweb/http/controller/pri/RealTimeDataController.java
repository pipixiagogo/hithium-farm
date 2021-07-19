package com.bugull.hithiumfarmweb.http.controller.pri;

import com.alibaba.fastjson.JSON;
import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.common.exception.ExcelExportWithoutDataException;
import com.bugull.hithiumfarmweb.common.exception.ParamsValidateException;
import com.bugull.hithiumfarmweb.http.bo.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.service.EssStationService;
import com.bugull.hithiumfarmweb.http.service.ExcelExportService;
import com.bugull.hithiumfarmweb.http.service.RealTimeDataService;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.fs.BuguFS;
import com.bugull.mongo.fs.BuguFSFactory;
import com.bugull.mongo.fs.FileTypeUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bugull.hithiumfarmweb.common.Const.NO_MODIFY_PERMISSION;

/**
 * 储能集装箱实时数据模块
 */
@RestController
@RequestMapping(value = "/realTimeData")
public class RealTimeDataController extends AbstractController {

    public static final Logger log = LoggerFactory.getLogger(RealTimeDataController.class);
    @Resource
    private RealTimeDataService realTimeDataService;
    @Resource
    private ExcelExportService excelExportService;
    @Resource
    private EssStationService essStationService;

    @ApiOperation(value = "ammeter电表数据查询", httpMethod = "GET")
    @GetMapping(value = "/ammeterDataQuery")
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "equipmentId", required = false, paramType = "query", value = "设备ID 用于标识集装箱下哪种设备", dataType = "int", dataTypeClass = Integer.class)
    })
    public ResHelper<BuguPageQuery.Page<AmmeterDataDic>> ammeterDataQuery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.ammeterDataQuery(params, getUser());
    }

    @ApiOperation(value = "Bams数据查询", httpMethod = "GET")
    @GetMapping(value = "/bamsDataquery")
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "equipmentId", required = false, paramType = "query", value = "设备ID 用于标识集装箱下哪种设备", dataType = "int", dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<BamsDataDicBA>> bamsDataquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.bamsDataquery(params, getUser());
    }

    @ApiOperation(value = "Bms单元温度数据查询", httpMethod = "GET")
    @GetMapping(value = "/bmsTempDataquery")
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "equipmentId", required = false, paramType = "query", value = "设备ID 用于标识集装箱下哪种设备", dataType = "int", dataTypeClass = Integer.class)
    })
    public ResHelper<BuguPageQuery.Page<BmsCellTempDataDic>> bmsTempDataquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.bmsTempDataquery(params, getUser());
    }

    @ApiOperation(value = "Bms单元电压数据查询", httpMethod = "GET")
    @GetMapping(value = "/bmsVoltDataquery")
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "equipmentId", required = false, paramType = "query", value = "设备ID 用于标识集装箱下哪种设备", dataType = "int", dataTypeClass = Integer.class)
    })
    public ResHelper<BuguPageQuery.Page<BmsCellVoltDataDic>> bmsVoltDataquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.bmsVoltDataquery(params, getUser());
    }

    @ApiOperation(value = "Bcu电池簇状态数据查询", httpMethod = "GET")
    @GetMapping(value = "/bcuDataquery")
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "equipmentId", required = false, paramType = "query", value = "设备ID 用于标识集装箱下哪种设备", dataType = "int", dataTypeClass = Integer.class)
    })
    public ResHelper<BuguPageQuery.Page<BcuDataDicBCU>> bcuDataquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.bcuDataquery(params, getUser());
    }

    @ApiOperation(value = "pcs舱主机状态数据查询", httpMethod = "GET")
    @GetMapping(value = "/pcsCabinetquery")
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "equipmentId", required = false, paramType = "query", value = "设备ID 用于标识集装箱下哪种设备", dataType = "int", dataTypeClass = Integer.class)
    })
    public ResHelper<BuguPageQuery.Page<PcsCabinetDic>> pcsCabinetquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.pcsCabinetquery(params, getUser());
    }

    @ApiOperation(value = "pcs通道状态数据查询", httpMethod = "GET")
    @GetMapping(value = "/pcsChannelquery")
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "equipmentId", required = false, paramType = "query", value = "设备ID 用于标识集装箱下哪种设备", dataType = "int", dataTypeClass = Integer.class)
    })
    public ResHelper<BuguPageQuery.Page<PcsChannelDic>> pcsChannelquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.pcsChannelquery(params, getUser());
    }

    @ApiOperation(value = "空调数据查询", httpMethod = "GET")
    @GetMapping(value = "/airConditionquery")
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "equipmentId", required = false, paramType = "query", value = "设备ID 用于标识集装箱下哪种设备", dataType = "int", dataTypeClass = Integer.class)
    })
    public ResHelper<BuguPageQuery.Page<AirConditionDataDic>> airConditionquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.airConditionquery(params, getUser());
    }

    @ApiOperation(value = "动环系统数据查询", httpMethod = "GET")
    @GetMapping(value = "/temperatureMeterquery")
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "equipmentId", required = false, paramType = "query", value = "设备ID 用于标识集装箱下哪种设备", dataType = "int", dataTypeClass = Integer.class)
    })
    public ResHelper<BuguPageQuery.Page<TemperatureMeterDataDic>> temperatureMeterquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.temperatureMeterquery(params, getUser());
    }

    @ApiOperation(value = "消防控制数据查询", httpMethod = "GET")
    @GetMapping(value = "/fireControlquery")
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "equipmentId", required = false, paramType = "query", value = "设备ID 用于标识集装箱下哪种设备", dataType = "int", dataTypeClass = Integer.class)
    })
    public ResHelper<BuguPageQuery.Page<FireControlDataDic>> fileControlquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.fileControlquery(params, getUser());
    }

    @ApiOperation(value = "ups后备电源数据查询", httpMethod = "GET")
    @GetMapping(value = "/upsPowerquery")
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "equipmentId", required = false, paramType = "query", value = "设备ID 用于标识集装箱下哪种设备", dataType = "int", dataTypeClass = Integer.class)
    })
    public ResHelper<BuguPageQuery.Page<UpsPowerDataDic>> upsPowerquery(@ApiIgnore @RequestParam Map<String, Object> params) {
        return realTimeDataService.upsPowerquery(params, getUser());
    }

    /**
     * BMS最近一次 平均温度 最高温度 最低温度
     */
    @ApiOperation(value = "BMS最近一次 温度 湿度", httpMethod = "GET")
    @GetMapping(value = "/bmsTempMsg")
    @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class)
    public ResHelper<List<BmsTempMsgBo>> bmsTempMsg(@ApiIgnore @RequestParam(value = "deviceName", required = false) String deviceName) {
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
                if (!realTimeDataService.verificationDeviceName(deviceName,getUser())) {
                    return ResHelper.success("");
                }
            }
            return realTimeDataService.bmsTempMsg(deviceName);
        }
        return ResHelper.pamIll();

    }

    /**
     * 获取BAMS最新一条数据更新充放电电量
     */
    @ApiOperation(value = "BAMS最新一条数据更新充放电电量", httpMethod = "GET")
    @GetMapping(value = "/bamsLatestData")
    @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class)
    public ResHelper<BamsLatestBo> bamsLatestData(@ApiIgnore @RequestParam(value = "deviceName", required = false) String deviceName) {
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
                if (!realTimeDataService.verificationDeviceName(deviceName,getUser())) {
                    return ResHelper.success("");
                }
            }
            return realTimeDataService.bamsLatestData(deviceName);
        }
        return ResHelper.pamIll();

    }

    /**
     * BCU簇数据 状态查询
     */
    @ApiOperation(value = "集装箱所有电池簇实时状态查询", httpMethod = "GET")
    @GetMapping(value = "/bcuDataqueryByName")
    @ApiImplicitParams({@ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识")})
    public ResHelper<List<BcuDataBo>> bcuDataqueryByName(@ApiIgnore @RequestParam(value = "deviceName", required = false) String deviceName) {
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
                if (!realTimeDataService.verificationDeviceName(deviceName,getUser())) {
                    return ResHelper.success("");
                }
            }
            return realTimeDataService.bcuDataqueryByName(deviceName);
        }
        return ResHelper.pamIll();
    }

    /**
     * 根据电池簇获取对应 电池电压 跟温度信息
     */
    @ApiOperation(value = "集装箱所有电池簇对应全部电池详细信息查询")
    @GetMapping(value = "/bmscellDataQuery")
    @ApiImplicitParams({@ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识"),})
    public ResHelper<List<BmsCellDataBo>> bmscellDataQuery(@ApiIgnore @RequestParam(value = "deviceName", required = false) String deviceName) {
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
                if (!realTimeDataService.verificationDeviceName(deviceName,getUser())) {
                    return ResHelper.success("");
                }
            }
            return realTimeDataService.bmscellDataQuery(deviceName);
        }
        return ResHelper.pamIll();
    }

    @ApiOperation(value = "集装箱信息设备查询")
    @GetMapping(value = "/stationInformationQuery")
    @ApiImplicitParams({@ApiImplicitParam(name = "deviceName", required = true, paramType = "query", value = "设备码  唯一标识"),
            @ApiImplicitParam(name = "equipmentId", required = true, paramType = "query", value = "设备ID")})
    public ResHelper<Object> stationInformationQuery(@ApiIgnore @RequestParam(value = "deviceName", required = false) String deviceName,
                                                     @ApiIgnore @RequestParam(value = "equipmentId", required = false) Integer equipmentId) {
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
                if (!realTimeDataService.verificationDeviceName(deviceName,getUser())) {
                    return ResHelper.success("");
                }
            }
            return realTimeDataService.stationInformationQuery(deviceName, equipmentId);
        }
        return ResHelper.pamIll();

    }

    @ApiOperation(value = "导出电池相关数据 默认为当天数据内容   可选时间 只能为一天的数据")
    @RequestMapping(value = "/exportBamsData", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识"),
            @ApiImplicitParam(name = "time", required = false, paramType = "query", value = "时间日期"),
            @ApiImplicitParam(name = "fileName", required = false, paramType = "query", value = "文件名称"),
            @ApiImplicitParam(name = "type", required = false, paramType = "query", value = "导出数据类型 0:pcs数据 1:电池堆数据 2:电表数据 3:消防、空调 ")})
    public void exportBamsData(HttpServletRequest request, HttpServletResponse response,
                               @ApiIgnore @RequestParam(value = "deviceName", required = false) String deviceName,
                               @ApiIgnore @RequestParam(value = "time", required = false) String time,
                               @ApiIgnore @RequestParam(value = "fileName", required = false) String fileName,
                               @ApiIgnore @RequestParam(value = "type", required = false) Integer type) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            if (StringUtils.isEmpty(time)) {
                throw new ParamsValidateException("时间参数为空");
            }
            if (!StringUtils.isEmpty(deviceName)) {
                if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
                    if (!realTimeDataService.verificationDeviceName(deviceName,getUser())) {
                        throw new ExcelExportWithoutDataException("该日期暂无数据");
                    }
                }
            }
            DBObject query = new BasicDBObject();
            query.put("filename", deviceName + "_" + time + "_" + type);
            BuguFS fs = BuguFSFactory.getInstance().create();
            GridFSDBFile f = fs.findOne(query);
            if (f != null) {
                f.getInputStream();
                int fileLength = (int) f.getLength();
                String ext = FileTypeUtil.getExtention(deviceName + "_" + time + "_" + type);
                response.setContentType(FileTypeUtil.getContentType(ext));
                response.setStatus(200);
                response.setContentLength(fileLength);
                f.writeTo(outputStream);
            } else {
                //TODO 变成生成两份 或者需要先存入数据库 在从数据库中取出来 后续优化使用
                excelExportService.exportStationOfBattery(deviceName, time, type, outputStream);
            }
        } catch (Exception e) {
            log.error("下载文件失败:{},时间为:{},设备名称:{},导出设备类型:{}", e.getMessage(), time, deviceName, excelExportService.getMsgByType(type));
            response.reset();
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
            response.setContentType("application/json; charset=utf-8");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = new HashMap<>();
            map.put("message", "下载文件失败:" + e.getMessage());
            response.getWriter().print(JSON.toJSONString(map));
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

}
