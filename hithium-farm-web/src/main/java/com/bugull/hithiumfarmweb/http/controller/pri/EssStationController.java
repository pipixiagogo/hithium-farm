package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.annotation.SysLog;
import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.bo.EssStationBo;
import com.bugull.hithiumfarmweb.http.entity.UploadEntity;
import com.bugull.hithiumfarmweb.http.service.EssStationService;
import com.bugull.hithiumfarmweb.http.vo.EssStationVo;
import com.bugull.hithiumfarmweb.utils.PatternUtil;
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
    public ResHelper<Void> saveStation(@RequestBody EssStationBo essStationBo) {
        return essStationService.save(essStationBo);
    }

    @RequiresPermissions(value = "sys:user")
    @SysLog(value = "修改电站信息")
    @PostMapping(value = "/updateStation")
    public ResHelper<Void> updateStation(@RequestBody EssStationBo essStationBo) {
        return essStationService.updateStation(essStationBo);
    }

    @GetMapping(value = "/selectStation")
    public ResHelper<BuguPageQuery.Page<EssStationVo>> selectStation(@RequestParam (value = "page",required = false)Integer page,
                                                                      @RequestParam(value = "pageSize",required = false)Integer pageSize,
                                                                       @RequestParam(value = "stationName",required = false)String stationName,
                                                                      @RequestParam(value = "country",required = false)String country,
                                                                     @RequestParam(value = "province",required = false)String province,
                                                                     @RequestParam(value = "city",required = false)String city) {
        if(page == null || pageSize == null){
            return ResHelper.pamIll();
        }
        if(!propertiesConfig.verifyProvinceOfCity(province,city)){
            return ResHelper.pamIll();
        }
        return essStationService.selectStation(page,pageSize,stationName,country,province,city,stationName,getUser());
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

    @RequiresPermissions(value = "sys:user")
    @RequestMapping(value = "/uploadImg", method = RequestMethod.POST)
    @SysLog(value = "上传图片")
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
