package com.bugull.hithiumfarmweb.http.controller;

import com.alibaba.fastjson.JSON;
import com.bugull.hithiumfarmweb.annotation.SysLog;
import com.bugull.hithiumfarmweb.common.Page;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.service.BreakDownLogService;
import com.bugull.hithiumfarmweb.http.vo.BreakDownLogVo;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

import static com.bugull.hithiumfarmweb.common.Const.BREAKDOWNLOG_TABLE;

/**
 * 告警日志模块
 */
@RestController
@RequestMapping(value = "/breakDownlog")
public class BreakDownLogController extends AbstractController {

    @Resource
    private BreakDownLogService breakDownLogService;
    @Resource
    private PropertiesConfig propertiesConfig;

    @RequiresPermissions(value = "sys:device")
    @GetMapping(value = "/queryBreakDownlog")
    public ResHelper<Page<BreakDownLogVo>> queryBreakDownlog(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "deviceName", required = false) String deviceName,
            @RequestParam(value = "orderField", required = false) String orderField,
            @RequestParam(value = "order", required = false) Integer order,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "alarmType", required = false) Integer alarmType,
            @RequestParam(value = "startTime", required = false) Date startTime,
            @RequestParam(value = "endTime", required = false) Date endTime,
            @RequestParam(value = "province", required = false) String province,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "city", required = false) String city) {
        if (page == null || pageSize == null) {
            return ResHelper.pamIll();
        }
        if (startTime != null && endTime != null) {
            if (startTime.getTime() > endTime.getTime()) {
                return ResHelper.pamIll();
            }
        }
        if (!ResHelper.checkOrder(order, orderField, BREAKDOWNLOG_TABLE)) {
            return ResHelper.pamIll();
        }
        if (!propertiesConfig.verifyProvinceOfCity(province, city)) {
            return ResHelper.pamIll();
        }
        return breakDownLogService.queryBreakdownLog(page, pageSize, deviceName, order, orderField, alarmType, startTime, endTime, province, city, country, status);
    }

    @SysLog("告警日志导出")
    @RequiresPermissions(value = "sys:device")
    @RequestMapping(value = "/exportBreakDwonlog", method = {RequestMethod.GET, RequestMethod.POST})
    public void exportBreakdownLog(HttpServletRequest request, HttpServletResponse response,
                                   @RequestParam(value = "time", required = false) String time,
                                   @RequestParam(value = "fileName", required = false) String fileName,
                                   @RequestParam(value = "province", required = false) String province,
                                   @RequestParam(value = "country", required = false) String country,
                                   @RequestParam(value = "city", required = false) String city,
                                   @RequestParam(value = "status", required = false) Boolean status,
                                   @RequestParam(value = "deviceName", required = false) String deviceName) throws IOException {
        OutputStream outputStream = null;
        try {
            breakDownLogService.verify(time, province, city);
            if (StringUtils.isEmpty(fileName)) {
                fileName = "告警日志";
            }
            outputStream = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            breakDownLogService.exportBreakDownLog(time, province, city, status, outputStream, deviceName);
        } catch (Exception e) {
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
