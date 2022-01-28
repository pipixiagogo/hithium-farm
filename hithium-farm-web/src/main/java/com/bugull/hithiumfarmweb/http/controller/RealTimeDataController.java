package com.bugull.hithiumfarmweb.http.controller;

import com.alibaba.fastjson.JSON;
import com.bugull.hithiumfarmweb.annotation.SysLog;
import com.bugull.hithiumfarmweb.common.Page;
import com.bugull.hithiumfarmweb.common.exception.ExcelExportWithoutDataException;
import com.bugull.hithiumfarmweb.common.exception.ParamsValidateException;
import com.bugull.hithiumfarmweb.http.bo.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.service.ExcelExportService;
import com.bugull.hithiumfarmweb.http.service.RealTimeDataService;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bugull.hithiumfarmweb.common.Const.*;

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
    private GridFsTemplate gridFsTemplate;
    @Resource
    private GridFSBucket gridFSBucket;

    @RequiresPermissions(value = "sys:device")
    @GetMapping(value = "/ammeterDataQuery")
    public ResHelper<Page<AmmeterDataDic>> ammeterDataQuery(@RequestParam(value = "page", required = false) Integer page,
                                                                          @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                          @RequestParam(value = "deviceName", required = false) String deviceName,
                                                                          @RequestParam(value = "orderField", required = false) String orderField,
                                                                          @RequestParam(value = "order", required = false) Integer order,
                                                                          @RequestParam(value = "equipmentId", required = false) Integer equipmentId) {
        if (page == null || pageSize == null || equipmentId == null) {
            return ResHelper.pamIll();
        }
        if(equipmentId == null){
            return ResHelper.error("设备ID为空");
        }
        if (!ResHelper.checkOrder(order, orderField, AMMETER_DATADIC_TABLE)) {
            return ResHelper.pamIll();
        }
        return realTimeDataService.ammeterDataQuery(page, pageSize, deviceName, order, orderField, equipmentId, getUser());
    }

    @RequiresPermissions(value = "sys:device")
    @GetMapping(value = "/bamsDataquery")
    public ResHelper<Page<BamsDataDicBA>> bamDataquery( @RequestParam(value = "page", required = false) Integer page,
                                                                     @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                       @RequestParam(value = "deviceName", required = false) String deviceName,
                                                                    @RequestParam(value = "orderField", required = false) String orderField,
                                                                      @RequestParam(value = "order", required = false) Integer order,
                                                                     @RequestParam(value = "equipmentId", required = false) Integer equipmentId) {
        if (page == null || pageSize == null ) {
            return ResHelper.pamIll();
        }
        if(equipmentId == null){
            return ResHelper.error("设备ID为空");
        }
        if (!ResHelper.checkOrder(order, orderField, BAMS_DATADICDA_TABLE)) {
            return ResHelper.pamIll();
        }
        return realTimeDataService.bamDataQuery(page, pageSize, deviceName, orderField, order, equipmentId, getUser());
    }

    @RequiresPermissions(value = "sys:device")
    @GetMapping(value = "/bmsTempDataquery")
    public ResHelper<Page<BmsCellTempDataDic>> bmsTempDataquery(@RequestParam(value = "page", required = false) Integer page,
                                                                              @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                              @RequestParam(value = "deviceName", required = false) String deviceName,
                                                                              @RequestParam(value = "orderField", required = false) String orderField,
                                                                               @RequestParam(value = "order", required = false) Integer order,
                                                                             @RequestParam(value = "equipmentId", required = false) Integer equipmentId,
                                                                              @RequestParam(value = "startTime", required = false) Date startTime,
                                                                              @RequestParam(value = "endTime", required = false) Date endTime) {
        if (page == null || pageSize == null) {
            return ResHelper.pamIll();
        }
        if(equipmentId == null){
            return ResHelper.error("设备ID为空");
        }
        if (startTime != null && endTime != null) {
            if (startTime.getTime() > endTime.getTime()) {
                return ResHelper.pamIll();
            }
        }
        if (!ResHelper.checkOrder(order, orderField, BMSCELL_TEMP_DATADIC_TABLE)) {
            return ResHelper.pamIll();
        }
        return realTimeDataService.bmsTempDataQuery(page, pageSize, deviceName, order, orderField, equipmentId, startTime, endTime, getUser());
    }

    @RequiresPermissions(value = "sys:device")
    @GetMapping(value = "/bmsVoltDataquery")
    public ResHelper<Page<BmsCellVoltDataDic>> bmsVoltDataquery(@RequestParam(value = "page", required = false) Integer page,
                                                                              @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                              @RequestParam(value = "deviceName", required = false) String deviceName,
                                                                              @RequestParam(value = "orderField", required = false) String orderField,
                                                                              @RequestParam(value = "order", required = false) Integer order,
                                                                              @RequestParam(value = "equipmentId", required = false) Integer equipmentId) {
        if (page == null || pageSize == null) {
            return ResHelper.pamIll();
        }
        if(equipmentId == null){
            return ResHelper.error("设备ID为空");
        }
        if (!ResHelper.checkOrder(order, orderField, BMSCELL_VOL_DATADIC_TABLE)) {
            return ResHelper.pamIll();
        }
        return realTimeDataService.bmsVoltDataQuery(page, pageSize, deviceName, order, orderField, equipmentId, getUser());
    }

    @RequiresPermissions(value = "sys:device")
    @GetMapping(value = "/bcuDataquery")
    public ResHelper<Page<BcuDataDicBCU>> bcuDataquery(@RequestParam(value = "page", required = false) Integer page,
                                                                    @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                     @RequestParam(value = "deviceName", required = false) String deviceName,
                                                                     @RequestParam(value = "orderField", required = false) String orderField,
                                                                     @RequestParam(value = "order", required = false) Integer order,
                                                                     @RequestParam(value = "equipmentId", required = false) Integer equipmentId) {
        if (page == null || pageSize == null ) {
            return ResHelper.pamIll();
        }
        if(equipmentId == null){
            return ResHelper.error("设备ID为空");
        }
        if (!ResHelper.checkOrder(order, orderField, BCU_DATADIC_TABLE)) {
            return ResHelper.pamIll();
        }
        return realTimeDataService.bcuDataQuery(page, pageSize, deviceName, orderField, order, equipmentId, getUser());
    }

    @RequiresPermissions(value = "sys:device")
    @GetMapping(value = "/pcsCabinetquery")
    public ResHelper<Page<PcsCabinetDic>> pcsCabinetquery(@RequestParam(value = "page", required = false) Integer page,
                                                                       @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                        @RequestParam(value = "deviceName", required = false) String deviceName,
                                                                        @RequestParam(value = "orderField", required = false) String orderField,
                                                                        @RequestParam(value = "order", required = false) Integer order,
                                                                        @RequestParam(value = "equipmentId", required = false) Integer equipmentId) {
        if (page == null || pageSize == null) {
            return ResHelper.pamIll();
        }
        if(equipmentId == null){
            return ResHelper.error("设备ID为空");
        }
        if (!ResHelper.checkOrder(order, orderField, PCS_CABINETDIC_TABLE)) {
            return ResHelper.pamIll();
        }
        return realTimeDataService.pcsCabinetQuery(page, pageSize, deviceName, order, orderField, equipmentId, getUser());
    }

    @RequiresPermissions(value = "sys:device")
    @GetMapping(value = "/pcsChannelquery")
    public ResHelper<Page<PcsChannelDic>> pcsChannelquery(@RequestParam(value = "page", required = false) Integer page,
                                                                        @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                        @RequestParam(value = "deviceName", required = false) String deviceName,
                                                                        @RequestParam(value = "order", required = false) Integer order,
                                                                        @RequestParam(value = "orderField", required = false) String orderField,
                                                                        @RequestParam(value = "equipmentId", required = false) Integer equipmentId) {
        if (page == null || pageSize == null) {
            return ResHelper.pamIll();
        }
        if(equipmentId == null){
            return ResHelper.error("设备ID为空");
        }
        if (!ResHelper.checkOrder(order, orderField, PCS_CHANNELDIC_TABLE)) {
            return ResHelper.pamIll();
        }
        return realTimeDataService.pcsChannelQuery(page, pageSize, deviceName, order, orderField, equipmentId, getUser());
    }

    @RequiresPermissions(value = "sys:device")
    @GetMapping(value = "/airConditionquery")
    public ResHelper<Page<AirConditionDataDic>> airConditionQuery(@RequestParam(value = "page", required = false) Integer page,
                                                                               @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                                @RequestParam(value = "deviceName", required = false) String deviceName,
                                                                                @RequestParam(value = "orderField", required = false) String orderField,
                                                                                 @RequestParam(value = "order", required = false) Integer order,
                                                                                @RequestParam(value = "equipmentId", required = false) Integer equipmentId) {
        if (page == null || pageSize == null) {
            return ResHelper.pamIll();
        }
        if(equipmentId == null){
            return ResHelper.error("设备ID为空");
        }
        if (!ResHelper.checkOrder(order, orderField, AIRCONDITION_DATADIC_TABLE)) {
            return ResHelper.pamIll();
        }
        return realTimeDataService.airConditionQuery(page, pageSize, deviceName, orderField, order, equipmentId, getUser());
    }

    @RequiresPermissions(value = "sys:device")
    @GetMapping(value = "/temperatureMeterquery")
    public ResHelper<Page<TemperatureMeterDataDic>> temperatureMeterQuery(@RequestParam(value = "page", required = false) Integer page,
                                                                                        @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                                       @RequestParam(value = "deviceName", required = false) String deviceName,
                                                                                        @RequestParam(value = "orderField", required = false) String orderField,
                                                                                       @RequestParam(value = "order", required = false) Integer order,
                                                                                        @RequestParam(value = "equipmentId", required = false) Integer equipmentId) {
        if (page == null || pageSize == null) {
            return ResHelper.pamIll();
        }
        if(equipmentId == null){
            return ResHelper.error("设备ID为空");
        }
        if (!ResHelper.checkOrder(order, orderField, TEMPERATURE_METERDATADIC_TABLE)) {
            return ResHelper.pamIll();
        }
        return realTimeDataService.temperatureMeterQuery(page, pageSize, deviceName, order, orderField, equipmentId, getUser());
    }

    @RequiresPermissions(value = "sys:device")
    @GetMapping(value = "/fireControlquery")
    public ResHelper<Page<FireControlDataDic>> fileControlQuery(@RequestParam(value = "page", required = false) Integer page,
                                                                              @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                              @RequestParam(value = "deviceName", required = false) String deviceName,
                                                                              @RequestParam(value = "orderField", required = false) String orderField,
                                                                              @RequestParam(value = "order", required = false) Integer order,
                                                                              @RequestParam(value = "equipmentId", required = false) Integer equipmentId) {
        if (page == null || pageSize == null) {
            return ResHelper.pamIll();
        }
        if (!ResHelper.checkOrder(order, orderField, FIRECONTROL_DATADIC_TABLE)) {
            return ResHelper.pamIll();
        }
        if(equipmentId == null){
            return ResHelper.error("设备ID为空");
        }
        return realTimeDataService.fireControlQuery(page, pageSize, deviceName, order, orderField, equipmentId, getUser());
    }

    @RequiresPermissions(value = "sys:device")
    @GetMapping(value = "/upsPowerquery")
    public ResHelper<Page<UpsPowerDataDic>> upsPowerQuery(@RequestParam(value = "page", required = false) Integer page,
                                                          @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                          @RequestParam(value = "deviceName", required = false) String deviceName,
                                                          @RequestParam(value = "orderField", required = false) String orderField,
                                                          @RequestParam(value = "order", required = false) Integer order,
                                                          @RequestParam(value = "equipmentId", required = false) Integer equipmentId) {
        if (page == null || pageSize == null) {
            return ResHelper.pamIll();
        }
        if(equipmentId == null){
            return ResHelper.error("设备ID为空");
        }
        if (!ResHelper.checkOrder(order, orderField, UPSPOWER_DATADIC_TABLE)) {
            return ResHelper.pamIll();
        }
        return realTimeDataService.upsPowerQuery(page, pageSize, deviceName, order, orderField, equipmentId, getUser());
    }

    /**
     * BMS最近一次 平均温度 最高温度 最低温度
     */
    @GetMapping(value = "/bmsTempMsg")
    public ResHelper<List<BmsTempMsgBo>> bmsTempMsg(@RequestParam(value = "deviceName", required = false) String deviceName) {
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
                if (!realTimeDataService.verificationDeviceName(deviceName, getUser())) {
                    return ResHelper.success("");
                }
            }
            return realTimeDataService.bmsTempMsg(deviceName);
        }
        return ResHelper.error("deviceName为空");
    }

    /**
     * 获取BAMS最新一条数据更新充放电电量
     */
    @GetMapping(value = "/bamsLatestData")
    public ResHelper<BamsLatestBo> bamsLatestData(@RequestParam(value = "deviceName", required = false) String deviceName) {
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
                if (!realTimeDataService.verificationDeviceName(deviceName, getUser())) {
                    return ResHelper.success("");
                }
            }
            return realTimeDataService.bamLatestData(deviceName);
        }
        return ResHelper.error("deviceName为空");
    }

    /**
     * BCU簇数据 状态查询
     */
    @GetMapping(value = "/bcuDataqueryByName")
    public ResHelper<List<BcuDataBo>> bcuDataqueryByName(@RequestParam(value = "deviceName", required = false) String deviceName) {
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
                if (!realTimeDataService.verificationDeviceName(deviceName, getUser())) {
                    return ResHelper.success("");
                }
            }
            return realTimeDataService.bcuDataQueryByName(deviceName);
        }
        return ResHelper.error("deviceName为空");
    }

    /**
     * 根据电池簇获取对应 电池电压 跟温度信息
     */
    @GetMapping(value = "/bmscellDataQuery")
    public ResHelper<List<BmsCellDataBo>> bmscellDataQuery(@RequestParam(value = "deviceName", required = false) String deviceName) {
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
                if (!realTimeDataService.verificationDeviceName(deviceName, getUser())) {
                    return ResHelper.success("");
                }
            }
            return realTimeDataService.cellDataQuery(deviceName);
        }
        return ResHelper.error("deviceName为空");
    }

    @GetMapping(value = "/stationInformationQuery")
    public ResHelper<Object> stationInformationQuery(@RequestParam(value = "deviceName", required = false) String deviceName,
                                                     @RequestParam(value = "equipmentId", required = false) Integer equipmentId) {

        if (!StringUtils.isEmpty(deviceName)) {

            if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
                if (!realTimeDataService.verificationDeviceName(deviceName, getUser())) {
                    return ResHelper.success("");
                }
            }
            if (StringUtils.isEmpty(deviceName) || equipmentId == null) {
                return ResHelper.error("设备ID为空");
            }
            if (getUser().getUserType() != null) {
                return realTimeDataService.stationInformationQuery(deviceName, equipmentId, getUser().getUserType());
            }
        }
        return ResHelper.error("deviceName为空");
    }

    @SysLog("Excel导出数据")
    @RequiresPermissions(value = "sys:device")
    @RequestMapping(value = "/exportBamsData", method = {RequestMethod.GET, RequestMethod.POST})
    public void exportBamData(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(value = "deviceName", required = false) String deviceName,
                               @RequestParam(value = "time", required = false) String time,
                               @RequestParam(value = "fileName", required = false) String fileName,
                               @RequestParam(value = "type", required = false) Integer type) throws IOException {
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
                    if (!realTimeDataService.verificationDeviceName(deviceName, getUser())) {
                        throw new ExcelExportWithoutDataException("该日期暂无数据");
                    }
                }
            }
            Query fileNameQuery = new Query();
            fileNameQuery.addCriteria(Criteria.where("filename").is(deviceName + "_" + time + "_" + type));
            GridFSFile gridFSFile = gridFsTemplate.findOne(fileNameQuery);
            if(gridFSFile != null){
                GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
                GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
                response.setStatus(200);
                response.setContentLength( (int) gridFSFile.getLength());
                IOUtils.copy(gridFsResource.getInputStream(),outputStream);
            }else {
                excelExportService.exportStationOfBattery(deviceName, time, type, outputStream);
            }
        } catch (Exception e) {
            log.error("下载文件失败:{},时间为:{},设备名称:{},导出设备类型:{}", e, time, deviceName, excelExportService.getMsgByType(type));
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
