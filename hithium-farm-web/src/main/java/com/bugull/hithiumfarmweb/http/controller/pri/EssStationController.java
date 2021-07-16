package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.annotation.SysLog;
import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.entity.EssStation;
import com.bugull.hithiumfarmweb.http.entity.EssStationWithDeviceVo;
import com.bugull.hithiumfarmweb.http.entity.SysUser;
import com.bugull.hithiumfarmweb.http.service.EssStationService;
import com.bugull.hithiumfarmweb.http.vo.EssStationVo;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/station")
public class EssStationController {

    @Resource
    private EssStationService essStationService;

    @RequiresPermissions(value = "sys:user")
    @SysLog(value = "新增电站")
    @PostMapping(value = "/save")
    @ApiOperation(value = "新增电站", httpMethod = "POST", response = ResHelper.class)
    @ApiImplicitParam(name = "essStation", value = "电站实体类", required = true, paramType = "body", dataTypeClass = SysUser.class, dataType = "EssStation")
    public ResHelper<Void> saveStation(@RequestBody EssStation essStation) {
        return essStationService.save(essStation);
    }

    @RequiresPermissions(value = "sys:user")
    @SysLog(value = "修改电站信息")
    @PostMapping(value = "/updateStation")
    @ApiOperation(value = "修改电站信息", httpMethod = "POST", response = ResHelper.class)
    @ApiImplicitParam(name = "essStation", value = "电站实体类", required = true, paramType = "body", dataTypeClass = SysUser.class, dataType = "EssStation")
    public ResHelper<Void> updateStation(@RequestBody EssStation essStation) {
        return essStationService.updateStation(essStation);
    }

    @SysLog(value = "查询电站信息")
    @GetMapping(value = "/selectStation")
    @ApiOperation(value = "查询电站信息", httpMethod = "GET", response = ResHelper.class)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "stationName", required = false, paramType = "query", value = "电站名称", dataType = "String", dataTypeClass = String.class)})
    public ResHelper<BuguPageQuery.Page<EssStationVo>> selectStation(@ApiIgnore @RequestParam Map<String, Object> params) {
        return essStationService.selectStation(params);
    }

    /**
     * @param stationId
     * @return
     */
    @RequiresPermissions(value = "sys:user")
    @GetMapping(value = "/info/{stationId}")
    @ApiOperation(value = "根据电站ID查询电站信息", response = ResHelper.class)
    @ApiImplicitParam(name = "stationId", value = "电站ID", paramType = "path", example = "60ee8c8275da123544dc2b54", dataType = "string", dataTypeClass = String.class)
    public ResHelper<EssStationWithDeviceVo> infoUserId(@PathVariable String stationId) {
        if (StringUtils.isEmpty(stationId)) {
            return ResHelper.pamIll();
        }
        return ResHelper.success("", essStationService.infoStationById(stationId));
    }


    @RequiresPermissions(value = "sys:user")
    @SysLog(value = "删除电站信息")
    @PostMapping(value = "/deleteStation")
    @ApiOperation(value = "删除电站信息", httpMethod = "POST", response = ResHelper.class)
    @ApiImplicitParam(name = "stationIds", value = "电站ID数组", allowMultiple = true, dataType = "string", required = true)
    public ResHelper<Void> deleteStation(@RequestBody String[] stationIds) {
        List<String> stations = new ArrayList<>();
        for (String stationId : stationIds) {
            if (!StringUtils.isEmpty(stationId)) {
                stations.add(stationId);
            }
        }
        return essStationService.deleteStation(stations);
    }
}
