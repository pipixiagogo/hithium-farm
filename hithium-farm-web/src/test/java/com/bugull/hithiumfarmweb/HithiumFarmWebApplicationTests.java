package com.bugull.hithiumfarmweb;


import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.common.Const;
import com.bugull.hithiumfarmweb.config.CustomThreadFactory;
import com.bugull.hithiumfarmweb.dao.BcuEntityDao;
import com.bugull.hithiumfarmweb.dao.CaEntityDao;
import com.bugull.hithiumfarmweb.dao.TestExpireIndexDao;
import com.bugull.hithiumfarmweb.entity.BcuEntity;
import com.bugull.hithiumfarmweb.entity.CaEntity;
import com.bugull.hithiumfarmweb.entity.TestExpireIndex;
import com.bugull.hithiumfarmweb.enumerate.DataType;
import com.bugull.hithiumfarmweb.http.bo.StatisticBo;
import com.bugull.hithiumfarmweb.http.bo.TimeOfPriceBo;
import com.bugull.hithiumfarmweb.http.dao.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.service.RealTimeDataService;
import com.bugull.hithiumfarmweb.http.vo.CapacityVo;
import com.bugull.hithiumfarmweb.http.vo.IncomeStatisticOfDayVo;
import com.bugull.hithiumfarmweb.http.vo.IncomeStatisticVo;
import com.bugull.hithiumfarmweb.http.vo.PriceOfPercenVo;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.PatternUtil;
import com.bugull.hithiumfarmweb.utils.UUIDUtil;
import com.bugull.mongo.BuguConnection;
import com.bugull.mongo.BuguFramework;
import com.bugull.mongo.BuguQuery;
import com.bugull.mongo.fs.BuguFS;
import com.bugull.mongo.fs.BuguFSFactory;
import com.bugull.mongo.fs.Uploader;
import com.bugull.mongo.utils.MapperUtil;
import com.google.common.eventbus.EventBus;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import io.swagger.models.auth.In;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.integration.history.MessageHistory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bugull.hithiumfarmweb.common.Const.*;
import static com.bugull.hithiumfarmweb.http.service.StatisticsService.INCOME_RECORD_PREFIX;

public class HithiumFarmWebApplicationTests {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    @Test
//    public void getResultValue(){
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("172.24.63.229:27017");
//        conn2.setPort(27017);
//        conn2.setUsername("farm");
//        conn2.setPassword("hithium.db.mongo");
//        conn2.setDatabase("farm");
//        conn2.connect();
////        {$dateToString:{format:'%Y-%m-%d',date:{$add:['$generationDataTime',28800000]}}}
////{_id:{$dateToString:{format:'%Y-%m-%d %H',date:{$add:['$generationDataTime',28800000]}}}
//        BcuEntityDao bcuEntityDao = new BcuEntityDao();
////        Iterable <DBObject>results = bcuDataDicBCUDao.aggregate().match(bcuDataDicBCUDao.query().is("deviceName", "clld3w7tdxjhvuhf6gzn60whffuy0hyj").is("name", "集装箱电池簇6"))
////                .group("{_id:{$dateToString:{format:'%Y-%m-%d',date:{$add:['$generationDataTime',28800000]}}},sum:{$max:{$toDouble:'$cellTemperatureDiff'}}}").results();
//
////        Iterable <DBObject>results = bcuDataDicBCUDao.aggregate().match(bcuDataDicBCUDao.query().is("deviceName", "clld3w7tdxjhvuhf6gzn60whffuy0hyj").is("name", "集装箱电池簇6"))
////                .group("{_id:{$dateToString:{format:'%Y-%m-%d',date:{$add:['$generationDataTime',28800000]}}},sum:{$max:{$toDouble:'$maxSingleTemperature'}}}").results();
////        Iterable <DBObject>results = bcuDataDicBCUDao.aggregate().match(bcuDataDicBCUDao.query().is("deviceName", "clld3w7tdxjhvuhf6gzn60whffuy0hyj").is("name", "集装箱电池簇6"))
////                .group("{_id:{$dateToString:{format:'%Y-%m-%d',date:{$add:['$generationDataTime',28800000]}}},sum:{$max:{$toDouble:'$avgSingleTemperature'}}}").results();
//        Date date = DateUtils.addDateDays(new Date(), -1);
//        Date startTime = DateUtils.getStartTime(date);
//        Date endTime = DateUtils.getEndTime(date);
//        List<BcuEntity> results = bcuEntityDao.query().is("deviceName","clld3w7tdxjhvuhf6gzn60whffuy0hyj").is("name","集装箱电池簇6").greaterThan("generationDataTime", startTime).lessThan("generationDataTime", endTime)
//                .returnFields("_id","deviceName","name","avgSingleTemperature","maxSingleTemperature","groupCurrent","generationDataTime").results();
//        Map<String,String>map=new TreeMap<>();
//        for(BcuEntity bcuEntity:results){
//            map.put(sdf.format(bcuEntity.getGenerationDataTime()),JSONObject.toJSONString(bcuEntity));
//        }
//        Set<Map.Entry<String, String>> entries = map.entrySet();
//        for(Map.Entry<String, String> entry:entries){
//            System.out.println(entry.getKey()+entry.getValue());
//        }
//        roupCurrent
//        if(results!=null){
//            Map<String,Double> map = new TreeMap<>();
//            for(DBObject object:results){
//                String id = (String) object.get("_id");
//                Double sum=(Double) object.get("sum");
//                map.put(id,sum);
//            }
//            Set<Map.Entry<String, Double>> entries = map.entrySet();
//            for(Map.Entry<String, Double> entry:entries){
//                System.out.println(entry.getKey()+"---"+entry.getValue());
//            }
//        }
//        for(DBObject object :results){
//            BcuDataDicBCU bcuDataDicBCU = MapperUtil.fromDBObject(bcuDataDicBCUDao.getEntityClass(),  object);
//            System.out.println(bcuDataDicBCU.getId());
//        }
//    }

    @Test
    public void testFileExist(){
        String value="[\"clld3w7tdxjhvuhf6gzn60whffuy0hyj\",\"2021-08-31\",\"电池堆数据\",0]";
        String s = value.replaceAll("\"", "").replaceAll("\\]","").replaceAll("\\[","");
        System.out.println(s);
//        BigDecimal bigDecimal  = new BigDecimal("0");
//        BigDecimal bigDecimal1 = new BigDecimal("0");
//        System.out.println(bigDecimal1.compareTo(bigDecimal));
//        File file = new File("D://d92b934a1b474ca8975de89fa748225.jpg");
//        System.out.println(file.exists());
//        file.deleteOnExit();
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("192.168.241.151");
//        conn2.setPort(27017);
//        conn2.setUsername("ess");
//        conn2.setPassword("ess");
//        conn2.setDatabase("ess");
//        conn2.connect();
//        CaEntityDao caEntityDao = new CaEntityDao();
//
////        CaEntity caEntity = new CaEntity();
//        List<String> list = new ArrayList<>();
//        for(int i=0;i<9;i++){
//            list.add(String.valueOf(i));
//        }
//        caEntity.setList(list);
//
//        CaEntity list1 = caEntityDao.query().is("list", list).result();
//        System.out.println(list1);
//        System.out.println(list1.getId());
//        List<String> result=new ArrayList<>();
//
//        result.add(String.valueOf(11));
//        result.add(String.valueOf(10));
//        result.add(String.valueOf(15));
//        boolean list = caEntityDao.query().in("list", result).exists();
//        System.out.println(list);

    }
    @Test
    public void contextLoads() {
//        String salt = RandomStringUtils.randomAlphanumeric(20);
//        String s = new Sha256Hash("123456", salt).toHex();
//        System.out.println(salt);
//        System.out.println(s);
//        String s = null;
//        System.out.println(StringUtils.isEmpty(s));

//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("192.168.241.145").setPort(27017).setUsername("ess").setPassword("ess").setDatabase("ess").connect();
        List<String> list1 = new ArrayList<String>();
        list1.add("1");
        list1.add("2");
        list1.add("3");
        list1.add("5");
        list1.add("6");

        List<String> list2 = new ArrayList<String>();
        list2.add("2");
        list2.add("3");
        list2.add("7");
        list2.add("8");

        // 交集
        List<String> intersection = list1.stream().filter(item -> list2.contains(item)).collect(Collectors.toList());
        System.out.println("---交集 intersection---");
        intersection.parallelStream().forEach(System.out::println);

        // 差集 (list1 - list2)
        List<String> reduce1 = list1.stream().filter(item -> !list2.contains(item)).collect(Collectors.toList());
        System.out.println("---差集 reduce1 (list1 - list2)---");
        reduce1.parallelStream().forEach(System.out::println);

        // 差集 (list2 - list1)
        List<String> reduce2 = list2.stream().filter(item -> !list1.contains(item)).collect(Collectors.toList());
        System.out.println("---差集 reduce2 (list2 - list1)---");
        reduce2.parallelStream().forEach(System.out::println);

        // 并集
        List<String> listAll = list1.parallelStream().collect(Collectors.toList());
        List<String> listAll2 = list2.parallelStream().collect(Collectors.toList());
        listAll.addAll(listAll2);
        System.out.println("---并集 listAll---");
        listAll.parallelStream().forEachOrdered(System.out::println);

        // 去重并集
        List<String> listAllDistinct = listAll.stream().distinct().collect(Collectors.toList());
        System.out.println("---得到去重并集 listAllDistinct---");
        listAllDistinct.parallelStream().forEachOrdered(System.out::println);

        System.out.println("---原来的List1---");
        list1.parallelStream().forEachOrdered(System.out::println);
        System.out.println("---原来的List2---");
        list2.parallelStream().forEachOrdered(System.out::println);
//        List<Integer> a = new ArrayList<>(32);
//
//        a.add(1);
//
//        a.add(2);
//
//        a.add(3);
//
//        List<Integer> b = new ArrayList<>(32);
//
//        b.add(2);
//
//        b.add(3);
//
//        b.add(4);
//
//        a.addAll(b);
//        List<Integer> collect = a.stream().distinct().collect(Collectors.toList());
//        System.out.println(collect);

    }

    @Test
    public void testMobile() throws ParseException {
//        boolean mobile = PatternUtil.isMobile("2321899000");
//        System.out.println(mobile);
//        Date day = new Date();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd 00");
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
//        String s = df.format(day);
//        Date date = df.parse(s);
//        ArrayList<String> dates = new ArrayList<>();
//        for (int i = 0; i < 24; i++) {
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(date);
//            cal.add(Calendar.HOUR, 1);
//            date = cal.getTime();
//            String s1 = format.format(date);
//            dates.add(s1);
//        }
//        System.out.println(dates.size());
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("192.168.241.149");
//        conn2.setPort(27017);
//        conn2.setUsername("ess");
//        conn2.setPassword("ess");
//        conn2.setDatabase("ess");
//        conn2.connect();
//        DeviceDao deviceDao = new DeviceDao();
//        boolean updateOfExisting = deviceDao.update().set("imgUrl", new ArrayList<>()).execute(deviceDao.query().is("deviceName", "d06a4137967744efa6a24dd564480e0a")).isUpdateOfExisting();
//        System.out.println(updateOfExisting);
//        RoleEntityDao entityDao = new RoleEntityDao();
//        boolean updateOfExisting = entityDao.update().set("roleName", "1111").execute(entityDao.query().is("_id", "6057f5b1171c617d48587e22")).isUpdateOfExisting();
//        System.out.println(updateOfExisting);
//        BamsDischargeCapacityDao bamsDischargeCapacityDao = new BamsDischargeCapacityDao();
//        Iterable<DBObject> bamsDischargeCapacityIterable = bamsDischargeCapacityDao.aggregate().match(bamsDischargeCapacityDao.query()
//                .is("deviceName", "d06a4137967744efa6a24dd564480e0a")
//                .is("equipmentId", 14)).sort("{generationDataTime:-1}").limit(1).results();
//        BamsDischargeCapacity bamsDischargeCapacity = null;
//        if (bamsDischargeCapacityIterable != null) {
//            for (DBObject object : bamsDischargeCapacityIterable) {
//                bamsDischargeCapacity = MapperUtil.fromDBObject(BamsDischargeCapacity.class, object);
//            }
//        }
//        System.out.println(bamsDischargeCapacity);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(format.format(DateUtils.getCurrentYearStartTime()));
        System.out.println(format.format(DateUtils.getCurrentYearEndTime()));
//         System.out.println(format.format(DateUtils.getYearEndTime(new Date())));
    }

    @Test
    public void testStatics() {
        /**
         * TODO 下午测试下上传
         */
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("172.24.63.229").setPort(27017).setUsername("farm").setPassword("hithium.db.mongo").setDatabase("farm").connect();
////        conn2.setHost("192.168.241.189").setPort(27017).setUsername("ess").setPassword("ess").setDatabase("ess").connect();
//        Date date = DateUtils.addDateMinutes(new Date(), -1);
//        EssStationDao essStationDao= new EssStationDao();
//        long essStationCount = essStationDao.count();
//        int pageSize = 2000;
//        Set<String> uploadEntityIds = new HashSet<>();
//        for(int i=0;i<(int) Math.ceil((double) essStationCount / (double) pageSize);i++){
//            List<EssStation> essStationList = essStationDao.query().pageSize(pageSize).pageNumber(i+1).results();
//            if (!CollectionUtils.isEmpty(essStationList) && !essStationList.isEmpty()) {
//                for (EssStation essStation : essStationList) {
//                    uploadEntityIds.add(essStation.getUploadImgId());
//                }
//            }
//        }
//        DeviceDao deviceDao = new DeviceDao();
//        long deviceCount = deviceDao.count();
//        for (int i = 0; i < (int) Math.ceil((double) deviceCount / (double) pageSize); i++) {
//            List<Device> deviceList = deviceDao.query().pageSize(pageSize).pageNumber(i + 1).results();
//            if (!CollectionUtils.isEmpty(deviceList) && !deviceList.isEmpty()) {
//                for (Device device : deviceList) {
//                    uploadEntityIds.add(device.getUploadImgId());
//                }
//            }
//        }
//        UploadEntityDao uploadEntityDao = new UploadEntityDao();
//        BuguQuery<UploadEntity> query = uploadEntityDao.query().notIn("_id", new ArrayList(uploadEntityIds)).lessThan("uploadTime", date).pageSize(2000).pageNumber(1);
//        List<UploadEntity> uploadEntities = query.results();
//        System.out.println("启动定时删除导入图片任务,查询到可删除图片文件数量为:{}"+uploadEntities.size());
//        for (UploadEntity uploadEntity : uploadEntities) {
//            List<String> imgOfFullPath = uploadEntity.getImgOfFullPath();
//            if (!CollectionUtils.isEmpty(imgOfFullPath) && !imgOfFullPath.isEmpty()) {
//                for (String path : imgOfFullPath) {
////                    File file = new File(path);
////                    if (file.exists()) {
//                    System.out.println("删除图片,删除图片路径名称:{}"+path);
////                        file.delete();
////                    }
//                }
////                uploadEntityDao.remove(uploadEntity);
//            }
//        }
//        for (ExportRecord exportRecord : exportRecords) {
//            fs.remove(exportRecord.getFilename());
//            exportRecordDao.remove(exportRecord);
//            System.out.println("删除excel,时间为:"+exportRecord.getRecordTime()+",删除文件名称:{}"+exportRecord.getFilename());
//        }
//        DeviceDao deviceDao = new DeviceDao();
//        deviceDao.update().set("bindStation",false).execute(deviceDao.query());
//        BamsDischargeCapacityDao bamsDischargeCapacityDao = new BamsDischargeCapacityDao();
//        {_id:'$incomeOfDay',count:{$sum:{$toDouble:'$income'}}}
//        {_id:{ $dateToString: { format: '%Y-%m', date: '$accessTime' } }
//         %H
//        date:{$add:['$generationDataTime',28800000]}}


//        Iterable<DBObject> iterable = bamsDischargeCapacityDao.aggregate()
//                .group("{_id:{$dateToString:{format:'%Y-%m-%d %H',date:{$add:['$generationDataTime',28800000]}}},count:{$sum:{$toDouble:'$dischargeCapacitySubtract'}}}")
//                .sort("{generationDataTime:-1}").limit(25)
//                .results();
//        Map<String, CaEntity> dbOfResultOfHour = new HashMap<>();
//        if (iterable != null) {
//            for (DBObject object : iterable) {
//                String id = (String) object.get("_id");
//                dbOfResultOfHour.put(id.split(" ")[1], new CaEntity(id.split(" ")[1], (Double) object.get("count")));
//            }
//        }
//        Map<String, CaEntity> initHoursData = new HashMap<>();
//        for (int i = 0; i < 26; i++) {
//            Date startTimeOfDay = DateUtils.getStartTime(new Date());
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(startTimeOfDay);
//            cal.add(Calendar.HOUR, i);
//            startTimeOfDay = cal.getTime();
//            initHoursData.put(DateUtils.dateToStrWithHH(startTimeOfDay).split(" ")[1], new CaEntity(DateUtils.dateToStrWithHH(startTimeOfDay).split(" ")[1], 0D));
//        }
//        Map<String, CaEntity> map3 = Stream.of(initHoursData, dbOfResultOfHour)
//                .flatMap(map -> map.entrySet().stream())
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        Map.Entry::getValue,
//                        (v1, v2) -> v2));
//        List<CaEntity> caEntities = new ArrayList<>(map3.values());
//        Collections.sort(caEntities, new Comparator<CaEntity>() {
//            @Override
//            public int compare(CaEntity o1, CaEntity o2) {
//                return Integer.parseInt(o1.getDate()) - Integer.parseInt(o2.getDate());
//            }
//        });
//        System.out.println(caEntities);
//        Map<String, Double> stringDoubleMap = sortByKey(map3);
//        System.out.println(stringDoubleMap);
    }

    private Map<String, Double> sortByKey(Map<String, Double> map) {
        Map<String, Double> result = new LinkedHashMap<>(map.size());
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    @Test
    public void testDay555() {
        List<String> date = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Date startTimeOfDay = DateUtils.getStartTime(new Date());
            Calendar cal = Calendar.getInstance();
            cal.setTime(startTimeOfDay);
            cal.add(Calendar.DAY_OF_YEAR, -i);
            startTimeOfDay = cal.getTime();
            String key = DateUtils.dateToStrMMDD(startTimeOfDay);
            date.add(key);
        }
        System.out.println(date);
        Collections.sort(date, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String[] splitOne = o1.split("-");
                String[] splitTwo = o2.split("-");
                if (splitOne[0].equals(splitTwo[0])) {
                    return Integer.parseInt(splitOne[1]) - Integer.parseInt(splitTwo[1]);
                }
                return Integer.parseInt(splitOne[0]) - Integer.parseInt(splitTwo[0]);
            }
        });
        System.out.println(date);
    }
//
//    public static String upload(MultipartFile file, String path, String fileLocation) {
//        String fileFinishName = null;
//        try {
//            // 如果目录不存在则创建
//            File uploadDir = new File(fileLocation);
//            if (!uploadDir.exists()) {
//                uploadDir.mkdir();
//            }
//            //获取源文件名称
//            String fileName = file.getOriginalFilename();
//            fileFinishName = UUID.randomUUID() + fileName.substring(fileName.lastIndexOf("."), fileName.length());
//            //上传文件到指定目录下
//            File uploadFile = new File(uploadDir + uploadDir.separator + fileFinishName);
//            file.transferTo(uploadFile);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return path + "/" + fileFinishName;
//    }

    @Test
    public void testWhile() {
        Date startTimeOfDay = DateUtils.getStartTime(new Date());
        ArrayList<String> dates = new ArrayList<>();
        dates.add(DateUtils.dateToStrWithHH(startTimeOfDay));
        for (int i = 0; i < 24; i++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(startTimeOfDay);
            cal.add(Calendar.HOUR, 1);
            cal.set(Calendar.MINUTE, 0);
            startTimeOfDay = cal.getTime();

            dates.add(DateUtils.dateToStrWithHH(startTimeOfDay));
        }
        System.out.println(dates);

//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("192.168.241.145");
//        conn2.setPort(27017);
//        conn2.setUsername("ess");
//        conn2.setPassword("ess");
//        conn2.setDatabase("ess");
//        conn2.connect();
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("172.24.63.229");
//        conn2.setPort(27017);
//        conn2.setUsername("farm");
//        conn2.setPassword("hithium.db.mongo");
//        conn2.setDatabase("farm");
//        conn2.connect();
//        BamsDataDicBADao bamsDataDicBADao = new BamsDataDicBADao();
//        Map<String, Double> resultMap = new LinkedHashMap<>();
//        DeviceDao deviceDao = new DeviceDao();
//        List<Device> results = deviceDao.query().results();
//        List<String> stringList = results.stream().map(device -> device.getDeviceName()).collect(Collectors.toList());
//        if (!CollectionUtils.isEmpty(stringList) && !stringList.isEmpty()) {
//            for (String deviceName : stringList) {
//                for (int i = 0; i < dates.size() - 1; i++) {
//                    double maxOfYesterday = bamsDataDicBADao.max("dischargeCapacitySum", bamsDataDicBADao.query().is("deviceName", deviceName)
//                            .greaterThan("generationDataTime", dates.get(i)).lessThan("generationDataTime", dates.get(i + 1)));
//                    resultMap.put(deviceName + "_" + dates.get(i) + "_" + dates.get(i + 1), maxOfYesterday);
//                }
//            }
//        }
//        List<BamsDataDicBA> results = bamsDataDicBADao.query().sort("{generationDataTime:-1}").pageSize(1).pageNumber(0).results();
//        System.out.println(results.get(0));
//        Iterable<DBObject> iterable = bamsDataDicBADao.aggregate()
//                .match(bamsDataDicBADao.query().is("deviceName", "d06a4137967744efa6a24dd564480e0a")
//                .is("equipmentId", 14)).sort("{generationDataTime:-1}").limit(1).results();
//
//        Iterable<DBObject> iterable1 = bamsDataDicBADao.aggregate()
//                .match(bamsDataDicBADao.query().is("deviceName", "d06a4137967744efa6a24dd564480e0a")
//                        .is("equipmentId", 14).sort("{generationDataTime:-1}")).limit(1).results();
//        BamsDataDicBA bamsDataDicObject = null;
//        if (iterable != null) {
//            for (DBObject object : iterable) {
//                bamsDataDicObject = MapperUtil.fromDBObject(BamsDataDicBA.class, object);
//            }
//        }
//        System.out.println(bamsDataDicObject);

//        BcuDataDicBCUDao bcuDataDicBCUDao = new BcuDataDicBCUDao();
//        Iterable<DBObject> iterable = bcuDataDicBCUDao.aggregate().match(bcuDataDicBCUDao.query().is(DEVICE_NAME, "d06a4137967744efa6a24dd564480e0a"))
//                .group("{_id:'$equipmentId','dataId':{$last:'$_id'}}").sort("{generationDataTime:-1}").limit(16).results();
//        List<String> strings = new ArrayList<>();
//        if(iterable != null){
//            for (DBObject object : iterable) {
//                ObjectId dataId = (ObjectId) object.get("dataId");
//                String queryId = dataId.toString();
//                strings.add(queryId);
//            }
//        }

    }

    @Test
    public void testDay222() {
        Date startTimeOfDay = DateUtils.getStartTime(new Date());
        Map<String, Double> map = new HashMap<>();
        map.put(DateUtils.dateToStr(startTimeOfDay), 0D);
        for (int i = 0; i < 7; i++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(startTimeOfDay);
            cal.add(Calendar.DAY_OF_YEAR, -1);
            startTimeOfDay = cal.getTime();
            map.put(DateUtils.dateToStr(startTimeOfDay), 0D);
        }
        System.out.println(map);
    }

    @Test
    public void testDay3333() {

        Map<String, Double> map = new HashMap<>();
        for (int i = 0; i < 25; i++) {
            Date startTimeOfDay = DateUtils.getStartTime(new Date());
            Calendar cal = Calendar.getInstance();
            cal.setTime(startTimeOfDay);
            cal.add(Calendar.DAY_OF_MONTH, i);
            startTimeOfDay = cal.getTime();
            map.put(DateUtils.dateToStrWithHH(startTimeOfDay), 0D);
        }
//        map.put(DateUtils.dateToStr(startTimeOfDay), 0D);
        System.out.println(map.size());
    }

    public static Date getYearStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.add(Calendar.DATE, 0);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @Test
    public void testMap() {
        Map<String, Double> map = new HashMap<>();
        for (int i = 0; i < 12; i++) {
            Date yearStartTime = getYearStartTime(new Date());
            Calendar cal = Calendar.getInstance();
            cal.setTime(yearStartTime);
            cal.add(Calendar.MONTH, i);
            yearStartTime = cal.getTime();
            map.put(DateUtils.formatYYYYMM(yearStartTime), 0D);
        }
        System.out.println(map);

    }

    @Test
    public void testHour() {
//        Map<String,Double> initData = new HashMap<>();
//        for (int i = 0; i < 25; i++) {
//            Date startTimeOfDay = DateUtils.getStartTime(new Date());
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(startTimeOfDay);
//            cal.add(Calendar.HOUR, i);
//            startTimeOfDay = cal.getTime();
//            initData.put(DateUtils.dateToStrWithHH(startTimeOfDay), 0D);
//        }
//        Map<String, Double> initData = new HashMap<>();
//        for (int i = 0; i < 7; i++) {
//            Date startTimeOfDay = DateUtils.getStartTime(new Date());
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(startTimeOfDay);
//            cal.add(Calendar.DAY_OF_YEAR, -i);
//            startTimeOfDay = cal.getTime();
//            initData.put(DateUtils.dateToStr(startTimeOfDay), 0D);
//        }
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("192.168.241.145").setPort(27017).setUsername("ess").setPassword("ess").setDatabase("ess").connect();
//        BamsDischargeCapacityDao bamsDischargeCapacityDao = new BamsDischargeCapacityDao();
//        Iterable<DBObject> iterable = bamsDischargeCapacityDao.aggregate().group("{_id:{$dateToString:{format:'%Y-%m-%d',date:{$add:['$generationDataTime',28800000]}}},count:{$sum:{$toDouble:'$dischargeCapacitySubtract'}}}")
//                .sort("{generationDataTime:-1}").limit(7)
//                .results();
//        Map<String, Double> dbOfResultOfHour = new HashMap<>();
//        if (iterable != null) {
//            for (DBObject object : iterable) {
//                dbOfResultOfHour.put((String) object.get("_id"), Double.valueOf(object.get("count").toString()));
//            }
//        }
//        Map<String, Double> map3 = Stream.of(initData, dbOfResultOfHour)
//                .flatMap(map -> map.entrySet().stream())
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        Map.Entry::getValue,
//                        (v1, v2) -> v2));
//        System.out.println(map3);
//        Map<String, Double> map = sortByKey(map3);
//        System.out.println(map);
    }


    @Test
    public void testTime23333() {
        Calendar cale = Calendar.getInstance();
        int year = cale.get(Calendar.YEAR);
        int month = cale.get(Calendar.MONTH) + 1;
        int day = cale.get(Calendar.DATE);
        int hour = cale.get(Calendar.HOUR_OF_DAY);
        int minute = cale.get(Calendar.MINUTE);
        int second = cale.get(Calendar.SECOND);
        int dow = cale.get(Calendar.DAY_OF_WEEK);
        int dom = cale.get(Calendar.DAY_OF_MONTH);
        int doy = cale.get(Calendar.DAY_OF_YEAR);

        System.out.println("Current Date: " + cale.getTime());
        System.out.println("Year: " + year);
        System.out.println("Month: " + month);
        System.out.println("Day: " + day);
        System.out.println("Hour: " + hour);
        System.out.println("Minute: " + minute);
        System.out.println("Second: " + second);
        System.out.println("Day of Week: " + dow);
        System.out.println("Day of Month: " + dom);
        System.out.println("Day of Year: " + doy);

        // 获取当月第一天和最后一天
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String firstday, lastday;
        // 获取前月的第一天
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 0);
        cale.set(Calendar.DAY_OF_MONTH, 1);
        cale.set(Calendar.HOUR_OF_DAY, 0);
        cale.set(Calendar.MINUTE, 0);
        cale.set(Calendar.SECOND, 0);
        firstday = format.format(cale.getTime());
        // 获取前月的最后一天
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 1);
        cale.set(Calendar.DAY_OF_MONTH, 0);
        cale.set(Calendar.HOUR_OF_DAY, 0);
        cale.set(Calendar.MINUTE, 0);
        cale.set(Calendar.SECOND, 0);
        lastday = format.format(cale.getTime());
        System.out.println("本月第一天和最后一天分别是 ： " + firstday + " and " + lastday);

        // 获取当前日期字符串
        Date d = new Date();
        System.out.println("当前日期字符串1：" + format.format(d));
        System.out.println("当前日期字符串2：" + year + "/" + month + "/" + day + " "
                + hour + ":" + minute + ":" + second);
    }

    @Test
    public void createUUID() {
        String str = UUIDUtil.get32Str();
        System.out.println(str);
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("DATATYPE", "REAL_TIME_DATA");
//        jsonObject.put("PROJECTTYPE", "KC_ESS");
//        JSONObject jsonObject1 = new JSONObject();
//        jsonObject.put("DATA", jsonObject1);
//        String s = UUID.randomUUID().toString();
//        jsonObject1.put("pipixia", s);
//        System.out.println(jsonObject.toString());
    }

    @Test
    public void createDeviceInfo() {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("DATATYPE", DataType.DEVICE_LIST_DATA);
//        jsonObject.put("PROJECTTYPE", "KC_ESS");
//        JSONObject jsonObject1 = new JSONObject();
//        jsonObject.put("DATA", jsonObject1);
//        String s = UUID.randomUUID().toString();
//        jsonObject1.put("pipixia2222222222999", s);
//
//        System.out.println(jsonObject);
        int i = -1;
        if (i == 0 || i == 1) {
            System.out.println(true);
        }
    }

    @Test
    public void testBreakDownLog() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("DATATYPE", DataType.BREAKDOWNLOG_DATA);
        jsonObject.put("PROJECTTYPE", "KC_ESS");
        JSONArray jsonObject1 = new JSONArray();
        jsonObject.put("DATA", jsonObject1);
        String s = UUID.randomUUID().toString();
        JSONObject jsonObject2 = new JSONObject();

        jsonObject1.add(jsonObject2);
        for (int i = 10; i < 20; i++) {
            jsonObject2.put("pipi" + i, "xia" + i);
        }
        System.out.println(jsonObject);
    }

    @Test
    public void getCity() {
        String city = "";
        Map<String, String> citys = new HashMap<>();
        Map<String, Set<String>> proToCitys = new HashMap<>();
        BufferedReader reader = null;
        try {
            Resource resource = new ClassPathResource("city.json");
            InputStream in = new FileInputStream(resource.getFile());
//        InputStream in = HithiumFarmWebApplicationTests.class.getClassLoader().getResourceAsStream(city);
            reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        reader.lines().forEach((s) -> {
            sb.append(s);
        });
        JSONObject jsonObject = JSON.parseObject(sb.toString());
        Set<Map.Entry<String, Object>> items = jsonObject.entrySet();
        for (Map.Entry<String, Object> entry : items) {
            JSONArray arr = (JSONArray) entry.getValue();
            for (Object obj : arr) {
                JSONObject o = (JSONObject) obj;
                String cityName = o.getString("name");
                String province = o.getString("province");
                citys.put(cityName, province);
                Set<String> cs = proToCitys.get(province);
                if (cs == null) {
                    Set<String> set = new HashSet<>();
                    proToCitys.put(province, set);
                    set.add(cityName);
                } else {
                    cs.add(cityName);
                }
            }
        }
    }

    /**
     * EventBus 是 Guava 的事件处理机制,是观察者模式(生产/消费模型)的一种实现。
     */
    @Test
    public void testListener() {
        EventBus eventBus = new EventBus("test");
        EventListener listener = new EventListener();

        eventBus.register(listener);

        eventBus.post(new TestEvent(200));
        eventBus.post(new TestEvent(300));
        eventBus.post(new TestEvent(400));

        System.out.println("LastMessage:" + listener.getLastMessage());
    }

    @Test
    public void testTime() {
        /**
         * 根据当前时间查询出当前电价 计算充放电收益
         */
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("192.168.241.170");
//        conn2.setPort(27017);
//        conn2.setUsername("ess");
//        conn2.setPassword("ess");
//        conn2.setDatabase("ess");
//        conn2.connect();
//        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
//        DeviceDao deviceDao = new DeviceDao();
//        Device device = deviceDao.query().is("deviceName", "d06a4137967744efa6a24dd564480e0a").result();
//        List<TimeOfPriceBo> priceOfTime = device.getPriceOfTime();
//        Map<Integer, List<String>> map = new HashMap<>();
//        for (TimeOfPriceBo timeOfPriceBo : priceOfTime) {
//            String time = timeOfPriceBo.getTime();
//            String[] split = time.split(",");
//            List<String> list = new ArrayList<>();
//            for (String spl : split) {
//                list.add(spl);
//            }
//            map.put(timeOfPriceBo.getType(), list);
//        }
//        Map<Integer, String> typeOfMap = null;
//        try {
//            Set<Map.Entry<Integer, List<String>>> entries = map.entrySet();
//            for (Map.Entry<Integer, List<String>> entry : entries) {
//                List<String> entryValue = entry.getValue();
//                for (String value : entryValue) {
//                    String[] split = value.split("-");
//                    Calendar now = Calendar.getInstance();
//                    now.setTime(df.parse(df.format(new Date())));
//                    Calendar begin = Calendar.getInstance();
//                    begin.setTime(df.parse(split[0]));
//                    Calendar end = Calendar.getInstance();
//                    end.setTime(df.parse(split[1]));
//                    /**
//                     * 不相等时候在查看是否在区间内
//                     */
//                    if (!now.equals(begin) && !now.equals(end)) {
//                        if (now.after(begin) && now.before(end)) {
//                            System.out.println("不相等" + begin.getTime() + "---" + end.getTime());
//                            if (typeOfMap == null) {
//                                typeOfMap = new HashMap<>();
//                            }
//                            typeOfMap.put(entry.getKey(), split[0] + "-" + split[1]);
//                        }
//                    } else {
//                        /**
//                         * 相同的话  会有两个区间出来 取哪个？
//                         */
//                        if (typeOfMap == null) {
//                            typeOfMap = new HashMap<>();
//                        }
//                        typeOfMap.put(entry.getKey(), split[0] + "-" + split[1]);
//                        System.out.println("相等" + entry.getKey() + "#####" + begin.getTime() + "---" + end.getTime());
//                    }
//                }
//            }
//        } catch (Exception e) {
//
//        }
//        List<BigDecimal> bigDecimalList = null;
//        for (Map.Entry<Integer, String> entry : typeOfMap.entrySet()) {
//            Integer key = entry.getKey();
//            List<TimeOfPriceBo> ofPriceBos = priceOfTime.stream().filter(timeOfPriceBo -> {
//                return timeOfPriceBo.getType() == key;
//            }).collect(Collectors.toList());
//            BigDecimal bigDecimal = new BigDecimal(ofPriceBos.get(0).getPrice());
//            if (bigDecimalList == null) {
//                bigDecimalList = new ArrayList<>();
//            }
//            bigDecimalList.add(bigDecimal);
//        }
//        /**
//         * 放电降序取第一个  充电升序取第一个
//         */
//        bigDecimalList = bigDecimalList.stream().sorted(new Comparator<BigDecimal>() {
//            @Override
//            public int compare(BigDecimal o1, BigDecimal o2) {
//                return o2.compareTo(o1);
//            }
//        }).collect(Collectors.toList());
//
//        for (BigDecimal bigDecimal : bigDecimalList) {
//            System.out.println(bigDecimal);
//        }

    }

    @Test
    public void test2() {

        Date time = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.set(Calendar.HOUR_OF_DAY, 12); //时
        cal.set(Calendar.MINUTE, 0); //分
        cal.set(Calendar.SECOND, 0); //秒
        cal.set(Calendar.MILLISECOND, 0); //毫秒
        Date time2 = cal.getTime();
        System.out.println(time2);
    }

    @Test
    public void testBigdecimal() {
        String str1 = "1";
        String str2 = "0.8459";
        String str3 = "0.5732";
        String str4 = "0.3004";
        BigDecimal bigDecimal1 = new BigDecimal(str1);
        BigDecimal bigDecimal2 = new BigDecimal(str2);
        BigDecimal bigDecimal3 = new BigDecimal(str3);
        BigDecimal bigDecimal4 = new BigDecimal(str4);
        List<BigDecimal> bigDecimalList = new ArrayList<>();
        bigDecimalList.add(bigDecimal1);
        bigDecimalList.add(bigDecimal2);
        bigDecimalList.add(bigDecimal3);
        bigDecimalList.add(bigDecimal4);

        bigDecimalList = bigDecimalList.stream().sorted(new Comparator<BigDecimal>() {
            @Override
            public int compare(BigDecimal o1, BigDecimal o2) {
                /**
                 * 降序
                 */
                return o2.compareTo(o1);
                /**
                 * 升序
                 */
//                return o1.compareTo(o2);
            }
        }).collect(Collectors.toList());
        for (BigDecimal bigDecimal : bigDecimalList) {
            System.out.println(bigDecimal);
        }
    }

    @Test
    public void testAmmer() {

//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("172.24.63.229");
//        conn2.setPort(27017);
//        conn2.setUsername("farm");
//        conn2.setPassword("hithium.db.mongo");
//        conn2.setDatabase("farm");
//        conn2.connect();
//        DeviceDao deviceDao = new DeviceDao();
//        DBObject group = new BasicDBObject();
//        group.put("_id", null);
//        DBObject toDouble = new BasicDBObject();
//        toDouble.put("$toDouble", "$longitude");
//        group.put("sum", new BasicDBObject("$sum", toDouble));
//        Iterable<DBObject> iterable = deviceDao.aggregate().group(group).results();
//        if (iterable != null) {
//            for (DBObject object : iterable) {
//                Double sum = (Double) object.get("sum");
//                System.out.println(sum);
//            }
//        }

//        AmmeterDataDicDao ammeterDataDicDao = new AmmeterDataDicDao();
//        long start = System.currentTimeMillis();
//        Iterable<DBObject> iterable = ammeterDataDicDao.aggregate().match(ammeterDataDicDao.query().is("deviceName", "705780da821448599201d2f4cf68d6a1")
//                .is("equipmentId", 3)).sort("{generationDataTime:-1}").limit(1).results();
//        AmmeterDataDic fromDBObject = null;
//        if (iterable != null) {
//            for (DBObject object : iterable) {
//                fromDBObject = MapperUtil.fromDBObject(AmmeterDataDic.class, object);
//            }
//        }
//        if (fromDBObject != null) {
//            BigDecimal lastTimeTotalChargeQuantity = new BigDecimal(fromDBObject.getTotalChargeQuantity());
//            BigDecimal lastTimeTotalDisChargeQuantity = new BigDecimal(fromDBObject.getTotalDischargeQuantity());
//            System.out.println(fromDBObject.getGenerationDataTime());
//            /**
//             * 当前上报 充电电表数据
//             */
//        }
//        long end = System.currentTimeMillis();
//        System.out.println(end-start);
    }

    @Test
    public void testTime1() {
        /**
         * 根据当前时间查询出当前电价 计算充放电收益
         */
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("192.168.241.170");
//        conn2.setPort(27017);
//        conn2.setUsername("ess");
//        conn2.setPassword("ess");
//        conn2.setDatabase("ess");
//        conn2.connect();
//        DeviceDao deviceDao = new DeviceDao();
//
//        Device result = deviceDao.query().is("deviceName", "d06a4137967744efa6a24dd564480e0a").result();
//        System.out.println(result);
    }

    @Test
    public void testRemoveTemp() {
        /**
         * 根据当前时间查询出当前电价 计算充放电收益
         */
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("172.24.63.229");
//        conn2.setPort(27017);
//        conn2.setUsername("farm");
//        conn2.setPassword("hithium.db.mongo");
//        conn2.setDatabase("farm");
//        conn2.connect();
//        DeviceDao deviceDao = new DeviceDao();
//        Iterable<DBObject> objs = deviceDao.aggregate()
//                .group("{_id:{ $dateToString: { format: '%Y-%m', date: '$accessTime' } },count:{$sum:{$toDouble:'$stationPower'}}}").results();
//        Map<String, StatisticBo> map = getDefaultBo(2021);
//        if (objs != null) {
//            for (DBObject obj : objs) {
//                String name = obj.get("_id").toString();
//                Double count = Double.valueOf(obj.get("count").toString());
//                StatisticBo bo = map.get(name);
//                if (bo != null) {
//                    bo.setCount(count);
//                }
//            }
//        }
//        List<StatisticBo> aim = new ArrayList<>(map.values());
//
//        Collections.sort(aim, new Comparator<StatisticBo>() {
//            @Override
//            public int compare(StatisticBo o1, StatisticBo o2) {
//                return Integer.parseInt(o1.getDate()) - Integer.parseInt(o2.getDate());
//            }
//        });
//        List<StatisticBo> result = new ArrayList<>(map.values().size());
//        for (int i = 0; i < 4; i++) {
//            result.add(i,getStatisticBo(i,aim));
//        }
        /**
         * 生成12个集合
         */
        /**
         * 第一个集合
         */
//        for (int i = 0; i < 11; i++) {
//            result.add(getStatisticBo(aim, i));
//        }
//        for (StatisticBo statisticBo1 : result) {
//            System.out.println(statisticBo1);
//        }


//        BmsCellTempDataDicDao bmsCellTempDataDicDao = new BmsCellTempDataDicDao();
//        Iterable<DBObject> iterable = bmsCellTempDataDicDao.aggregate().match(bmsCellTempDataDicDao.query().is("deviceName", "705780da821448599201d2f4cf68d6a1")
//                .is("equipmentId", 36))
//                .sort("{generationDataTime:-1}").limit(1).results();
//        if (iterable != null) {
//            BmsCellTempDataDic bmsCellTempDataDic = null;
//            for (DBObject object : iterable) {
//                bmsCellTempDataDic = MapperUtil.fromDBObject(BmsCellTempDataDic.class, object);
//            }
//            Map<String, Integer> tempMap = bmsCellTempDataDic.getTempMap();
//            Iterator<Map.Entry<String, Integer>> iterator = tempMap.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Map.Entry<String, Integer> next = iterator.next();
//                if (next.getValue() == 0) {
//                    iterator.remove();
//                }
//            }
//
//        }
    }

    private StatisticBo getStatisticBo(List<StatisticBo> aim, int i) {
        List<StatisticBo> statisticBos = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            statisticBos.add(aim.get(j));
        }
        double sum = statisticBos.stream().mapToDouble(StatisticBo::getCount).sum();
        StatisticBo statisticBo = new StatisticBo();
        statisticBo.setCount(sum);
        statisticBo.setDate(String.valueOf(i));
        return statisticBo;
    }

    private Map<String, StatisticBo> getDefaultBo(Integer year) {
        Map<String, StatisticBo> map = new TreeMap<>();
        for (int i = 1; i <= 12; i++) {
            map.put(year + "-" + (i > 9 ? i : "0" + i), new StatisticBo(i + "", 0D));
        }
        return map;
    }

    @Test
    public void testRedis() {
//        Jedis jedis = new Jedis("192.168.241.170", 6379);
//        jedis.auth("redis.hithium.pwd");
////        Set<String> keys = jedis.keys("DEIVCE_INCOME_RECORED_广东省_*");
////        for(String key:keys){
////            System.out.println(key);
////        }
////        System.out.println(getDate(new Date()));
//        Set<String> keys = jedis.keys("DEIVCE_INCOME_RECORED_2021-04-??_广东省_珠海市");
//        System.out.println(keys.size());
////        Set<String> keys = jedis.keys(getDate(new Date()));
//        for (String key : keys) {
//            System.out.println(key);
//        }
    }

    public String getDate(Date recordDate) {
        return INCOME_RECORD_PREFIX + DateUtils.dateToStryyyymm(recordDate) + "_*";
    }

    @Test
    public void testSimple() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-?");
        String format1 = format.format(new Date());
        System.out.println(format1);
    }

    @Test
    public void testPrice() {
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("172.24.63.229");
//        conn2.setPort(27017);
//        conn2.setUsername("farm");
//        conn2.setPassword("hithium.db.mongo");
//        conn2.setDatabase("farm");
//        conn2.connect();
//        EquipmentDao equipmentDao = new EquipmentDao();
//        List<Equipment> equipments = equipmentDao.query().is(DEVICE_NAME, "clld3w7tdxjhvuhf6gzn60whffuy0hyj").is("enabled", true).results();
//        List<Equipment> resutlEquipment = new ArrayList<>();
//
//        /**
//         * 电池簇
//         */
//        List<Equipment> bamsClusterEquipment2 = equipments.stream().
//                filter(equip -> equip.getEquipmentId() > 36 && equip.getEquipmentId() < 53).collect(Collectors.toList());
//        List<Equipment> equipment = equipments.stream().
//                filter(equip -> equip.getEquipmentId() == 34 || equip.getEquipmentId() == 53).collect(Collectors.toList());
//        resutlEquipment.addAll(bamsClusterEquipment2);
//        resutlEquipment.addAll(equipment);
//        System.out.println(resutlEquipment.size());
//        Device device = deviceDao.query().is("deviceName", "705780da821448599201d2f4cf68d6a1").result();

//        List<TimeOfPriceBo> priceOfTime = device.getPriceOfTime();
//        Map<Integer, List<String>> map = new HashMap<>();
//        for (TimeOfPriceBo priceBo : priceOfTime) {
//            String[] split = priceBo.getTime().split(",");
//            List<String> list = new ArrayList<>();
//            for (String spl : split) {
//                list.add(spl);
//            }
//            map.put(priceBo.getType(), list);
//        }
//        List<PriceOfPercenVo> priceOfPercenVoList = new ArrayList<>();
//        for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
//            List<String> entryValue = entry.getValue();
//            try {
//                for (String value : entryValue) {
//                    PriceOfPercenVo priceOfPercenVo = new PriceOfPercenVo();
//                    String[] split = value.split("-");
//                    Calendar begin = Calendar.getInstance();
//                    begin.setTime(DateUtils.dateToStrWithHHmmWith(split[0]));
//                    Calendar end = Calendar.getInstance();
//                    end.setTime(DateUtils.dateToStrWithHHmmWith(split[1]));
//                    /**
//                     * 不相等时候在查看是否在区间内
//                     */
//                    String s = null;
//                    if (begin.before(end)) {
//                        s = DateUtils.timeDifferent(end.getTime(), begin.getTime());
//                    } else {
//                        Date endDate = DateUtils.addDateHours(end.getTime(), 24);
//                        s = DateUtils.timeDifferent(endDate, begin.getTime());
//                    }
//                    priceOfPercenVo.setType(entry.getKey());
//                    priceOfPercenVo.setTime(value);
//                    priceOfPercenVo.setMinute(s);
//                    priceOfPercenVoList.add(priceOfPercenVo);
//                }
//            } catch (Exception e) {
//
//            }
//
//        }
//        for (PriceOfPercenVo priceOfPercenVo : priceOfPercenVoList) {
//            System.out.println(priceOfPercenVo);
//        }

    }

    @Test
    public void testDay() throws Exception {
        Date startTime = DateUtils.dateToStrWithHHmmWith(START_TIME);
        System.out.println(startTime);
    }

    private Date getStartTimeOfDay() {
        Calendar dayStart = Calendar.getInstance();
        dayStart.set(Calendar.HOUR_OF_DAY, 0);
        dayStart.set(Calendar.MINUTE, 0);
        return dayStart.getTime();
    }

    @Test
    public void testCharNum() {
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("192.168.241.170");
//        conn2.setPort(27017);
//        conn2.setUsername("ess");
//        conn2.setPassword("ess");
//        conn2.setDatabase("ess");
//        conn2.connect();
//        DeviceDao deviceDao = new DeviceDao();
//        Iterable<DBObject> iterable = deviceDao.aggregate().group("{_id:null,count:{$sum:{$toDouble:'$chargeCapacitySum'}}}").results();
//        for (DBObject object : iterable) {
//            Double o = (Double) object.get("count");
//            System.out.println(o);
//        }
    }

    /**
     * TODO 下午根据相同采集时间合并不同数据集合
     *
     * @throws Exception
     */
    @Test
    public void testUrlCode() throws Exception {
        String str = "%E5%AF%BC%E5%87%BA";
        String encode = URLDecoder.decode(str, "utf-8");
        System.out.println(encode);
        String fileName = URLEncoder.encode("excel导出", "UTF-8");
        System.out.println(fileName);

        String time = "2021-04-11";
        Date date = DateUtils.dateToStrWithHHmm(time);
        Date endTime = DateUtils.getEndTime(date);
        System.out.println(endTime);
        System.out.println(date);
    }

    /**
     * 不创建对象的写
     */
//    @Test
//    public void noModelWrite() {
//        // 写法1
//        String fileName = "D://noModelWrite" + System.currentTimeMillis() + ".xlsx";
//        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
//        EasyExcel.write(fileName).head(head()).sheet("模板").doWrite(dataList());
//    }
    private List<List<String>> head() {
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("192.168.241.170");
//        conn2.setPort(27017);
//        conn2.setUsername("ess");
//        conn2.setPassword("ess");
//        conn2.setDatabase("ess");
//        conn2.connect();
//        BmsCellTempDataDicDao bmsCellTempDataDicDao = new BmsCellTempDataDicDao();
//        BmsCellTempDataDic results = bmsCellTempDataDicDao.query().is("deviceName", "d06a4137967744efa6a24dd564480e0a").result();
//        Map<String, Integer> tempMap = results.getTempMap();
//        List<List<String>> list = new ArrayList<List<String>>();
//        List<String> head0 = new ArrayList<String>();
//        head0.add("字符串");
//        List<String> head1 = new ArrayList<String>();
//        head1.add("数字" + System.currentTimeMillis());
//        List<String> head2 = new ArrayList<String>();
//        head2.add("日期" + System.currentTimeMillis());
//        list.add(head0);
//        list.add(head1);
//        list.add(head2);
//        for (Map.Entry<String, Integer> entry : tempMap.entrySet()) {
//            List<String> head3 = new ArrayList<String>();
//            head3.add(entry.getKey());
//            list.add(head3);
//        }
//        return list;
        return null;
    }

    private List<List<Object>> dataList() {
        List<List<Object>> list = new ArrayList<List<Object>>();
        for (int i = 0; i < 10; i++) {
            List<Object> data = new ArrayList<Object>();
            data.add("字符串" + i);
            data.add(new Date());
            data.add(0.56);
            list.add(data);
        }
        return list;
    }

    @Test
    public void testTime2() {
//        String time="2021-12-23";
//        boolean matches = Const.DATE_PATTERN.matcher(time).matches();
//        System.out.println(matches);
//
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("192.168.241.170");
//        conn2.setPort(27017);
//        conn2.setUsername("ess");
//        conn2.setPassword("ess");
//        conn2.setDatabase("ess");
//        conn2.connect();
//        IncomeEntityDao incomeEntityDao = new IncomeEntityDao();
//        BuguQuery<IncomeEntity> incomeEntityBuguQuery = incomeEntityDao.query().is("deviceName", "d06a4137967744efa6a24dd564480e0a");
//        Iterable<DBObject> iterable = incomeEntityDao.aggregate().match(incomeEntityBuguQuery).group(INCOMEOFDAY).sort("{_id:-1}").limit(7).results();
//        List<IncomeStatisticOfDayVo> incomeStatisticOfDayVoList = new ArrayList<>();
//        if (iterable != null) {
//            for (DBObject object : iterable) {
//                String time = (String) object.get("_id");
//                Double count = Double.valueOf(object.get("count").toString());
//                IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
//                incomeStatisticOfDayVo.setDay(time);
//                incomeStatisticOfDayVo.setIncome(BigDecimal.valueOf(count).toString());
//                incomeStatisticOfDayVoList.add(incomeStatisticOfDayVo);
//            }
//        }
//
//        /**
//         * 创建 空的List<IncomeStatisticOfDayVo></>  将有值的替换 无值的为空  年份就先创建3年吧  月份6个月  天数7天
//         */
//        List<IncomeStatisticOfDayVo> incomeStatisticOfDayVos = new ArrayList<>();
//
//
//        for (int i = 0; i < 7; i++) {
//            IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
//            incomeStatisticOfDayVo.setDay(DateUtils.format(DateUtils.addDateDays(new Date(), -i)));
//            incomeStatisticOfDayVos.add(incomeStatisticOfDayVo);
//        }
//
//        for (IncomeStatisticOfDayVo incomeStatisticOfDayVo : incomeStatisticOfDayVos) {
//            for (IncomeStatisticOfDayVo statisticOfDayVo : incomeStatisticOfDayVoList) {
//                if (incomeStatisticOfDayVo.getDay().equals(statisticOfDayVo.getDay())) {
//                    incomeStatisticOfDayVo.setIncome(statisticOfDayVo.getIncome());
//                }
//            }
//        }
//
//        System.out.println(incomeStatisticOfDayVos.isEmpty());
//        List<String> device = new ArrayList<>();
//        System.out.println(device.isEmpty());
    }

    @Test
    public void testArraysList() {
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("192.168.241.170");
//        conn2.setPort(27017);
//        conn2.setUsername("ess");
//        conn2.setPassword("ess");
//        conn2.setDatabase("ess");
//        conn2.connect();
//        DeviceDao deviceDao = new DeviceDao();
//        Device device = deviceDao.query().is("deviceName", "d06a4137967744efa6a24dd564480e0a").result();
//        Map<Integer, List<String>> map = new HashMap<>();
//        for (TimeOfPriceBo priceBo : device.getPriceOfTime()) {
//            String[] split = priceBo.getTime().split(",");
//            List<String> list = new ArrayList<>();
//            for (String spl : split) {
//                list.add(spl);
//            }
//            map.put(priceBo.getType(), list);
//        }
//
//        System.out.println(map.size());
//
//
//        Map<Integer, List<String>> map2 = new HashMap<>();
//        for (TimeOfPriceBo priceBo : device.getPriceOfTime()) {
//            String[] split = priceBo.getTime().split(",");
////            List<String> list = new ArrayList<>();
////            for (String spl : split) {
////                list.add(spl);
////            }
//            map2.put(priceBo.getType(), Arrays.asList(split));
//        }
//
//        System.out.println(map2.size());
    }

    @Test
    public void testContain() {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i < 54; i++) {
            list.add(i);
        }

        List<Integer> list1 = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            list1.add(i);
        }
        boolean b = list.containsAll(list1);
        System.out.println(b);
    }

    public ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2 + 1, Runtime.getRuntime().availableProcessors() * 2 + 1, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100000),
            new CustomThreadFactory(WEBTHREAD_POOL_TASK), new ThreadPoolExecutor.CallerRunsPolicy());

    @Test
    public void testCalc() throws InterruptedException {
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("192.168.241.170");
//        conn2.setPort(27017);
//        conn2.setUsername("ess");
//        conn2.setPassword("ess");
//        conn2.setDatabase("ess");
//        conn2.connect();

//        int i=-1;
//        int y=-1;
//        BigDecimal bigDecimalOfI = new BigDecimal(i);
//        BigDecimal bigDecimalOfY = new BigDecimal(y);
//
//        BigDecimal multiply = bigDecimalOfI.multiply(bigDecimalOfY);
//        System.out.println(multiply);
//        DeviceDao deviceDao = new DeviceDao();
//        System.out.println("执行了" + new Date());
//        long count = deviceDao.count();
//        int pageSize = 100;
//        Date date = new Date();
////        Date time = DateUtils.addDateDays(date, -1);
//        String time = "2021-04-16";
//        RealTimeDataService realTimeDataService = new RealTimeDataService();
//        ExportRecordDao exportRecordDao = new ExportRecordDao();
//        int ceil = (int) Math.ceil((double) count / (double) pageSize);
//        System.out.println(ceil);
//        Date date1 = new Date();
//        Date beforeDate = DateUtils.addDateDays(date1, -1);
//        String time1 = DateUtils.format(beforeDate);
//        System.out.println(time1);
//        for (int i = 0; i < (int) Math.ceil((double) count / (double) pageSize); i++) {
//            List<Device> deviceList = deviceDao.query().pageSize(pageSize).pageNumber(i + 1).results();
//            for (Device device : deviceList) {
//                for (int result : EXPORT_DATA_TYPE) {
//                    threadPoolExecutor.execute(() -> {
//                        File file = new File("D:" + File.separator + device.getDeviceName() + device.getDeviceName() + "_" + time + "_" + result + "_" + System.currentTimeMillis() + ".xlsx");
//                        OutputStream outputStream = null;
//                        ExportRecord exportRecord = new ExportRecord();
//                        try {
//                            makeSureFileExist(file);
//                            outputStream = new FileOutputStream(file);
//                            exportRecord.setRecordTime(new Date());
//                            exportRecord.setFilename(device.getDeviceName() + "_" + time + "_" + result);
//                            realTimeDataService.exportStationOfBattery(device.getDeviceName(), time, result, outputStream);
//                            exportRecord.setExporting(true);
//                            outputStream.close();
//                            Uploader uploader = new Uploader(file, exportRecord.getFilename(), false);
//                            uploader.save();
//                            exportRecordDao.insert(exportRecord);
//                        } catch (Exception e) {
//                            exportRecord.setExporting(false);
//                        } finally {
//                            if (outputStream != null) {
//                                try {
//                                    outputStream.close();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
////                        file.delete();
//                        }
//                    });
//                }
//            }
//        }
//        Thread.sleep(300000);
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

    @Test
    public void testCalc2() throws InterruptedException {
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("192.168.241.170");
//        conn2.setPort(27017);
//        conn2.setUsername("ess");
//        conn2.setPassword("ess");
//        conn2.setDatabase("ess");
//        conn2.connect();
//        //TODO delete 删除超过一个月的excel
//        ExportRecordDao exportRecordDao = new ExportRecordDao();
//        List<ExportRecord> exportRecords = exportRecordDao.query().lessThanEquals("recordTime", new Date()).results();
//
//        BuguFS fs = BuguFSFactory.getInstance().create();
//        for (ExportRecord exportRecord : exportRecords) {
//            fs.remove(exportRecord.getFilename());
//            exportRecordDao.remove(exportRecord);
//        }
    }

    @Test
    public void applyTime() {
        Date date = new Date();
        Date dateMinutes = DateUtils.addDateMinutes(date, -1);
        boolean before = date.before(dateMinutes);
        System.out.println(date.after(dateMinutes));
        System.out.println(before);
//        int i = new Random().nextInt(1000 - 0) + 0;
//        System.out.println(i);
    }

    @Test
    public void testList() {
        List<Device> deviceList = new ArrayList<>();
        Device device = new Device();
        device.setDeviceName("123");
        device.setName("321");
        deviceList.add(device);
        List<Device> deviceList1 = new ArrayList<>();
        for (Device device1 : deviceList) {
            device1.setDeviceName("987");
            device1.setName("987");
            deviceList1.add(device1);
        }
        for (Device device1 : deviceList1) {
            System.out.println(device1.getDeviceName());
        }
    }

    @Test
    public void testPattern() {
        String patten1 = "^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_]+$)(?![a-z0-9]+$)(?![a-z\\W_]+$)(?![0-9\\W_]+$)[a-zA-Z0-9\\W_]{9,}$";
        String patten2 = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[a-zA-Z0-9]{8,31}$";
        String patten3 = "^[a-z0-9A-Z]+$";
        // 匹配数字、字母 密码的正则表达式
        String patten = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,12}$";

        String password1 = "fuiang123ggg22";      // false
        String password2 = "Fukanggggg2222";  // false
        String password3 = "fuKang123";   // false
        String password4 = "Fukg12356";   // true
        String password5 = "##Fuk%%22";     // false
        String password6 = "###fu123kang%%"; // false
        String password7 = "66FFFFFFFFFFff";// false

        System.out.println(password1.matches(patten));
        System.out.println(password2.matches(patten));
        System.out.println(password3.matches(patten));
        System.out.println(password4.matches(patten));
        System.out.println(password5.matches(patten));
        System.out.println(password6.matches(patten));
        System.out.println(password7.matches(patten));
    }

    @Test
    public void expireTime() {
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("192.168.241.170");
//        conn2.setPort(27017);
//        conn2.setUsername("ess");
//        conn2.setPassword("ess");
//        conn2.setDatabase("ess");
//        conn2.connect();
//        TestExpireIndex testExpireIndex = new TestExpireIndex();
////        testExpireIndex.setExpireTime(new Date());
//        testExpireIndex.setName("李四");
//        TestExpireIndexDao testExpireIndexDao = new TestExpireIndexDao();
//        testExpireIndexDao.insert(testExpireIndex);
    }

    @Test
    public void testRun() {
        for (int i = 0; i < 100; i++) {
            int j = new Random().nextInt(40000 - 35000) + 35000;
            System.out.println(j);
        }
    }

    @Test
    public void testPassword() {
        String match = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,8}$";
        String password1 = "gg1234561";
        String password2 = "123456gg3";
        String password3 = "22atqYYY";
        String password4 = "2AaAa7";
        String password5 = "AaAaAa12";
        String password6 = "...222aa";
        String password7 = "aa123456";
        System.out.println(password1.matches(match));
        System.out.println(password2.matches(match));
        System.out.println(password3.matches(match));
        System.out.println(password4.matches(match));
        System.out.println(password5.matches(match));
        System.out.println(password6.matches(match));
        System.out.println(password7.matches(match));
        boolean matches = DATE_PATTERN.matcher("").matches();
        System.out.println(matches);
    }
}
