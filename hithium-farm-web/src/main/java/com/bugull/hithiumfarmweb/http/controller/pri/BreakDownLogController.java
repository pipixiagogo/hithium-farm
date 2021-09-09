package com.bugull.hithiumfarmweb.http.controller.pri;

import com.alibaba.fastjson.JSON;
import com.bugull.hithiumfarmweb.annotation.SysLog;
import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.service.BreakDownLogService;
import com.bugull.hithiumfarmweb.http.vo.BreakDownLogVo;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 告警日志模块
 */
@RestController
@RequestMapping(value = "/breakDownlog")
public class BreakDownLogController extends AbstractController {

    @Resource
    private BreakDownLogService breakDownLogService;
    @RequiresPermissions(value = "sys:device")
    @ApiOperation(value = "告警日志查询")
    @GetMapping(value = "/queryBreakDownlog")
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false, paramType = "query", value = "设备码  唯一标识", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false, paramType = "query", value = "排序字段", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "1", name = "order", required = false, paramType = "query", value = "升序 | 降序 -1：降序 1：升序", dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "0", name = "alarmType", required = false, paramType = "query", dataType = "int", value = "0,1,2,3", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "true", name = "status", required = false, paramType = "query", dataType = "boolean", value = "false 消除 true 发生", dataTypeClass = Boolean.class),
            @ApiImplicitParam(name = "startTime", value = "告警发生开始时间 yyyy-MM-dd HH:mm:ss", paramType = "query", required = false, dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "endTime", value = "告警发生结束时间 yyyy-MM-dd HH:mm:ss", paramType = "query", required = false, dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "province", value = "省份", required = false, paramType = "query", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "country", value = "国家", required = false, example = "china 中国 ", paramType = "query", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "city", value = "市区", required = false, paramType = "query", dataType = "String", dataTypeClass = String.class)
    })
    public ResHelper<BuguPageQuery.Page<BreakDownLogVo>> queryBreakDownlog(@ApiIgnore @RequestParam Map<String, Object> params) {
        return breakDownLogService.query(params);
    }
    @SysLog("告警日志导出")
    @RequiresPermissions(value = "sys:device")
    @ApiOperation(value = "告警日志导出")
    @RequestMapping(value = "/exportBreakDwonlog",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiImplicitParams({@ApiImplicitParam(name = "time", required = false, paramType = "query", value = "时间日期"),
            @ApiImplicitParam(name = "fileName", required = false, paramType = "query", value = "文件名称"),
            @ApiImplicitParam(name = "province", value = "省份", required = false, paramType = "query", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "country", value = "国家", required = false, example = "china 中国 ", paramType = "query", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "city", value = "市区", required = false, paramType = "query", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(example = "true", name = "status", required = false, paramType = "query", dataType = "boolean", value = "false 消除 true 发生", dataTypeClass = Boolean.class),
            @ApiImplicitParam(name = "deviceName",required = false,paramType = "query",value = "设备名称",dataTypeClass = String.class)
    })
    public void exportBreakDwonlog(HttpServletRequest request, HttpServletResponse response,
                                   @ApiIgnore @RequestParam(value = "time", required = false) String time,
                                   @ApiIgnore @RequestParam(value = "fileName", required = false) String fileName,
                                   @ApiIgnore @RequestParam(value = "province", required = false) String province,
                                   @ApiIgnore @RequestParam(value = "country", required = false) String country,
                                   @ApiIgnore @RequestParam(value = "city", required = false) String city,
                                   @ApiIgnore @RequestParam(value = "status",required = false)Boolean status,
                                   @ApiIgnore @RequestParam(value = "deviceName",required = false)String deviceName) throws IOException {
        OutputStream outputStream = null;
        try {
            breakDownLogService.verify(time,province,city);
            if(StringUtils.isEmpty(fileName)){
                fileName="告警日志";
            }
            outputStream = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            breakDownLogService.exportBreakDownlog(time,province,city,status,outputStream,deviceName );
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
