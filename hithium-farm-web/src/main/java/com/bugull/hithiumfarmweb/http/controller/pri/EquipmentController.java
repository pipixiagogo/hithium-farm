package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.entity.Equipment;
import com.bugull.hithiumfarmweb.http.service.EquipmentService;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 设备模块
 */
@RestController
@RequestMapping(value = "/equipment")
public class EquipmentController extends AbstractController {

    @Resource
    private EquipmentService equipmentService;

    @GetMapping(value = "/query")
    public ResHelper<BuguPageQuery.Page<Equipment>> queryEquipment(@RequestParam(value = "page", required = false) Integer page,
                                                                   @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                   @RequestParam(value = "deviceName", required = false) String deviceName) {
        if (page == null || pageSize == null) {
            return ResHelper.pamIll();
        }
        return equipmentService.queryEquipment(page, pageSize, deviceName);
    }


}
