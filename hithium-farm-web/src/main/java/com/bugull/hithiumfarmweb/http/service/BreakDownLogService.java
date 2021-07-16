package com.bugull.hithiumfarmweb.http.service;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.common.Const;
import com.bugull.hithiumfarmweb.common.exception.ExcelExportWithoutDataException;
import com.bugull.hithiumfarmweb.common.exception.ParamsValidateException;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.dao.BreakDownLogDao;
import com.bugull.hithiumfarmweb.http.dao.DeviceDao;
import com.bugull.hithiumfarmweb.http.entity.BreakDownLog;
import com.bugull.hithiumfarmweb.http.entity.Device;
import com.bugull.hithiumfarmweb.http.entity.EssStation;
import com.bugull.hithiumfarmweb.http.entity.SysUser;
import com.bugull.hithiumfarmweb.http.vo.BreakDownLogVo;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.PagetLimitUtil;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.BuguQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bugull.hithiumfarmweb.common.Const.*;


@Service
public class BreakDownLogService {

    @Resource
    private BreakDownLogDao breakDownLogDao;
    @Resource
    private DeviceDao deviceDao;
    @Resource
    private PropertiesConfig propertiesConfig;
    @Resource
    private EssStationService essStationService;
    public static final Logger log = LoggerFactory.getLogger(BreakDownLogService.class);


    public ResHelper<BuguPageQuery.Page<BreakDownLogVo>> query(Map<String, Object> params, SysUser user) {
        BuguPageQuery<BreakDownLog> query = breakDownLogDao.pageQuery();
        String deviceName = (String) params.get(DEVICE_NAME);
        if (!StringUtils.isEmpty(deviceName)) {
            query.is(DEVICE_NAME, deviceName);
        }
        String alarmType = (String) params.get("alarmType");
        if (!StringUtils.isEmpty(alarmType)) {
            query.is("alarmType", Integer.valueOf(alarmType));
        }
        String status = (String) params.get(STATUS);
        if (status != null) {
            query.is(STATUS, Boolean.valueOf(status));
        }
        String startTime = (String) params.get("startTime");
        String endTime = (String) params.get("endTime");
        try {
            if (!StringUtils.isEmpty(startTime)) {
                Date end;
                if (StringUtils.isEmpty(endTime)) {
                    end = new Date();
                } else {
                    end = DateUtils.strToDate(endTime);
                }
                Date start = DateUtils.strToDate(startTime);
                if (startTime != null && endTime != null) {
                    if (start.getTime() > end.getTime()) {
                        return ResHelper.pamIll();
                    } else {
                        query.greaterThanEquals(GENERATION_DATA_TIME, start);
                        query.lessThanEquals(GENERATION_DATA_TIME, end);
                    }
                }
            }
        } catch (Exception e) {
            log.error("告警日志查询失败:{}", e);
            return ResHelper.pamIll();
        }
        List<EssStation> essStationList = essStationService.getEssStationList(user.getStationList());
        if (propertiesConfig.verify(params)) {
            String province = (String) params.get(PROVINCE);
            String city = (String) params.get("city");
            BuguQuery<Device> buguQuery = deviceDao.query();
            if (!StringUtils.isEmpty(province)) {
                buguQuery.is(PROVINCE, province);
            }
            if (!StringUtils.isEmpty(city)) {
                buguQuery.is("city", city);
            }
            if (!CollectionUtils.isEmpty(essStationList) && !essStationList.isEmpty()) {
                List<String> deviceNameList = new ArrayList<>();
                for (EssStation essStation : essStationList) {
                    if (!CollectionUtils.isEmpty(essStation.getDeviceNameList()) && !essStation.getDeviceNameList().isEmpty()) {
                        deviceNameList.addAll(essStation.getDeviceNameList());
                    }
                }
                buguQuery.in(DEVICE_NAME, deviceNameList);
            }
            List<String> deviceNames = buguQuery.results().stream().map(Device::getDeviceName).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                query.in(DEVICE_NAME, deviceNames);
            } else {
                return ResHelper.success("", new BuguPageQuery.Page<>(Integer.parseInt((String) params.get(Const.PAGE)),
                        Integer.parseInt((String) params.get(Const.PAGESIZE)), 0, null));
            }
        } else {
            return ResHelper.pamIll();
        }
        if (!PagetLimitUtil.pageLimit(query, params)) return ResHelper.pamIll();
        if (!PagetLimitUtil.orderField(query, params, BREAKDOWNLOG_TABLE)) return ResHelper.pamIll();
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
                if (!CollectionUtils.isEmpty(essStationList) && !essStationList.isEmpty()) {
                    boolean flag = false;
                    for (EssStation essStation : essStationList) {
                        if (!CollectionUtils.isEmpty(essStation.getDeviceNameList()) && !essStation.getDeviceNameList().isEmpty()) {
                            if (essStation.getDeviceNameList().contains(deviceName)) {
                                flag = true;
                            }
                        }
                    }
                    if (!flag) {
                        return ResHelper.success("", new BuguPageQuery.Page<>(Integer.parseInt((String) params.get(Const.PAGE)),
                                Integer.parseInt((String) params.get(Const.PAGESIZE)), 0, null));
                    }
                }
            }
        }
        query.sortDesc(GENERATION_DATA_TIME);
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
        return deviceDao.query().is(DEVICE_NAME, deviceName).returnFields(PROVINCE, "city", "name", "description").result();
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

    public void verify(String time, String province, String city) {
        if (!StringUtils.isEmpty(time) && !Const.DATE_PATTERN.matcher(time).matches()) {
            //根据传入的数值获取
            throw new ParamsValidateException("日期格式错误");
        }
        if (!StringUtils.isEmpty(province)) {
            if (!propertiesConfig.getProToCitys().containsKey(province)
                    || !propertiesConfig.getProToCitys().get(province).contains(city)) {
                throw new ParamsValidateException("省份/市区错误");
            }
        }
        if (!StringUtils.isEmpty(city)) {
            if (!propertiesConfig.getCitys().containsKey(city) ||
                    !propertiesConfig.getCitys().get(city).equals(province))
                throw new ParamsValidateException("省份/市区错误");
        }
    }

    public void exportBreakDownlog(String time, String province, String city, Boolean status, OutputStream outputStream, SysUser user, String deviceName) throws ParseException {
        Date startTime = null;
        Date endTime = null;
        if (StringUtils.isEmpty(time)) {
            Date date = new Date();
            startTime = DateUtils.getStartTime(date);
            endTime = DateUtils.getEndTime(date);
        } else {
            //根据传入的数值获取
            if (!Const.DATE_PATTERN.matcher(time).matches()) {
                throw new ExcelExportWithoutDataException("日期格式错误");
            }
            startTime = DateUtils.dateToStrWithHHmm(time);
            endTime = DateUtils.getEndTime(startTime);
        }
        BuguQuery<Device> deviceBuguQuery = deviceDao.query();
        if (!StringUtils.isEmpty(province)) {
            deviceBuguQuery.is(PROVINCE, province);
        }
        if (!StringUtils.isEmpty(city)) {
            deviceBuguQuery.is("city", city);
        }
        if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
            List<EssStation> essStationList = essStationService.getEssStationList(user.getStationList());
            boolean flag = false;
            List<String> deviceNames = new ArrayList<>();
            for (EssStation essStation : essStationList) {
                if (!CollectionUtils.isEmpty(essStation.getDeviceNameList()) && !essStation.getDeviceNameList().isEmpty()) {
                    if (essStation.getDeviceNameList().contains(deviceName)) {
                        flag = true;
                    }
                }
                deviceNames.addAll(essStation.getDeviceNameList());
            }

            if (!flag) {
                throw new ExcelExportWithoutDataException("该日期暂无数据");
            } else {
                if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                    deviceBuguQuery.in(DEVICE_NAME, deviceNames);
                }
            }

        }
        List<String> deviceNames = deviceBuguQuery.results().stream().map(Device::getDeviceName).collect(Collectors.toList());
        BuguQuery<BreakDownLog> breakDownLogBuguQuery = breakDownLogDao.query();
        if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
            breakDownLogBuguQuery.in(DEVICE_NAME, deviceNames);
        }
        if (!StringUtils.isEmpty(province) && !StringUtils.isEmpty(city) && CollectionUtils.isEmpty(deviceNames)) {
            throw new ExcelExportWithoutDataException("该日期暂无数据");
        }
        if (status != null) {
            breakDownLogBuguQuery.is(STATUS, status);
        }
        List<BreakDownLog> breakDownLogList = breakDownLogBuguQuery.greaterThanEquals(GENERATION_DATA_TIME, startTime)
                .lessThanEquals(GENERATION_DATA_TIME, endTime).sort("{generationDataTime:-1}").results();
        if (!CollectionUtils.isEmpty(breakDownLogList) && !breakDownLogList.isEmpty()) {
            List<BreakDownLogVo> breakDownLogBos = breakDownLogList.stream().map(data -> {
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
            ExcelWriter writer = null;
            try {
                writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                WriteSheet writeSheet = EasyExcelFactory.writerSheet(0, BREAKDOWNLOG_DATA_SHEET_NAME).head(BreakDownLogVo.class).build();
                writer.write(breakDownLogBos, writeSheet);
            } finally {
                if (writer != null) {
                    writer.finish();
                }
            }
        } else {
            throw new ExcelExportWithoutDataException("该日期暂无数据");
        }
    }
}
