package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.dao.BreakDownLogDao;
import com.bugull.hithiumfarmweb.http.dao.DeviceDao;
import com.bugull.hithiumfarmweb.http.entity.BreakDownLog;
import com.bugull.hithiumfarmweb.http.entity.Device;
import com.bugull.hithiumfarmweb.http.vo.BreakDownLogVo;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.BuguQuery;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class BreakDownLogService {

    @Resource
    private BreakDownLogDao breakDownLogDao;
    @Resource
    private DeviceDao deviceDao;
    @Resource
    private PropertiesConfig propertiesConfig;

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public ResHelper<BuguPageQuery.Page<BreakDownLogVo>> query(Map<String, Object> params) {
        BuguPageQuery<BreakDownLog> query = (BuguPageQuery<BreakDownLog>) breakDownLogDao.pageQuery();
        String deviceName = (String) params.get("deviceName");
        if (!StringUtils.isEmpty(deviceName)) {
            query.is("deviceName", deviceName);
        }
        String alarmType = (String) params.get("alarmType");
        if (!StringUtils.isEmpty(alarmType)) {
            query.is("alarmType", Integer.valueOf(alarmType));
        }
        String status = (String) params.get("status");
        if (status != null) {
            query.is("status",Boolean.valueOf( status));
        }
        String startTime = (String) params.get("startTime");
        String endTime = (String) params.get("endTime");
        try {
            if (!StringUtils.isEmpty(startTime)) {
                Date end;
                if (StringUtils.isEmpty(endTime)) {
                    end = new Date();
                } else {
                    end = simpleDateFormat.parse(endTime);
                }
                Date start = simpleDateFormat.parse(startTime);
                if (startTime != null && endTime != null) {
                    if (start.getTime() > end.getTime()) {
                        return ResHelper.pamIll();
                    } else {
                        query.greaterThanEquals("generationDataTime", start);
                        query.lessThanEquals("generationDataTime", end);
                    }
                }
            }
        } catch (Exception e) {
            return ResHelper.pamIll();
        }
        if (propertiesConfig.verify(params)) {
            String province = (String) params.get("province");
            String city = (String) params.get("city");
            BuguQuery<Device> buguQuery = deviceDao.query();
            if(!StringUtils.isEmpty(province)){
                buguQuery.is("province",province);
            }
            if(!StringUtils.isEmpty(city)){
                buguQuery.is("city",city);
            }
                List<String> deviceNames = new ArrayList<>();
            List<Device> deviceList = buguQuery.results();
            for (Device device : deviceList) {
                deviceNames.add(device.getDeviceName());
            }
            if(!CollectionUtils.isEmpty(deviceNames) && deviceNames.size()>0){
                query.in("deviceName", deviceNames);
            }else {
                return ResHelper.success("");
            }
        }else {
            return ResHelper.pamIll();
        }
        if (!PagetLimitUtil.pageLimit(query, params)) return ResHelper.pamIll();
        if (!PagetLimitUtil.orderField(query, params)) return ResHelper.pamIll();
        query.sortDesc("generationDataTime");
        BuguPageQuery.Page<BreakDownLog> downLogPage = query.resultsWithPage();
        List<BreakDownLog> datas = downLogPage.getDatas();
        List<BreakDownLogVo> breakDownLogBos = datas.stream().map(data -> {
            BreakDownLogVo breakDownLogVo = new BreakDownLogVo();
            BeanUtils.copyProperties(data, breakDownLogVo);
            breakDownLogVo.setAlarmTypeMsg(getAlarmTypeMsg(data.getAlarmType()));
            breakDownLogVo.setStatusMsg(data.getStatus() ? "发生" : "消除");
            Device byName = getDeviceByName(data.getDeviceName());
            breakDownLogVo.setAreaMsg(byName.getProvince() + "-" + byName.getCity());
            breakDownLogVo.setDescription(byName.getDescription());
            breakDownLogVo.setDeviceOfName(byName.getName());
            return breakDownLogVo;
        }).collect(Collectors.toList());
        BuguPageQuery.Page<BreakDownLogVo> breakDownLogBoPage = new BuguPageQuery.Page<>(downLogPage.getPage(),
                downLogPage.getPageSize(), downLogPage.getTotalRecord(), breakDownLogBos);
        return ResHelper.success("", breakDownLogBoPage);
    }

    private Device getDeviceByName(String deviceName) {
        Device device = deviceDao.query().is("deviceName", deviceName).returnFields("province", "city", "name", "description").result();
        return device;
    }

    private String getAlarmTypeMsg(Integer alarmType) {
        switch (alarmType) {
            case 0:
                return "预警";
            case 1:
                return "告警";
            case 2:
                return "保护";
            case 3:
                return "故障";
            default:
                return "未知告警类型";
        }
    }
}
