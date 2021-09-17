package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.annotation.SysLog;
import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.bo.EssStationBo;
import com.bugull.hithiumfarmweb.http.entity.UploadEntity;
import com.bugull.hithiumfarmweb.http.service.EssStationService;
import com.bugull.hithiumfarmweb.http.vo.EssStationVo;
import com.bugull.hithiumfarmweb.utils.PatternUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.bugull.hithiumfarmweb.common.Const.*;

@RestController
@RequestMapping(value = "/station")
public class EssStationController extends AbstractController {

    @Resource
    private EssStationService essStationService;

    @RequiresPermissions(value = "sys:user")
    @SysLog(value = "新增电站")
    @PostMapping(value = "/save")
    @ApiOperation(value = "新增电站", httpMethod = "POST", response = ResHelper.class)
    @ApiImplicitParam(name = "essStationBo", value = "电站实体类", required = true, paramType = "body", dataTypeClass = EssStationBo.class, dataType = "EssStationBo")
    public ResHelper<Void> saveStation(@RequestBody EssStationBo essStationBo) {
        return essStationService.save(essStationBo);
    }

    @RequiresPermissions(value = "sys:user")
    @SysLog(value = "修改电站信息")
    @PostMapping(value = "/updateStation")
    @ApiOperation(value = "修改电站信息", httpMethod = "POST", response = ResHelper.class)
    @ApiImplicitParam(name = "essStationBo", value = "电站实体类", required = true, paramType = "body", dataTypeClass = EssStationBo.class, dataType = "EssStationBo")
    public ResHelper<Void> updateStation(@RequestBody EssStationBo essStationBo) {
        return essStationService.updateStation(essStationBo);
    }

    @GetMapping(value = "/selectStation")
    @ApiOperation(value = "查询电站信息", httpMethod = "GET", response = ResHelper.class)
    @ApiImplicitParams({
            @ApiImplicitParam(example = "1", name = "page", value = "当前页码", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(example = "10", name = "pageSize", value = "每页记录数", paramType = "query", required = true, dataType = "int", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "stationName", required = false, paramType = "query", value = "电站名称", dataType = "String", dataTypeClass = String.class)})
    public ResHelper<BuguPageQuery.Page<EssStationVo>> selectStation(@ApiIgnore @RequestParam Map<String, Object> params) {
        return essStationService.selectStation(params,getUser());
    }

    /**
     * @param stationId
     * @return
     */
    @GetMapping(value = "/info/{stationId}")
    @ApiOperation(value = "根据电站ID查询电站信息", response = ResHelper.class)
    @ApiImplicitParam(name = "stationId", value = "电站ID", paramType = "path", example = "60ee8c8275da123544dc2b54", dataType = "string", dataTypeClass = String.class)
    public ResHelper<EssStationVo> infoUserId(@PathVariable String stationId) {
        if (StringUtils.isEmpty(stationId)) {
            return ResHelper.pamIll();
        }
        if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
            if (!getUser().getStationList().contains(stationId)) {
                return ResHelper.error(NO_QUERY_PERMISSION);
            }
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

    @RequiresPermissions(value = "sys:user")
    @GetMapping(value = "/queryProvinces")
    @ApiOperation(value = "查询所有中国省份信息", httpMethod = "GET", response = ResHelper.class)
    public ResHelper<Map<String, Set<String>>> queryProvinces() {
        return essStationService.queryProvinces();
    }

    @RequiresPermissions(value = "sys:user")
    @RequestMapping(value = "/uploadImg", method = RequestMethod.POST)
    @ApiOperation("图片上传,返回图片地址")
    @SysLog(value = "上传图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "files", value = "上传文件",
                    required = true, paramType = "body"),
            @ApiImplicitParam(name = "type", value = "STATION 电站图片  | ENERGY_STORAGE 储能站图片",
                    required = true, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = "deviceNames", value = "多个设备使用,分隔",
                    required = true, paramType = "body", dataType = "string")
    })
    public ResHelper<UploadEntity> upload(MultipartFile[] files,
                                          String type,
                                          String deviceNames) throws Exception {
        if (StringUtils.isEmpty(type)) {
            return ResHelper.pamIll();
        }
        if (type.equals(ENERGY_STORAGE)) {
            if (StringUtils.isEmpty(deviceNames)) {
                return ResHelper.pamIll();
            }
            String[] deviceNamesSplit = deviceNames.split(",");
            if (deviceNamesSplit == null || deviceNamesSplit.length == 0) {
                return ResHelper.pamIll();
            }
        }
        for (MultipartFile file : files) {
            byte[] b = new byte[3];
            file.getInputStream().read(b, 0, b.length);
            String hexString = PatternUtil.bytesToHexString(b);
            String ooo = PatternUtil.checkType(hexString.toUpperCase());
            if (StringUtils.isEmpty(ooo)) {
                return ResHelper.pamIll();
            }
            if (ooo.equals("0000")) {
                return ResHelper.pamIll();
            }
        }
        return essStationService.upload(files, type, deviceNames);
    }


}
