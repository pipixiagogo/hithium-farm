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
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.bugull.hithiumfarmweb.common.Const.*;
import static com.bugull.hithiumfarmweb.common.Const.GENERATION_DATA_TIME;

@Service
public class ExcelExportService {
    protected static final List<Integer> AMMETER_EQUIPMENT_IDS = new ArrayList<>();

    protected static final List<Integer> PRODUCTION_EQUIPMENT_IDS = new ArrayList<>();

    protected static final List<Integer> FIRE_AIR_UPS_EQUIPMENT_IDS = new ArrayList<>();
    protected static final List<Integer> PRODUCTION_FIRE_AIR_UPS_EQUIPMENT_IDS = new ArrayList<>();

    protected static final List<Integer> PRODUCTION_CABIN_IDS = new ArrayList<>();


    public static final List<Integer> EXPORT_DATA_TYPE = new ArrayList<>();

    static {
        AMMETER_EQUIPMENT_IDS.add(3);
        AMMETER_EQUIPMENT_IDS.add(6);
        AMMETER_EQUIPMENT_IDS.add(11);

        PRODUCTION_EQUIPMENT_IDS.add(4);
        PRODUCTION_EQUIPMENT_IDS.add(7);
        PRODUCTION_EQUIPMENT_IDS.add(12);

        FIRE_AIR_UPS_EQUIPMENT_IDS.add(1);
        FIRE_AIR_UPS_EQUIPMENT_IDS.add(12);
        FIRE_AIR_UPS_EQUIPMENT_IDS.add(13);

        PRODUCTION_FIRE_AIR_UPS_EQUIPMENT_IDS.add(2);
        PRODUCTION_FIRE_AIR_UPS_EQUIPMENT_IDS.add(13);
        PRODUCTION_FIRE_AIR_UPS_EQUIPMENT_IDS.add(14);
        PRODUCTION_FIRE_AIR_UPS_EQUIPMENT_IDS.add(55);

        PRODUCTION_CABIN_IDS.add(34);
        PRODUCTION_CABIN_IDS.add(53);
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
    @Resource
    private TemperatureMeterDataDicDao temperatureMeterDataDicDao;
    @Resource
    private EssStationDao essStationDao;
    @Resource
    private ImgUploadsDao imgUploadsDao;
    @Resource
    private PowerInstructionsDao powerInstructionsDao;
    @Resource
    private GridFsTemplate gridFsTemplate;
    @Resource
    private GridFSBucket gridFSBucket;

    /**
     * TODO  定时修改下发默认指令的值 settingResult
     */
    @Scheduled(cron = "${plan.curve.scheduled.time}")
    public void planCurveSetting() {
        Date startTime = DateUtils.getStartTime(new Date(System.currentTimeMillis()));
        Date endTime = DateUtils.addDateDays(startTime, 30);
        Criteria powerCriteria = new Criteria();
        powerCriteria.and("strategyTime").gte(startTime).lte(endTime);

        Criteria typeCriteria = new Criteria();
        typeCriteria.and("type").is(0);

        Criteria criteria = new Criteria();
        criteria.orOperator(powerCriteria, typeCriteria);

        Query updateQuery = new Query();
        updateQuery.addCriteria(criteria);

        Update update = new Update();
        update.set("settingResult", true);
        powerInstructionsDao.updateQuery(updateQuery, update);
        log.info("定时任务执行下发设备功率曲线成功:{}", new Date(System.currentTimeMillis()));
    }

    /**
     * 定时删除数据库中超过一段时间的excel文件 释放存储压力
     * <p>
     */
    @Scheduled(cron = "${excel.scheduled.remove.excel}")
    public void excelDel() {
        if (!propertiesConfig.isStartExportExcelSwitch()) {
            return;
        }
        Date date = DateUtils.addDateDays(new Date(), -propertiesConfig.getRemoveExcelDate());
        Query exportQuery = new Query();
        exportQuery.addCriteria(Criteria.where("recordTime").lte(date));
        List<ExportRecord> exportRecords = exportRecordDao.findBatch(exportQuery);
        log.info("启动定时删除excel任务,查询到可删除excel文件数量为:{}", exportRecords.size());
        Query gridFsRemoveQuery = new Query();
        gridFsRemoveQuery.addCriteria(Criteria.where("filename").in(exportRecords.stream().map(ExportRecord::getFilename).collect(Collectors.toList())));
        gridFsTemplate.delete(gridFsRemoveQuery);
        exportRecordDao.removeQuery(exportQuery);
    }


    /**
     * 定时删除磁盘中中超过一段时间的  最多 未绑定的2000张图片文件 释放存储压力
     * <p>
     * 一个月启动一次
     * 查看电站 电站下面未绑定的图片 即为过期图片
     */
    @Scheduled(cron = "${excel.scheduled.remove.excel}")
    public void uploadImgDel() {
        if (!propertiesConfig.isImageRemoveSwitch()) {
            return;
        }
        List<String> fileIds = new ArrayList<>();
        List<String> filePath = new ArrayList<>();
        List<EssStation> essStationList = essStationDao.findEssStationBatch(new Query());
        if (!CollectionUtils.isEmpty(essStationList) && !essStationList.isEmpty()) {
            for (EssStation essStation : essStationList) {
                if (!CollectionUtils.isEmpty(essStation.getStationImgIdWithUrls()) && !essStation.getStationImgIdWithUrls().isEmpty()) {
                    fileIds.addAll(essStation.getStationImgIdWithUrls().stream().map(img -> {
                        return img.split("~")[0];
                    }).collect(Collectors.toList()));
                }

            }
        }
        List<Device> deviceList = deviceDao.findBatchDevices(new Query());
        if (!CollectionUtils.isEmpty(deviceList) && !deviceList.isEmpty()) {
            for (Device device : deviceList) {
                if (CollectionUtils.isEmpty(device.getDeviceImgIdWithUrls()) && !device.getDeviceImgIdWithUrls().isEmpty()) {
                    fileIds.addAll(device.getDeviceImgIdWithUrls().stream().map(img -> {
                        return img.split("~")[0];
                    }).collect(Collectors.toList()));
                }
            }
        }
        Query query = new Query().addCriteria(Criteria.where("imgId").nin(fileIds));
        List<ImgUpload> imgUploads = imgUploadsDao.findBatchImgUploads(query);
        List<String> originalFilenames = imgUploads.stream().map(ImgUpload::getOriginalFilename).collect(Collectors.toList());
        imgUploads.stream().forEach(imgUpload -> {
            filePath.addAll(imgUpload.getImgOfFullPath());
        });
        for (String path : filePath) {
            File file = new File(path);
            if (file.exists()) {
                log.info("删除图片,删除图片路径名称:{}", path);
                file.delete();
            }
        }
        Query removeGrid = new Query().addCriteria(Criteria.where("_id").nin(fileIds));
        gridFsTemplate.delete(removeGrid);
        imgUploadsDao.removeBatch(query);
        log.info("删除图片文件与记录信息->文件名称为:{}", originalFilenames);

    }


    /**
     * 定时导出前一天excel数据
     */
    @Scheduled(cron = "${excel.scheduled.export.excel}")
    public void exportExcel() {
        if (!propertiesConfig.isStartExportExcelSwitch()) {
            return;
        }
        //当前分页查询导出200个设备 可根据线上情况 实时查看导出情况在决定该设备数量
        long count = deviceDao.count(new Query());
        int pageSize = propertiesConfig.getExcelExportPageSize();
        Date date = new Date();
        Date beforeDate = DateUtils.addDateDays(date, -1);
        String time = DateUtils.format(beforeDate);
        log.info("定时执行excel导出任务:设备总数量{},导出时间:{}", count, time);
        for (int i = 0; i < (int) Math.ceil((double) count / (double) pageSize); i++) {
            Query findBatchQuery = new Query();
            findBatchQuery.skip(i).limit(pageSize);
            List<Device> deviceList = deviceDao.findBatchDevices(findBatchQuery);
            for (Device device : deviceList) {
                for (int type : EXPORT_DATA_TYPE) {
                    threadPoolExecutor.execute(() -> {
                        long exportStart = System.currentTimeMillis();
                        String filename = device.getDeviceName() + "_" + time + "_" + type;
                        log.info("导出deviceName :{}", device.getDeviceName());
                        String filePath = propertiesConfig.getRealTimeDataExcelTempDir() + File.separator + device.getDeviceName() + "_" + time + "_" + type + ".xlsx";
                        Query fileNameQuery = new Query();
                        fileNameQuery.addCriteria(Criteria.where("filename").is(filename));
                        GridFSFile gridFSFile = gridFsTemplate.findOne(fileNameQuery);
                        if (gridFSFile != null) {
                            return;
                        }
                        if (exportRecordDao.exists(new Query().addCriteria(Criteria.where("filename").is(filename)))) {
                            return;
                        }
                        File file = new File(filePath);
                        OutputStream outputStream = null;
                        ExportRecord exportRecord = new ExportRecord();
                        try {
                            makeSureFileExist(file);
                            outputStream = new FileOutputStream(file);
                            Date nowDate = new Date(System.currentTimeMillis());
                            exportRecord.setRecordTime(nowDate);
                            exportRecord.setFilename(filename);
                            exportStationOfBattery(device.getDeviceName(), time, type, outputStream);
                            exportRecord.setExporting(true);
                            outputStream.close();
                            if (!exportRecordDao.exists(new Query().addCriteria(Criteria.where("filename").is(filename)))) {
                                gridFsTemplate.store(new FileInputStream(file), exportRecord.getFilename());
                                exportRecord.setFailReason("导出Excel成功");
                                exportRecordDao.saveExportRecord(exportRecord);
                            }
                            long exportEnd = System.currentTimeMillis();
                            log.info("导出excel花费时间:{},设备名称:{},导出设备类型:{}", (exportEnd - exportStart), device.getDeviceName(), getMsgByType(type));
                        } catch (Exception e) {
                            log.error("定时执行导出excel失败:{},导出excel数据时间为:{},设备名称:{},导出设备类型:{}", e.getMessage(), time, device.getDeviceName(), getMsgByType(type));
                            exportRecord.setExporting(false);
                            exportRecord.setFailReason(e.getMessage());
                            exportRecordDao.saveExportRecord(exportRecord);
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
     * @throws ParseException TODO 看情况是否启用多线程导出
     */
    public void exportStationOfBattery(String deviceName, String time, Integer type, OutputStream outputStream) throws
            ParseException {
        if (StringUtils.isEmpty(deviceName)) {
            throw new ExcelExportWithoutDataException("该日期暂无数据");
        }
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
        if (type == 0) {
            /**
             * 导出PCS数据
             */
            exportPcsData(outputStream, deviceName, startTime, endTime);
        } else if (type == 1) {
            /**
             * 导出电池堆数据
             */
            exportBamsData(outputStream, deviceName, startTime, endTime);
        } else if (type == 2) {
            /**
             * 导出电表数据
             */
            exportAmmeterData(outputStream, deviceName, startTime, endTime);
        } else if (type == 3) {
            /**
             * 附属件导出
             */
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
                Query equipmentIdQuery = new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and("enabled").is(true)
                        .and(EQUIPMENT_ID).gte(15).lte(33));
                List<Equipment> equipmentList = equipmentDao.findBatchEquipment(equipmentIdQuery);
                if (!CollectionUtils.isEmpty(equipmentList) && !equipmentList.isEmpty()) {
                    int i = 1;
                    for (int z = 0; i < equipmentList.size() + 1; i++, z++) {
                        List channelData = getPcsChannelData(deviceName, equipmentList.get(z).getEquipmentId(), startTime, endTime);
                        if (!CollectionUtils.isEmpty(channelData) && !channelData.isEmpty()) {
                            WriteSheet writeSheet2 = EasyExcelFactory.writerSheet(i, equipmentList.get(z).getName()).head(PcsChannelDic.class).build();
                            writer.write(channelData, writeSheet2);
                        }
                    }
                    List<Equipment> equipmentPcsCabin = equipmentList.stream().filter(equipment -> equipment.getEquipmentId() == 32).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(equipmentPcsCabin) && !equipmentPcsCabin.isEmpty()) {
                        List pcsCabinEquipmentData = getPcsCabinEquipmentData(deviceName, equipmentPcsCabin.get(0).getEquipmentId(), startTime, endTime);
                        if (!CollectionUtils.isEmpty(pcsCabinEquipmentData) && !pcsCabinEquipmentData.isEmpty()) {
                            WriteSheet writeSheet2 = EasyExcelFactory.writerSheet(i - 1, equipmentPcsCabin.get(0).getName()).head(TemperatureMeterDataDic.class).build();
                            writer.write(pcsCabinEquipmentData, writeSheet2);
                        }
                    }
                }
            }
        } finally {
            if (writer != null) {
                writer.finish();
            } else {
                throw new ExcelExportWithoutDataException("该日期暂无数据");
            }
        }
    }

    private List getPcsCabinEquipmentData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        Query query = new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(equipmentId).and(GENERATION_DATA_TIME)
                .gte(startTime).lte(endTime)).with(Sort.by(Sort.Order.desc(GENERATION_DATA_TIME)));
        return temperatureMeterDataDicDao.findBatchTemperature(query);
    }

    private void exportAmmeterData(OutputStream outputStream, String deviceName, Date startTime, Date endTime) {
        List<Equipment> equipmentList;
        if (propertiesConfig.getProductionDeviceNameList().contains(deviceName)) {
            equipmentList = equipmentDao.findBatchEquipment(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and("enabled").is(true).and(EQUIPMENT_ID).in(PRODUCTION_EQUIPMENT_IDS)));
        } else {
            equipmentList = equipmentDao.findBatchEquipment(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and("enabled").is(true).and(EQUIPMENT_ID).in(AMMETER_EQUIPMENT_IDS)));
        }
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
                    }
                }
            } finally {
                if (writer != null) {
                    writer.finish();
                } else {
                    throw new ExcelExportWithoutDataException("该日期暂无数据");
                }
            }
        }
    }


    private void exportUpsAirFireData(OutputStream outputStream, String deviceName, Date startTime, Date
            endTime) {
        List<Equipment> equipmentList;
        if (propertiesConfig.getProductionDeviceNameList().contains(deviceName)) {
            equipmentList = equipmentDao.findBatchEquipment(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and("enabled").is(true).and(EQUIPMENT_ID).in(PRODUCTION_FIRE_AIR_UPS_EQUIPMENT_IDS)));
        } else {
            equipmentList = equipmentDao.findBatchEquipment(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and("enabled").is(true).and(EQUIPMENT_ID).in(FIRE_AIR_UPS_EQUIPMENT_IDS)));
        }
        if (!CollectionUtils.isEmpty(equipmentList) && !equipmentList.isEmpty()) {
            ExcelWriter writer = null;
            try {
                for (int i = 0; i < equipmentList.size(); i++) {
                    if (propertiesConfig.getProductionDeviceNameList().contains(deviceName)) {
                        if (equipmentList.get(i).getEquipmentId() == 2) {
                            List<FireControlDataDic> fireControlDataDics = fireControlDataDicDao.findBatchFireControl(
                                    new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(equipmentList.get(i).getEquipmentId())
                                            .and(GENERATION_DATA_TIME).gte(startTime).lte(endTime)).with(Sort.by(Sort.Order.desc(GENERATION_DATA_TIME)))
                            );
                            if (!CollectionUtils.isEmpty(fireControlDataDics) && !fireControlDataDics.isEmpty()) {
                                if (writer == null) {
                                    writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                                }
                                WriteSheet writeSheet = EasyExcelFactory.writerSheet(i, equipmentList.get(i).getName()).head(FireControlDataDic.class).build();
                                writer.write(fireControlDataDics, writeSheet);
                            }
                        } else if (equipmentList.get(i).getEquipmentId() == 13) {
                            List<UpsPowerDataDic> upsPowerDataDics = upsPowerDataDicDao.findBatchUpsPower(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(equipmentList.get(i).getEquipmentId())
                                    .and(GENERATION_DATA_TIME).gte(startTime).lte(endTime)).with(Sort.by(Sort.Order.desc(GENERATION_DATA_TIME))));
                            if (!CollectionUtils.isEmpty(upsPowerDataDics) && !upsPowerDataDics.isEmpty()) {
                                if (writer == null) {
                                    writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                                }
                                WriteSheet writeSheet = EasyExcelFactory.writerSheet(i, equipmentList.get(i).getName()).head(UpsPowerDataDic.class).build();
                                writer.write(upsPowerDataDics, writeSheet);
                            }
                        } else if (equipmentList.get(i).getEquipmentId() == 14 || equipmentList.get(i).getEquipmentId() == 55) {
                            List<AirConditionDataDic> airConditionDataDics = airConditionDataDicDao.findBatchAirCondition(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(equipmentList.get(i).getEquipmentId())
                                    .and(GENERATION_DATA_TIME).gte(startTime).lte(endTime)).with(Sort.by(Sort.Order.desc(GENERATION_DATA_TIME))));
                            if (!CollectionUtils.isEmpty(airConditionDataDics) && !airConditionDataDics.isEmpty()) {
                                if (writer == null) {
                                    writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                                }
                                WriteSheet writeSheet = EasyExcelFactory.writerSheet(i, equipmentList.get(i).getName()).head(AirConditionDataDic.class).build();
                                writer.write(airConditionDataDics, writeSheet);
                            }
                        }
                    } else {
                        if (equipmentList.get(i).getEquipmentId() == 1) {
                            List<FireControlDataDic> fireControlDataDics = fireControlDataDicDao.findBatchFireControl(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(equipmentList.get(i).getEquipmentId())
                                    .and(GENERATION_DATA_TIME).gte(startTime).lte(endTime)).with(Sort.by(Sort.Order.desc(GENERATION_DATA_TIME))));
                            if (!CollectionUtils.isEmpty(fireControlDataDics) && !fireControlDataDics.isEmpty()) {
                                if (writer == null) {
                                    writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                                }
                                WriteSheet writeSheet = EasyExcelFactory.writerSheet(i, equipmentList.get(i).getName()).head(FireControlDataDic.class).build();
                                writer.write(fireControlDataDics, writeSheet);
                            }
                        } else if (equipmentList.get(i).getEquipmentId() == 12) {
                            List<UpsPowerDataDic> upsPowerDataDics = upsPowerDataDicDao.findBatchUpsPower(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(equipmentList.get(i).getEquipmentId())
                                    .and(GENERATION_DATA_TIME).gte(startTime).lte(endTime)).with(Sort.by(Sort.Order.desc(GENERATION_DATA_TIME))));
                            if (!CollectionUtils.isEmpty(upsPowerDataDics) && !upsPowerDataDics.isEmpty()) {
                                if (writer == null) {
                                    writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                                }
                                WriteSheet writeSheet = EasyExcelFactory.writerSheet(i, equipmentList.get(i).getName()).head(UpsPowerDataDic.class).build();
                                writer.write(upsPowerDataDics, writeSheet);
                            }
                        } else if (equipmentList.get(i).getEquipmentId() == 13) {
                            List<AirConditionDataDic> airConditionDataDics = airConditionDataDicDao.findBatchAirCondition(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(equipmentList.get(i).getEquipmentId())
                                    .and(GENERATION_DATA_TIME).gte(startTime).lte(endTime)).with(Sort.by(Sort.Order.desc(GENERATION_DATA_TIME))));
                            if (!CollectionUtils.isEmpty(airConditionDataDics) && !airConditionDataDics.isEmpty()) {
                                if (writer == null) {
                                    writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                                }
                                WriteSheet writeSheet = EasyExcelFactory.writerSheet(i, equipmentList.get(i).getName()).head(AirConditionDataDic.class).build();
                                writer.write(airConditionDataDics, writeSheet);
                            }
                        }
                    }
                }
            } finally {
                if (writer != null) {
                    writer.finish();
                } else {
                    throw new ExcelExportWithoutDataException("该日期暂无数据");
                }
            }
        } else {
            throw new ExcelExportWithoutDataException("该日期暂无数据");
        }
    }


    private List<List<String>> getVolHeard(String deviceName, Integer equipmentId) {
        BmsCellVoltDataDic bmsCellVoltDataDic = bmsCellVoltDataDicDao.findBmsCellVol(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(equipmentId)).with(Sort.by(Sort.Order.desc(GENERATION_DATA_TIME))));
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
        Map<String, Integer> volMap = bmsCellVoltDataDic.getVolMap();
        Iterator<Map.Entry<String, Integer>> iterator = volMap.entrySet().iterator();
        boolean flag = false;
        while (iterator.hasNext() && !flag) {
            Map.Entry<String, Integer> next = iterator.next();
            if (next.getValue() != 0) {
                List<String> head3 = new ArrayList<>();
                head3.add(next.getKey());
                list.add(head3);
            } else {
                flag = true;
            }
        }
        return list;
    }

    private List getVolData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        List<BmsCellVoltDataDic> bmsCellVoltDataDics = bmsCellVoltDataDicDao.findBatchQuery(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(equipmentId)
                .and(GENERATION_DATA_TIME).gte(startTime).lte(endTime)).with(Sort.by(Sort.Order.desc(GENERATION_DATA_TIME))));
        List<List<Object>> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bmsCellVoltDataDics) && !bmsCellVoltDataDics.isEmpty()) {
            for (BmsCellVoltDataDic bmsCellVoltDataDic : bmsCellVoltDataDics) {
                List<Object> data = new ArrayList<>();
                data.add(bmsCellVoltDataDic.getName());
                data.add(bmsCellVoltDataDic.getGenerationDataTime());
                data.add(bmsCellVoltDataDic.getEquipChannelStatus() == 0 ? "正常" : "异常");
                Map<String, Integer> volMap = bmsCellVoltDataDic.getVolMap();
                Iterator<Map.Entry<String, Integer>> iterator = volMap.entrySet().iterator();
                boolean flag = false;
                while (iterator.hasNext() && !flag) {
                    Map.Entry<String, Integer> next = iterator.next();
                    if (next.getValue() != 0) {
                        data.add(next.getValue());
                    } else {
                        flag = true;
                    }
                }
                list.add(data);
            }
        }
        return list;
    }

    private List getBcuData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        Query query = new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(equipmentId).and(GENERATION_DATA_TIME).gte(startTime).lte(endTime)).with(Sort.by(Sort.Order.desc(GENERATION_DATA_TIME)));
        return bcuDataDicBCUDao.findBatchBcuData(query);
    }

    private void exportBamsData(OutputStream outputStream, String deviceName, Date startTime, Date endTime) {
        List bamData = getDataBams(deviceName, startTime, endTime);
        if (!CollectionUtils.isEmpty(bamData) && !bamData.isEmpty()) {
            ExcelWriter writer = null;
            try {
                writer = EasyExcelFactory.write(outputStream).excelType(ExcelTypeEnum.XLSX).build();
                WriteSheet writeSheet = EasyExcelFactory.writerSheet(0, BAMS_DATA_SHEET_NAME).head(BamsDataDicBA.class).build();
                writer.write(bamData, writeSheet);
                List<Equipment> equipmentList;
                if (propertiesConfig.getProductionDeviceNameList().contains(deviceName)) {
                    equipmentList = equipmentDao.findBatchEquipment(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and("enabled").is(true).and(EQUIPMENT_ID).gte(36).lte(53)));
                } else {
                    equipmentList = equipmentDao.findBatchEquipment(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and("enabled").is(true).and(EQUIPMENT_ID).gte(35).lte(54)));
                }
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
                    if (propertiesConfig.getProductionDeviceNameList().contains(deviceName)) {
                        List<Equipment> equipmentCabinBattery = equipmentDao.findBatchEquipment(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName)
                                .and("enabled").is(true).and(EQUIPMENT_ID).in(PRODUCTION_CABIN_IDS)));
                        if (!CollectionUtils.isEmpty(equipmentCabinBattery) && !equipmentCabinBattery.isEmpty()) {
                            int k = y + 1;
                            for (Equipment equipment : equipmentCabinBattery) {
                                List data = getPcsCabinEquipmentData(deviceName, equipment.getEquipmentId(), startTime, endTime);
                                WriteSheet writeSheet1 = EasyExcelFactory.writerSheet(k, equipment.getName()).head(TemperatureMeterDataDic.class).build();
                                if (!CollectionUtils.isEmpty(data) && !data.isEmpty()) {
                                    writer.write(data, writeSheet1);
                                }
                                k++;
                            }
                        }
                    }
                }
            } finally {
                if (writer != null) {
                    writer.finish();
                } else {
                    throw new ExcelExportWithoutDataException("该日期暂无数据");
                }
            }
        } else {
            throw new ExcelExportWithoutDataException("该日期暂无数据");
        }

    }

    public List<BamsDataDicBA> getDataBams(String deviceName, Date startTime, Date endTime) {
        Query query = new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and(GENERATION_DATA_TIME).gte(startTime).lte(endTime))
                .with(Sort.by(Sort.Order.desc(GENERATION_DATA_TIME)));
        return bamsDataDicBADao.findBatchBamData(query);
    }

    //
//    public List<AirConditionExcelBo> getAirConditionData(String deviceName, Date startTime, Date endTime) {
//        List<AirConditionDataDic> airConditionDataDics = airConditionDataDicDao.query().is(DEVICE_NAME, deviceName)
//                .greaterThanEquals(GENERATION_DATA_TIME, startTime)
//                .lessThanEquals(GENERATION_DATA_TIME, endTime).results();
//        return airConditionDataDics.stream().map(airConditionDataDic -> {
//            AirConditionExcelBo airConditionExcelBo = new AirConditionExcelBo();
//            BeanUtils.copyProperties(airConditionDataDic, airConditionExcelBo);
//            //TODO 直接这样类型转换
//            airConditionExcelBo.setSensorFaultMsg(airConditionDataDic.getSensorFault() == 1 ? "故障" : "正常");
//            airConditionExcelBo.setArefactionOnhumidity(airConditionDataDic.getArefactionOnhumidity());
//            airConditionExcelBo.setArefactionOffhumidity(airConditionDataDic.getArefactionOffhumidity());
//            return airConditionExcelBo;
//        }).collect(Collectors.toList());
//    }
//
    public List<List<String>> getTempHeard(String deviceName, Integer equipmentId) {
        BmsCellTempDataDic bmsCellTempDataDic = bmsCellTempDataDicDao.findBmsCellTemp(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(equipmentId))
                .with(Sort.by(Sort.Order.desc(GENERATION_DATA_TIME))));
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
        Map<String, Integer> tempMap = bmsCellTempDataDic.getTempMap();
        Iterator<Map.Entry<String, Integer>> iterator = tempMap.entrySet().iterator();
        boolean flag = false;
        while (iterator.hasNext() && !flag) {
            Map.Entry<String, Integer> next = iterator.next();
            if (next.getValue() != 0) {
                List<String> head3 = new ArrayList<>();
                head3.add(next.getKey());
                list.add(head3);
            } else {
                flag = true;
            }
        }
        return list;
    }

    public List<List<Object>> getTempData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        List<BmsCellTempDataDic> bmsCellTempDataDics = bmsCellTempDataDicDao.findBatchBmsCellTemp(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(equipmentId)
                .and(GENERATION_DATA_TIME).gte(startTime).lte(endTime)).with(Sort.by(Sort.Order.desc(GENERATION_DATA_TIME))));
        List<List<Object>> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bmsCellTempDataDics) && !bmsCellTempDataDics.isEmpty()) {
            for (BmsCellTempDataDic bmsCellTempDataDic : bmsCellTempDataDics) {
                List<Object> data = new ArrayList<>();
                data.add(bmsCellTempDataDic.getName());
                data.add(bmsCellTempDataDic.getGenerationDataTime());
                data.add(bmsCellTempDataDic.getEquipChannelStatus() == 0 ? "正常" : "异常");
                Map<String, Integer> tempMap = bmsCellTempDataDic.getTempMap();
                Iterator<Map.Entry<String, Integer>> iterator = tempMap.entrySet().iterator();
                boolean flag = false;
                while (iterator.hasNext() && !flag) {
                    Map.Entry<String, Integer> next = iterator.next();
                    if (next.getValue() != 0) {
                        data.add(next.getValue());
                    } else {
                        flag = true;
                    }
                }
                list.add(data);
            }
        }
        return list;
    }

    private List getAmmeData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        return ammeterDataDicDao.findBatchAmmeter(new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(equipmentId).and(GENERATION_DATA_TIME).gte(startTime).lt(endTime)).with(Sort.by(Sort.Order.desc(GENERATION_DATA_TIME))));
    }

    private List getPcsChannelData(String deviceName, Integer equipmentId, Date startTime, Date endTime) {
        Query query = new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(equipmentId)
                .and(GENERATION_DATA_TIME).gte(startTime).lte(endTime)).with(Sort.by(Sort.Order.desc(GENERATION_DATA_TIME)));
        return pcsChannelDicDao.findBatchPcsChannel(query);
    }

    private List getPcsCabinData(String deviceName, Date startTime, Date endTime) {
        Query query = new Query().addCriteria(Criteria.where(DEVICE_NAME).is(deviceName).and(EQUIPMENT_ID).is(15)
                .and(GENERATION_DATA_TIME).gte(startTime).lte(endTime)).with(Sort.by(Sort.Order.desc(GENERATION_DATA_TIME)));
        return pcsCabinetDicDao.findBatchPcsCabinet(query);
    }
}
