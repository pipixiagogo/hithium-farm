package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.entity.Equipment;
import com.bugull.hithiumfarmweb.http.service.EquipmentService;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 设备模块
 */
@RestController
@RequestMapping(value = "/equipment")
public class EquipmentController extends AbstractController{

    @Resource
    private EquipmentService equipmentService;

//    @RequiresPermissions("query")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @ApiOperation(value = "设备列表", httpMethod = "GET", response = ResHelper.class)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1",name = "page", value = "当前页码",paramType = "query",required = true,dataType = "int",dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10",name = "pageSize", value = "每页记录数",paramType = "query",required = true,dataType = "int",dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "deviceName", required = true,paramType = "query",value = "设备名称",dataType = "String",dataTypeClass = String.class)})
    public ResHelper<BuguPageQuery.Page<Equipment>> queryEquipment(@ApiIgnore @RequestParam Map<String, Object> params) {
        return equipmentService.queryEquipment(params);
    }



}
