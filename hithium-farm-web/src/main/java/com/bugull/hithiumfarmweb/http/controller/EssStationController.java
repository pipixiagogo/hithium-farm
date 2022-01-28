package com.bugull.hithiumfarmweb.http.controller;

import com.bugull.hithiumfarmweb.annotation.SysLog;
import com.bugull.hithiumfarmweb.common.Page;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.service.EssStationService;
import com.bugull.hithiumfarmweb.http.vo.EssStationVo;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @Resource
    private PropertiesConfig propertiesConfig;

    @RequiresPermissions(value = "sys:user")
    @SysLog(value = "新增电站")
    @PostMapping(value = "/save")
    public ResHelper<Void> saveStation(@RequestParam(value = "files") MultipartFile[] files,
                                       @RequestParam(value = "stationName", required = false) String stationName,
                                       @RequestParam(value = "deviceNameList", required = false) List<String> deviceNameList,
                                       @RequestParam(value = "province", required = false) String province,
                                       @RequestParam(value = "city", required = false) String city) {
        if (StringUtils.isEmpty(stationName)) {
            return ResHelper.pamIll();
        }
        if (CollectionUtils.isEmpty(deviceNameList) && deviceNameList.isEmpty()) {
            return ResHelper.pamIll();
        }
        if(essStationService.existStation(stationName)){
            return ResHelper.error("该电站名称已存在");
        }

        return essStationService.save(stationName, deviceNameList, files, province, city);
    }

    @RequiresPermissions(value = "sys:user")
    @SysLog(value = "修改电站信息")
    @PostMapping(value = "/updateStation")
    public ResHelper<Void> updateStation(
            @RequestParam(value = "files") MultipartFile[] files,
            @RequestParam(value = "stationName", required = false) String stationName,
            @RequestParam(value = "deviceNameList", required = false) List<String> deviceNameList,
            @RequestParam(value = "province", required = false) String province,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "stationId", required = false) String stationId,
            @RequestParam(value = "filesId", required = false) List<String> filesId) {
        if (StringUtils.isEmpty(stationName) || StringUtils.isEmpty(stationId)) {
            return ResHelper.pamIll();
        }
        if (CollectionUtils.isEmpty(deviceNameList) && deviceNameList.isEmpty()) {
            return ResHelper.pamIll();
        }
        if(!StringUtils.isEmpty(province) && StringUtils.isEmpty(city)){
            return ResHelper.pamIll();
        }
        if(essStationService.existStationWithId(stationName,stationId)){
            return ResHelper.error("该电站名称已存在");
        }
        return essStationService.updateStation(stationName, deviceNameList, files, province, city, stationId,filesId);
    }

    @GetMapping(value = "/selectStation")
    public ResHelper<Page<EssStationVo>> selectStation(@RequestParam(value = "page", required = false) Integer page,
                                                       @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                       @RequestParam(value = "stationName", required = false) String stationName,
                                                       @RequestParam(value = "country", required = false) String country,
                                                       @RequestParam(value = "province", required = false) String province,
                                                       @RequestParam(value = "city", required = false) String city) {
        if (page == null || pageSize == null) {
            return ResHelper.pamIll();
        }
        if (!propertiesConfig.verifyProvinceOfCity(province, city)) {
            return ResHelper.pamIll();
        }
        return essStationService.selectStation(page, pageSize, stationName, country, province, city, stationName, getUser());
    }

    /**
     * @param stationId
     * @return
     */
    @GetMapping(value = "/info/{stationId}")
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
    public ResHelper<Map<String, Set<String>>> queryProvinces() {
        return essStationService.queryProvinces();
    }



}
