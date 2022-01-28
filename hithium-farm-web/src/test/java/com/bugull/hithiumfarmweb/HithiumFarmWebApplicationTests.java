package com.bugull.hithiumfarmweb;


import com.bugull.hithiumfarmweb.entity.PowerInstructions;
import com.bugull.hithiumfarmweb.http.bo.StatisticBo;
import com.bugull.hithiumfarmweb.http.dao.ExportRecordDao;
import com.bugull.hithiumfarmweb.http.entity.ExportRecord;
import com.bugull.hithiumfarmweb.http.service.ExcelExportService;
import com.bugull.hithiumfarmweb.http.vo.CapacityVo;
import com.bugull.hithiumfarmweb.utils.DateUtils;
import com.bugull.hithiumfarmweb.utils.SecretUtil;
import com.bugull.hithiumfarmweb.utils.UUIDUtil;
import com.mongodb.DBObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

@SpringBootTest
public class HithiumFarmWebApplicationTests {
    @Resource
    private ExcelExportService excelExportService;
    @Resource
    private ExportRecordDao exportRecordDao;
    @Resource
    private GridFsTemplate gridFsTemplate;
    @Test
    public void testSystem()  {
//        Date date = DateUtils.addDateDays(new Date(), -10);
//        Query exportQuery = new Query();
//        exportQuery.addCriteria(Criteria.where("recordTime").lte(date));
//        List<ExportRecord> exportRecords = exportRecordDao.findBatch(exportQuery);
//        System.out.println(exportRecords);
//        excelExportService.exportExcel();
    }

    @Test
    public void testSelectDate() {
        long time = DateUtils.getStartTime(new Date(System.currentTimeMillis())).getTime();
        System.out.println(time);
        long time1 = new Date(System.currentTimeMillis()).getTime();
        System.out.println(time1);
        System.out.println(time1 > time);
//        list2.add("7");
//        list2.add("8");

//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("192.168.241.201:27017");
//        conn2.setPort(27017);
//        conn2.setUsername("ess");
//        conn2.setPassword("ess");
//        conn2.setDatabase("ess");
//        conn2.connect();
//        PowerInstructionsDao powerInstructionsDao = new PowerInstructionsDao();
        PowerInstructions powerInstructions = new PowerInstructions();
        Date date = new Date(System.currentTimeMillis());
        Date startTime = DateUtils.getStartTime(date);
        powerInstructions.setStrategyTime(startTime);
        List<Integer> objects = new ArrayList<>();
        for (int i = 0; i < 288; i++) {
            objects.add(i);
        }
        powerInstructions.setPowerStrategy(objects);
        powerInstructions.setDeviceName("xxx");
        powerInstructions.setOrderId(UUIDUtil.get32Str());
//        powerInstructionsDao.insert(powerInstructions);
        System.out.println(powerInstructions);
        /**
         * TODO执行下发功率曲线代码
         */
//        PowerInstructions strategyTime = powerInstructionsDao.query().is("strategyTime", startTime).result();
//        System.out.println(strategyTime);
    }

    //    @Test
    public void initCorp() throws InterruptedException {
        List<Integer> list = new ArrayList<>();
        Boolean b = list.stream().allMatch(item -> item > 2);
        System.out.println(b);
//        for (int i = 0; i < 1000; i++) {
//            String content = System.currentTimeMillis() + "-81a79ea4bf3f49b599dfe58e5c1815a8-" + UUIDUtil.get32Str();
//            String secretKey = "31323334353637383930313233343536";
//            String accessToken = SecretUtil.encryptWithBase64(content, secretKey);
//            System.out.println("accessToken:----->" + accessToken);
//            String result = SecretUtil.decryptWithBase64(accessToken, secretKey);
//            System.out.println("result:----->" + result);
//            Thread.sleep(5000L);
//        }

//        mongo.host=172.24.63.229:27017
//        mongo.port=27017
//        mongo.database=farm
//        mongo.username=farm
//        mongo.password=hithium.db.mongo
//        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
//        conn2.setHost("172.24.63.229:27017");
//        conn2.setPort(27017);
//        conn2.setUsername("farm");
//        conn2.setPassword("hithium.db.mongo");
//        conn2.setDatabase("farm");
//        conn2.connect();
//
//        CorpDao corpDao = new CorpDao();
//        Corp corp = new Corp();
//        corp.setCorpAccessId("HguxpJV3Knf9go3U");
//        corp.setCorpAccessSecret("81a79ea4bf3f49b599dfe58e5c1815a8");
//        List<String> deviceNameList  = Arrays.asList("clld3w7tdxjhvuhf6gzn60whffuy0hyj");
//        corp.setCorpAccessName("深电能科技");
//        corp.setDeviceName(deviceNameList);
//        corp.setCreateTime(new Date(System.currentTimeMillis()));
//        corpDao.insert(corp);
//        /**
//         * corpAccessId  ---->  HguxpJV3Knf9go3U
//         * corpAccessSecret ----> 81a79ea4bf3f49b599dfe58e5c1815a8
//         */
//        String nStr = UUIDUtil.getNStr(16);
//        System.out.println(nStr);
//        String str = UUIDUtil.get32Str();
//        System.out.println(str);
//        for(int i=0;i<100;i++){
//            /**
//             * TODO 颁发的accessToken  根据前面时间戳-公司ID或者公司颁发的ID-UUID随机数  reids 的key 为 公司ID或者公司颁发的ID  获取accessToken
//
//             */
//            String content=System.currentTimeMillis()+"-619379708b504715fd97f1bd-"+ UUIDUtil.get32Str();;
//            String password="31323334353637383930313233343536";
//            String s = SecretUtil.encryptWithBase64(content, password);
//            System.out.println("加密后:"+s);
//            String s1 = SecretUtil.decryptWithBase64(s, password);
//            System.out.println("解密后:"+s1);
//            try {
//                Thread.sleep(5000L);
//            }catch (InterruptedException e){
//                e.printStackTrace();
//            }
//
//        }

//
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

    //    @Test
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


}
