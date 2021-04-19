package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.http.dao.BreakDownLogDao;
import com.bugull.hithiumfarmweb.http.dao.DeviceDao;
import com.bugull.hithiumfarmweb.http.entity.BreakDownLog;
import com.bugull.hithiumfarmweb.http.entity.Device;
import com.bugull.hithiumfarmweb.http.vo.BreakDownLogVo;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class BreakDownLogService {

    @Resource
    private BreakDownLogDao breakDownLogDao;

    @Resource
    private DeviceDao deviceDao;


    public ResHelper<BuguPageQuery.Page<BreakDownLogVo>> query(Map<String, Object> params) {
        BuguPageQuery<BreakDownLog> query = (BuguPageQuery<BreakDownLog>) breakDownLogDao.pageQuery();
        String deviceName = (String) params.get("deviceName");
        if (!StringUtils.isEmpty(deviceName)) {
            query.is("deviceName", deviceName);
        }
        if (!PagetLimitUtil.pageLimit(query, params)) return ResHelper.pamIll();
        if (!PagetLimitUtil.orderField(query, params)) return ResHelper.pamIll();
        query.sortDesc("_id");
        BuguPageQuery.Page<BreakDownLog> downLogPage = query.resultsWithPage();

        List<BreakDownLog> datas = downLogPage.getDatas();
        List<BreakDownLogVo> breakDownLogBos = datas.stream().map(data -> {
            BreakDownLogVo breakDownLogVo = new BreakDownLogVo();
            BeanUtils.copyProperties(data, breakDownLogVo);
            breakDownLogVo.setAlarmTypeMsg(getAlarmTypeMsg(data.getAlarmType()));
            breakDownLogVo.setStatusMsg(data.getStatus() ? "发生" : "消除");
            Device byName = getDeviceByName(data.getDeviceName());
            breakDownLogVo.setAreaMsg(byName.getProvince()+"-"+byName.getCity());
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
