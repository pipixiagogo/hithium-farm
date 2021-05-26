package com.bugull.hithiumfarmweb.http.service;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.bugull.hithiumfarmweb.common.Const;
import com.bugull.hithiumfarmweb.common.exception.ExcelExportWithoutDataException;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.dao.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.excelBo.AirConditionExcelBo;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.mongo.fs.BuguFS;
import com.bugull.mongo.fs.BuguFSFactory;
import com.bugull.mongo.fs.Uploader;
import com.bugull.mongo.utils.MapperUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.bugull.hithiumfarmweb.common.Const.*;
import static com.bugull.hithiumfarmweb.common.Const.GENERATION_DATA_TIME;

@Service
public class ExcelExportService {
    protected static final List<Integer> AMMETER_EQUIPMENT_IDS = new ArrayList<>();
    protected static final List<Integer> FIRE_AIR_UPS_EQUIPMENT_IDS = new ArrayList<>();
    public static final List<Integer> EXPORT_DATA_TYPE = new ArrayList<>();

    static {
        AMMETER_EQUIPMENT_IDS.add(3);
        AMMETER_EQUIPMENT_IDS.add(6);
        AMMETER_EQUIPMENT_IDS.add(11);
        FIRE_AIR_UPS_EQUIPMENT_IDS.add(1);
        FIRE_AIR_UPS_EQUIPMENT_IDS.add(12);
        FIRE_AIR_UPS_EQUIPMENT_IDS.add(13);
        for (int i = 0; i < 4; i++) {
            EXPORT_DATA_TYPE.add(i);
        }
    }
    public static final Logger log = LoggerFactory.getLogger(ExcelExportService.class);

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private PropertiesConfig propertiesConfig;
    @Resource
    private DeviceDao deviceDao;
    @Resource
    private ExportRecordDao exportRecordDao;
    @Resource
    private EquipmentDao equipmentDao;
    @Resource
    private FireControlDataDicDao fireControlDataDicDao;
    @Resource
    private UpsPowerDataDicDao upsPowerDataDicDao;
    @Resource
    private AirConditionDataDicDao airConditionDataDicDao;
    @Resource
    private BmsCellTempDataDicDao bmsCellTempDataDicDao;
    @Resource
    private BmsCellVoltDataDicDao bmsCellVoltDataDicDao;
    @Resource
    private BcuDataDicBCUDao bcuDataDicBCUDao;
    @Resource
    private BamsDataDicBADao bamsDataDicBADao;
    @Resource
    private AmmeterDataDicDao ammeterDataDicDao;
    @Resource
    private PcsChannelDicDao pcsChannelDicDao;
    @Resource
    private PcsCabinetDicDao pcsCabinetDicDao;

    /**
     * 定时删除数据库中超过一段时间的excel文件 释放存储压力
     *
     * 一个月启动一次
     */
    @Scheduled(cron = "${excel.scheld.export.excel}")
    public void sheldExcelDel(){
        if (!propertiesConfig.isStartExportExcelSwitch()) {
            return;
        }

        Date date = DateUtils.addDateMonths(new Date(), -3);
        List<ExportRecord> exportRecords = exportRecordDao.query().lessThanEquals("recordTime", date).results();
        log.info("启动定时删除excel任务,查询到可删除excel文件数量为:{}",exportRecords.size());
        BuguFS fs = BuguFSFactory.getInstance().create();
        for(ExportRecord exportRecord:exportRecords){
            fs.remove(exportRecord.getFilename());
            exportRecordDao.remove(exportRecord);
            log.info("删除excel,时间为:{}",exportRecord.getRecordTime());
        }
    }
    /**
     * 定时导出前一天excel数据
     */
    @Scheduled(cron = "${excel.scheld.export.excel}")
    public void scheldExportExcel() {
        System.out.println("执行了");
        if (!propertiesConfig.isStartExportExcelSwitch()) {
            return;
        }
        //当前分页查询导出200个设备 可根据线上情况 实时查看导出情况在决定该设备数量
        long count = deviceDao.count();
        int pageSize = propertiesConfig.getExcelExportPageSize();
        Date date = new Date();
        Date beforeDate = DateUtils.addDateDays(date, -1);
        String time = DateUtils.format(beforeDate);
        log.info("定时执行excel导出任务:设备总数量{},导出时间:{}", count, time);
        for (int i = 0; i < (int) Math.ceil((double) count / (double) pageSize); i++) {
            List<Device> deviceList = deviceDao.query().pageSize(pageSize).pageNumber(i + 1).results();
            for (Device device : deviceList) {
                for (int type : EXPORT_DATA_TYPE) {
                    threadPoolExecutor.execute(() -> {
                        String filePath = propertiesConfig.getRealTimeDataExcelTempDir() + File.separator + device.getDeviceName()+ "_" + time + "_" + type + ".xlsx";
                        DBObject query = new BasicDBObject();
                        query.put("filename", device.getDeviceName() + "_" + time + "_" + type);
                        BuguFS fs = BuguFSFactory.getInstance().create();
                        GridFSDBFile f = fs.findOne(query);
                        if(f != null){
                            return;
                        }
                        if(exportRecordDao.query().is("filename", device.getDeviceName() + "_" + time + "_" + type).exists()){
                            return;
                        }
                        File file = new File(filePath);
                        OutputStream outputStream = null;
                        ExportRecord exportRecord = new ExportRecord();
                        try {
                            makeSureFileExist(file);
                            outputStream = new FileOutputStream(file);
                            Date nowDate = new Date();
                            exportRecord.setRecordTime(nowDate);
                            String filename = device.getDeviceName() + "_" + time + "_" + type;
                            exportRecord.setFilename(filename);
                            exportStationOfBattery(device.getDeviceName(), time, type, outputStream);
                            exportRecord.setExporting(true);
                            outputStream.close();
                            if(!exportRecordDao.query().is("filename",filename).exists()){
                                Uploader uploader = new Uploader(file, exportRecord.getFilename(), false);
                                uploader.save();
                                exportRecordDao.insert(exportRecord);
                            }
                        } catch (Exception e) {
                            log.error("定时执行导出excel失败:{},导出excel数据时间为:{},设备名称:{},导出设备类型:{}", e.getMessage(), time, device.getDeviceName(), getMsgByType(type));
                            exportRecord.setExporting(false);
                            exportRecord.setFailReason(e.getMessage());
                            exportRecordDao.insert(exportRecord);
                        } finally {
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            file.delete();
                        }
                    });
                }
            }
        }
    }

    public String getMsgByType(Integer type) {
        switch (type) {
            case 0:
                return "PCS";
            case 1:
                return "电池堆";
            case 2:
                return "电表";
            case 3:
                return "附属件";
            default:
                return "未知数据";
        }
    }

    private void makeSureFileExist(File file) throws IOException {
        if (file.exists()) {
            return;
        }
        synchronized (Thread.currentThread()) {
            if (!file.exists()) {
                file.createNewFile();
            }
        }
    }
    /**
     * @param deviceName
     * @param time
     * @param type
     * @param outputStream
     * @throws ParseException
     *
     * TODO 看情况是否启用多线程导出
     */
    public void exportStationOfBattery(String deviceName, String time, Integer type, OutputStream outputStream) throws ParseException {
        if (StringUtils.isEmpty(deviceName)) {
            throw new ExcelExportWithoutDataException("该日期暂无数据");
        }
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
        if (type == 0) {
            exportPcsData(outputStream, deviceName, startTime, endTime);
        } else if (type == 1) {
            exportBamsData(outputStream, deviceName, startTime, endTime);
        } else if (type == 2) {
            exportAmmeterData(outputStream, deviceName, startTime, endTime);
        } else if (type == 3) {
            exportUpsAirFireData(outputStream, deviceName, startTime, endTime);
        } else {
            throw new ExcelExportWithoutDataException("暂无该类型数据");
        }
    }
    private void exportPcsData(OutputStream outputStream, String deviceName, Date startTime, Date endTime) {
        ExcelWriter writer = null;
        List pcsCabinData = getPcsCabinData(deviceName, startTime, endTime);
        try {
            if (!CollectionUtils.isEmpty(pcsCabinData) && !pcsCabinData.isEmpty()) {
                writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                WriteSheet writeSheet = EasyExcelFactory.writerSheet(0, PCS_DATA_SHEET_NAME).head(PcsCabinetDic.class).build();
                writer.write(pcsCabinData, writeSheet);
                List<Equipment> equipmentList = equipmentDao.query().is(DEVICE_NAME, deviceName).is("enabled", true).greaterThan("equipmentId", 15).lessThan("equipmentId", 32).results();
                if (!CollectionUtils.isEmpty(equipmentList) && !equipmentList.isEmpty()) {
                    for (int i = 1, z = 0; i < equipmentList.size() + 1; i++, z++) {
                        List channelData = getPcsChannelData(deviceName, equipmentList.get(z).getEquipmentId(), startTime, endTime);
                        if (!CollectionUtils.isEmpty(channelData) && !channelData.isEmpty()) {
                            WriteSheet writeSheet2 = EasyExcelFactory.writerSheet(i, equipmentList.get(z).getName()).head(PcsChannelDic.class).build();
                            writer.write(channelData, writeSheet2);
                        }
                    }
                }
            } else {
                throw new ExcelExportWithoutDataException("该日期暂无数据");
            }
        } finally {
            if (writer != null) {
                writer.finish();
            }
        }
    }
    private void exportAmmeterData(OutputStream outputStream, String deviceName, Date startTime, Date endTime) {
        List<Equipment> equipmentList = equipmentDao.query().is(DEVICE_NAME, deviceName).is("enabled", true).in("equipmentId", AMMETER_EQUIPMENT_IDS).results();
        if (!CollectionUtils.isEmpty(equipmentList) && !equipmentList.isEmpty()) {
            ExcelWriter writer = null;
            try {
                for (int i = 0; i < equipmentList.size(); i++) {
                    List ammeterData = getAmmeData(deviceName, equipmentList.get(i).getEquipmentId(), startTime, endTime);
                    if (!CollectionUtils.isEmpty(ammeterData) && !ammeterData.isEmpty()) {
                        if (writer == null) {
                            writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                        }
                        WriteSheet writeSheet = EasyExcelFactory.writerSheet(i, equipmentList.get(i).getName()).head(AmmeterDataDic.class).build();
                        writer.write(ammeterData, writeSheet);
                    } else {
                        throw new ExcelExportWithoutDataException("该日期暂无数据");
                    }
                }
            } finally {
                if (writer != null) {
                    writer.finish();
                }
            }
        }
    }


    private void exportUpsAirFireData(OutputStream outputStream, String deviceName, Date startTime, Date endTime) {
        List<Equipment> equipmentList = equipmentDao.query().is(DEVICE_NAME, deviceName).is("enabled", true).in("equipmentId", FIRE_AIR_UPS_EQUIPMENT_IDS)
                .results();
        if (!CollectionUtils.isEmpty(equipmentList) && !equipmentList.isEmpty()) {
            ExcelWriter writer = null;
            try {
                for (int i = 0; i < equipmentList.size(); i++) {
                    if (equipmentList.get(i).getEquipmentId() == 1) {
                        List<FireControlDataDic> fireControlDataDics = fireControlDataDicDao.query().is(DEVICE_NAME, deviceName).is("equipmentId", equipmentList.get(i).getEquipmentId())
                                .greaterThanEquals(GENERATION_DATA_TIME, startTime).lessThanEquals(GENERATION_DATA_TIME, endTime).results();
                        if (!CollectionUtils.isEmpty(fireControlDataDics) && !fireControlDataDics.isEmpty()) {
                            if (writer == null) {
                                writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                            }
                            WriteSheet writeSheet = EasyExcelFactory.writerSheet(i, equipmentList.get(i).getName()).head(FireControlDataDic.class).build();
                            writer.write(fireControlDataDics, writeSheet);
                        }
                    } else if (equipmentList.get(i).getEquipmentId() == 12) {
                        List<UpsPowerDataDic> upsPowerDataDics = upsPowerDataDicDao.query().is(DEVICE_NAME, deviceName).is("equipmentId", equipmentList.get(i).getEquipmentId())
                                .greaterThanEquals(GENERATION_DATA_TIME, startTime).lessThanEquals(GENERATION_DATA_TIME, endTime).results();
                        if (!CollectionUtils.isEmpty(upsPowerDataDics) && !upsPowerDataDics.isEmpty()) {
                            if (writer == null) {
                                writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                            }
                            WriteSheet writeSheet = EasyExcelFactory.writerSheet(i, equipmentList.get(i).getName()).head(UpsPowerDataDic.class).build();
                            writer.write(upsPowerDataDics, writeSheet);
                        }
                    } else if (equipmentList.get(i).getEquipmentId() == 13) {
                        List<AirConditionDataDic> airConditionDataDics = airConditionDataDicDao.query().is(DEVICE_NAME, deviceName).is("equipmentId", equipmentList.get(i).getEquipmentId())
                                .greaterThanEquals(GENERATION_DATA_TIME, startTime).lessThanEquals(GENERATION_DATA_TIME, endTime).results();
                        if (!CollectionUtils.isEmpty(airConditionDataDics) && !airConditionDataDics.isEmpty()) {
                            if (writer == null) {
                                writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                            }
                            WriteSheet writeSheet = EasyExcelFactory.writerSheet(i, equipmentList.get(i).getName()).head(AirConditionDataDic.class).build();
                            writer.write(airConditionDataDics, writeSheet);
                        }
                    }
                }
                if (writer == null) {
                    throw new ExcelExportWithoutDataException("该日期暂无数据");
                }
            } finally {
                if (writer != null) {
                    writer.finish();
                }
            }

        }
    }



    private List<List<String>> getVolHeard(String deviceName, Integer equipmentId) {
        Iterable<DBObject> cellVolIte = bmsCellVoltDataDicDao.aggregate().match(bmsCellVoltDataDicDao.query().is(DEVICE_NAME, deviceName).is("equipmentId", equipmentId))
                .sort("{generationDataTime:-1}").limit(1).results();
        BmsCellVoltDataDic bmsCellVoltDataDic = null;
        if (cellVolIte != null) {
            for (DBObject object : cellVolIte) {
                bmsCellVoltDataDic = MapperUtil.fromDBObject(BmsCellVoltDataDic.class, object);
                Map<String, Integer> volMap = bmsCellVoltDataDic.getVolMap();
                Iterator<Map.Entry<String, Integer>> iterator = volMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> next = iterator.next();
                    if (next.getValue() == 0) {
                        iterator.remove();
                    }
                }
            }
        }
        List<List<String>> list = new ArrayList<>();
        List<String> head0 = new ArrayList<>();
        head0.add("设备名称");
        List<String> head1 = new ArrayList<>();
        head1.add("时间");
        List<String> head2 = new ArrayList<>();
        head2.add("前置机通道状态");
        list.add(head0);
        list.add(head1);
        list.add(head2);
        if (bmsCellVoltDataDic != null) {
            for (Map.Entry<String, Integer> entry : bmsCellVoltDataDic.getVolMap().entrySet()) {
                List<String> head3 = new ArrayList<>();
                head3.add(entry.getKey());
                list.add(head3);
            }
        }
        return list;
    }

    private List getVolData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        List<BmsCellVoltDataDic> bmsCellVoltDataDics = bmsCellVoltDataDicDao.query().
                is(DEVICE_NAME, deviceName)
                .is("equipmentId", equipmentId)
                .greaterThanEquals(GENERATION_DATA_TIME, startTime)
                .lessThanEquals(GENERATION_DATA_TIME, endTime).results();
        List<List<Object>> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bmsCellVoltDataDics) && !bmsCellVoltDataDics.isEmpty()) {
            for (BmsCellVoltDataDic bmsCellVoltDataDic : bmsCellVoltDataDics) {
                List<Object> data = new ArrayList<>();
                data.add(bmsCellVoltDataDic.getName());
                data.add(bmsCellVoltDataDic.getGenerationDataTime());
                data.add(bmsCellVoltDataDic.getEquipChannelStatus() == 0 ? "正常" : "异常");
                Map<String, Integer> volMap = bmsCellVoltDataDic.getVolMap();
                Iterator<Map.Entry<String, Integer>> iterator = volMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> next = iterator.next();
                    if (next.getValue() == 0) {
                        iterator.remove();
                    }
                }
                for (Map.Entry<String, Integer> entry : volMap.entrySet()) {
                    data.add(entry.getValue());
                }
                list.add(data);
            }
        }
        return list;
    }

    private List getBcuData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        return bcuDataDicBCUDao.query().is(DEVICE_NAME, deviceName).is("equipmentId", equipmentId)
                .greaterThanEquals(GENERATION_DATA_TIME, startTime)
                .lessThanEquals(GENERATION_DATA_TIME, endTime).results();
    }

    private void exportBamsData(OutputStream outputStream, String deviceName, Date startTime, Date endTime) {
        List bamsData = getDataBams(deviceName, startTime, endTime);
        if (!CollectionUtils.isEmpty(bamsData) && !bamsData.isEmpty()) {
            ExcelWriter writer = null;
            try {
                writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                WriteSheet writeSheet = EasyExcelFactory.writerSheet(0, BAMS_DATA_SHEET_NAME).head(BamsDataDicBA.class).build();
                writer.write(bamsData, writeSheet);
                List<Equipment> equipmentList = equipmentDao.query().is(DEVICE_NAME, deviceName).is("enabled", true).greaterThan("equipmentId", 35).lessThan("equipmentId", 54).results();
                if (!CollectionUtils.isEmpty(equipmentList) && !equipmentList.isEmpty()) {
                    int z = 0;
                    int j;
                    for (j = 1; j <= equipmentList.size(); j++, z++) {
                        List data = getBcuData(deviceName, equipmentList.get(z).getEquipmentId(), startTime, endTime);
                        WriteSheet writeSheet1 = EasyExcelFactory.writerSheet(j, equipmentList.get(z).getName()).head(BcuDataDicBCU.class).build();
                        if (!CollectionUtils.isEmpty(data) && !data.isEmpty()) {
                            writer.write(data, writeSheet1);
                        }
                    }
                    int x;
                    for (x = j + 1, z = 0; x <= equipmentList.size() + j; x++, z++) {
                        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
                        WriteFont headWriteFont = new WriteFont();
                        headWriteFont.setFontHeightInPoints((short) 12);
                        headWriteCellStyle.setWriteFont(headWriteFont);
                        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                                new HorizontalCellStyleStrategy(headWriteCellStyle, new WriteCellStyle());
                        List data = getTempData(deviceName, equipmentList.get(z).getEquipmentId(), startTime, endTime);
                        if (!CollectionUtils.isEmpty(data) && !data.isEmpty()) {
                            WriteSheet writeSheet2 = EasyExcelFactory.writerSheet(x, equipmentList.get(z).getName() + "电芯温度")
                                    .registerWriteHandler(horizontalCellStyleStrategy).head(getTempHeard(deviceName, equipmentList.get(z).getEquipmentId())).build();
                            writer.write(data, writeSheet2);
                        }
                    }
                    int y;
                    for (y = x + 1, z = 0; y <= equipmentList.size() + x; y++, z++) {
                        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
                        WriteFont headWriteFont = new WriteFont();
                        headWriteFont.setFontHeightInPoints((short) 12);
                        headWriteCellStyle.setWriteFont(headWriteFont);
                        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                                new HorizontalCellStyleStrategy(headWriteCellStyle, new WriteCellStyle());
                        List data = getVolData(deviceName, equipmentList.get(z).getEquipmentId(), startTime, endTime);
                        if (!CollectionUtils.isEmpty(data) && !data.isEmpty()) {
                            WriteSheet writeSheet3 = EasyExcelFactory.writerSheet(y, equipmentList.get(z).getName() + "电芯电压")
                                    .registerWriteHandler(horizontalCellStyleStrategy).head(getVolHeard(deviceName, equipmentList.get(z).getEquipmentId())).build();
                            writer.write(data, writeSheet3);
                        }
                    }
                }
            } finally {
                if (writer != null) {
                    writer.finish();
                }
            }
        } else {
            throw new ExcelExportWithoutDataException("该日期暂无数据");
        }
    }

    public List<BamsDataDicBA> getDataBams(String deviceName, Date startTime, Date endTime) {
        return bamsDataDicBADao.query().is(DEVICE_NAME, deviceName)
                .greaterThanEquals(GENERATION_DATA_TIME, startTime)
                .lessThanEquals(GENERATION_DATA_TIME, endTime).results();
    }

    public List<AirConditionExcelBo> getAirConditionData(String deviceName, Date startTime, Date endTime) {
        List<AirConditionDataDic> airConditionDataDics = airConditionDataDicDao.query().is(DEVICE_NAME, deviceName)
                .greaterThanEquals(GENERATION_DATA_TIME, startTime)
                .lessThanEquals(GENERATION_DATA_TIME, endTime).results();
        return airConditionDataDics.stream().map(airConditionDataDic -> {
            AirConditionExcelBo airConditionExcelBo = new AirConditionExcelBo();
            BeanUtils.copyProperties(airConditionDataDic, airConditionExcelBo);
            //TODO 直接这样类型转换
            airConditionExcelBo.setSensorFaultMsg(airConditionDataDic.getSensorFault() == 1 ? "故障" : "正常");
            airConditionExcelBo.setArefactionOnhumidity(airConditionDataDic.getArefactionOnhumidity());
            airConditionExcelBo.setArefactionOffhumidity(airConditionDataDic.getArefactionOffhumidity());
            return airConditionExcelBo;
        }).collect(Collectors.toList());
    }

    public List<List<String>> getTempHeard(String deviceName, Integer equipmentId) {
        Iterable<DBObject> cellTempIte = bmsCellTempDataDicDao.aggregate().match(bmsCellTempDataDicDao.query().is(DEVICE_NAME, deviceName)
                .is("equipmentId", equipmentId)).sort("{generationDataTime:-1}").limit(1).results();
        BmsCellTempDataDic bmsCellTempDataDic = null;
        if (cellTempIte != null) {
            for (DBObject object : cellTempIte) {
                bmsCellTempDataDic = MapperUtil.fromDBObject(BmsCellTempDataDic.class, object);
                Map<String, Integer> tempMap = bmsCellTempDataDic.getTempMap();
                Iterator<Map.Entry<String, Integer>> iterator = tempMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> next = iterator.next();
                    if (next.getValue() == 0) {
                        iterator.remove();
                    }
                }
            }
        }
        List<List<String>> list = new ArrayList<>();
        List<String> head0 = new ArrayList<>();
        head0.add("设备名称");
        List<String> head1 = new ArrayList<>();
        head1.add("时间");
        List<String> head2 = new ArrayList<>();
        head2.add("前置机通道状态");
        list.add(head0);
        list.add(head1);
        list.add(head2);
        if (bmsCellTempDataDic != null) {
            for (Map.Entry<String, Integer> entry : bmsCellTempDataDic.getTempMap().entrySet()) {
                List<String> head3 = new ArrayList<>();
                head3.add(entry.getKey());
                list.add(head3);
            }
        }
        return list;
    }


    public List<List<Object>> getTempData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        List<BmsCellTempDataDic> bmsCellTempDataDics = bmsCellTempDataDicDao.query().
                is(DEVICE_NAME, deviceName)
                .is("equipmentId", equipmentId)
                .greaterThanEquals(GENERATION_DATA_TIME, startTime)
                .lessThanEquals(GENERATION_DATA_TIME, endTime).results();
        List<List<Object>> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bmsCellTempDataDics) && !bmsCellTempDataDics.isEmpty()) {
            for (BmsCellTempDataDic bmsCellTempDataDic : bmsCellTempDataDics) {
                List<Object> data = new ArrayList<>();
                data.add(bmsCellTempDataDic.getName());
                data.add(bmsCellTempDataDic.getGenerationDataTime());
                data.add(bmsCellTempDataDic.getEquipChannelStatus() == 0 ? "正常" : "异常");
                Map<String, Integer> tempMap = bmsCellTempDataDic.getTempMap();
                Iterator<Map.Entry<String, Integer>> iterator = tempMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> next = iterator.next();
                    if (next.getValue() == 0) {
                        iterator.remove();
                    }
                }
                for (Map.Entry<String, Integer> entry : tempMap.entrySet()) {
                    data.add(entry.getValue());
                }
                list.add(data);
            }
        }

        return list;
    }



    private List getAmmeData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        return ammeterDataDicDao.query().is(DEVICE_NAME, deviceName).is("equipmentId", equipmentId)
                .greaterThanEquals(GENERATION_DATA_TIME, startTime)
                .lessThanEquals(GENERATION_DATA_TIME, endTime).results();
    }



    private List getPcsChannelData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        return pcsChannelDicDao.query().is(DEVICE_NAME, deviceName).is("equipmentId", equipmentId)
                .greaterThanEquals(GENERATION_DATA_TIME, startTime)
                .lessThanEquals(GENERATION_DATA_TIME, endTime).results();
    }

    private List getPcsCabinData(String deviceName, Date startTime, Date endTime) {
        return pcsCabinetDicDao.query().is(DEVICE_NAME, deviceName)
                .is("equipmentId", 15)
                .greaterThanEquals(GENERATION_DATA_TIME, startTime)
                .lessThanEquals(GENERATION_DATA_TIME, endTime).results();
    }
}
