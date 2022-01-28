package com.bugull.hithiumfarmweb.http.service;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.bugull.hithiumfarmweb.common.Const;
import com.bugull.hithiumfarmweb.common.Page;
import com.bugull.hithiumfarmweb.common.exception.ExcelExportWithoutDataException;
import com.bugull.hithiumfarmweb.common.exception.ParamsValidateException;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.dao.BreakDownLogDao;
import com.bugull.hithiumfarmweb.http.dao.CorpDao;
import com.bugull.hithiumfarmweb.http.dao.DeviceDao;
import com.bugull.hithiumfarmweb.http.entity.BreakDownLog;
import com.bugull.hithiumfarmweb.http.entity.Device;
import com.bugull.hithiumfarmweb.http.entity.EssStation;
import com.bugull.hithiumfarmweb.http.entity.SysUser;
import com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity.AlarmThirdPartyVo;
import com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity.AlarmTime;
import com.bugull.hithiumfarmweb.http.entity.thirdPartyEntity.Corp;
import com.bugull.hithiumfarmweb.http.vo.BreakDownLogVo;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.hithiumfarmweb.utils.SecretUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.*;
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
    @Resource
    private CorpDao corpDao;
    public static final Logger log = LoggerFactory.getLogger(BreakDownLogService.class);

    private Device getDeviceByName(String deviceName) {
        Query query = new Query();
        query.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)).fields().include(PROVINCE).include("city")
                .include("name").include("description");
        return deviceDao.findDevice(query);
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

    public void exportBreakDownLog(String time, String province, String city, Boolean status, OutputStream outputStream, String deviceName) throws ParseException {
        Date startTime;
        Date endTime;
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
        Query query = new Query();
        if (!StringUtils.isEmpty(province)) {
            query.addCriteria(Criteria.where(PROVINCE).is(province));
        }
        if (!StringUtils.isEmpty(city)) {
            query.addCriteria(Criteria.where("city").is(city));
        }
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        if (!CollectionUtils.isEmpty(user.getStationList()) && !user.getStationList().isEmpty()) {
            List<String> essStationServiceDeviceNames = essStationService.getDeviceNames(user.getStationList());
            if (!CollectionUtils.isEmpty(essStationServiceDeviceNames) && !essStationServiceDeviceNames.isEmpty()) {
                if (!StringUtils.isEmpty(deviceName) && essStationServiceDeviceNames.contains(deviceName)) {
                    query.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
                }
                if (!StringUtils.isEmpty(deviceName) && !essStationServiceDeviceNames.contains(deviceName)) {
                    throw new ExcelExportWithoutDataException("该日期暂无数据");
                }
                query.addCriteria(Criteria.where(DEVICE_NAME).in(essStationServiceDeviceNames));
            }
        }
        List<String> deviceNames = deviceDao.findBatchDevices(query).stream().map(Device::getDeviceName).collect(Collectors.toList());
        Query breakDownQuery = new Query();
        if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
            breakDownQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNames));
        }
        if (!StringUtils.isEmpty(province) && !StringUtils.isEmpty(city) && CollectionUtils.isEmpty(deviceNames)) {
            throw new ExcelExportWithoutDataException("该日期暂无数据");
        }
        if (status != null) {
            breakDownQuery.addCriteria(Criteria.where(STATUS).is(status));
        }
        log.info("EXCEL导出告警日志  告警日志开始时间:{}---告警日志结束时间:{}", startTime, endTime);
        breakDownQuery.addCriteria(Criteria.where(GENERATION_DATA_TIME).gte(startTime).lte(endTime))
                .with(Sort.by(Sort.Order.desc("generationDataTime")));
        List<BreakDownLog> breakDownLogList = breakDownLogDao.findBatchBreakDownLogs(breakDownQuery);
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

    public ResHelper<Page<BreakDownLogVo>> queryBreakdownLog(Integer page, Integer pageSize, String deviceName, Integer order, String orderField,
                                                             Integer alarmType, Date startTime, Date endTime, String province, String city, String country, Boolean status) {
        Query breakDownQuery = new Query();
        if (!StringUtils.isEmpty(deviceName)) {
            breakDownQuery.addCriteria(Criteria.where(DEVICE_NAME).is(deviceName));
        }
        if (alarmType != null) {
            breakDownQuery.addCriteria(Criteria.where("alarmType").is(alarmType));
        }
        if (status != null) {
            breakDownQuery.addCriteria(Criteria.where(STATUS).is(status));
        }
        if (startTime != null && endTime != null) {
            breakDownQuery.addCriteria(Criteria.where(GENERATION_DATA_TIME).gte(startTime))
                    .addCriteria(Criteria.where(GENERATION_DATA_TIME).lte(endTime));
        }
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        List<EssStation> essStationList = essStationService.getEssStationList(user.getStationList());
        Query deviceQuery = new Query();
        if (!StringUtils.isEmpty(province)) {
            deviceQuery.addCriteria(Criteria.where(PROVINCE).is(province));
        }
        if (!StringUtils.isEmpty(city)) {
            deviceQuery.addCriteria(Criteria.where("city").is(city));
        }
        List<String> deviceNameList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(essStationList) && !essStationList.isEmpty()) {
            for (EssStation essStation : essStationList) {
                if (!CollectionUtils.isEmpty(essStation.getDeviceNameList()) && !essStation.getDeviceNameList().isEmpty()) {
                    deviceNameList.addAll(essStation.getDeviceNameList());
                }
            }
            if (StringUtils.isEmpty(deviceName)) {
                deviceQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNameList));
            }
        }
        List<String> deviceNames = deviceDao.findBatchDevices(deviceQuery).stream().map(Device::getDeviceName).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
            if (StringUtils.isEmpty(deviceName)) {
                breakDownQuery.addCriteria(Criteria.where(DEVICE_NAME).in(deviceNames));
            }
        } else {
            return ResHelper.success("", new Page<>(page, pageSize, 0, null));
        }
        if (!StringUtils.isEmpty(deviceName)) {
            if (!CollectionUtils.isEmpty(deviceNames) && !deviceNames.isEmpty()) {
                if (!deviceNames.contains(deviceName)) {
                    return ResHelper.success("", new Page<>(page, pageSize, 0, null));
                }
            }
        }
        Long totalRecord = breakDownLogDao.countNumber(breakDownQuery);
        breakDownQuery.skip(((long) page - 1) * pageSize).limit(pageSize);
        if (!org.apache.commons.lang.StringUtils.isEmpty(orderField)) {
            if (Const.DESC.equals(order)) {
                breakDownQuery.with(Sort.by(Sort.Order.desc("generationDataTime")));
            } else {
                breakDownQuery.with(Sort.by(Sort.Order.asc("generationDataTime")));
            }
        } else {
            breakDownQuery.with(Sort.by(Sort.Order.desc("generationDataTime")));
        }
        List<BreakDownLog> batchBreakDownLogs = breakDownLogDao.findBatchBreakDownLogs(breakDownQuery);
        List<BreakDownLogVo> breakDownLogBos = batchBreakDownLogs.stream().map(data -> {
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
        Page<BreakDownLogVo> breakDownLogVoPage = new Page<>(page, pageSize, totalRecord, breakDownLogBos);
        return ResHelper.success("", breakDownLogVoPage);
    }

    public Map<String, Object> queryBreakDownLogByTime(AlarmTime alarmTime) {
        String corpSecret = SecretUtil.decryptWithBase64(alarmTime.getAccessToken().replaceAll(" ", "+"), propertiesConfig.getSecretKey()).split("-")[1];
        Query corpQuery = new Query();
        corpQuery.addCriteria(Criteria.where("corpAccessSecret").is(corpSecret));
        Corp corp = corpDao.findCorp(corpQuery);
        Query breakDownLogQuery = new Query();
        breakDownLogQuery.addCriteria(Criteria.where(DEVICE_NAME).in(corp.getDeviceName()).and(GENERATION_DATA_TIME).gte(alarmTime.getStartTime()).lte(alarmTime.getEndTime()));
        List<BreakDownLog> breakDownLogs = breakDownLogDao.findBatchBreakDownLogs(breakDownLogQuery);
        Map<String, Object> result = new HashMap<>();
        List<AlarmThirdPartyVo> alarmThirdPartyVos = breakDownLogs.stream().map(breakDownLog -> {
            AlarmThirdPartyVo alarmThirdPartyVo = new AlarmThirdPartyVo();
            BeanUtils.copyProperties(breakDownLog, alarmThirdPartyVo);
            alarmThirdPartyVo.setDetailInformation(breakDownLog.getDetailInfomation());
            if (breakDownLog.getRemoveData() != null) {
                alarmThirdPartyVo.setRemoveDate(breakDownLog.getRemoveData().getTime());
            }
            alarmThirdPartyVo.setGenerationDataTime(breakDownLog.getGenerationDataTime().getTime());
            return alarmThirdPartyVo;
        }).collect(Collectors.toList());
        result.put("data", alarmThirdPartyVos);
        return result;
    }
}
