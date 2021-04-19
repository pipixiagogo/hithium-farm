package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.service.BreakDownLogService;
import com.bugull.hithiumfarmweb.http.vo.BreakDownLogVo;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 告警日志模块
 */
@RestController
@RequestMapping(value = "/breakDownlog")
public class BreakDownLogController extends AbstractController{

    @Resource
    private BreakDownLogService breakDownLogService;

    @ApiOperation(value = "告警日志查询")
    @RequestMapping(value = "/queryBreakDownlog",method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1",name = "page", value = "当前页码",paramType = "query",required = true,dataType = "int",dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10",name = "pageSize", value = "每页记录数",paramType = "query",required = true,dataType = "int",dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = false,paramType = "query",value = "设备码  唯一标识",dataType = "String",dataTypeClass = String.class),
            @ApiImplicitParam(name = "orderField", required = false,paramType = "query",value = "排序字段",dataType = "String",dataTypeClass = String.class),
            @ApiImplicitParam(example = "1",name = "order", required = false,paramType = "query",value = "升序 | 降序 -1：降序 1：升序",dataType = "int",dataTypeClass = Integer.class),
    })
    public ResHelper<BuguPageQuery.Page<BreakDownLogVo>> queryBreakDownlog(@ApiIgnore @RequestParam Map<String,Object> params){
        return breakDownLogService.query(params);
    }


}
