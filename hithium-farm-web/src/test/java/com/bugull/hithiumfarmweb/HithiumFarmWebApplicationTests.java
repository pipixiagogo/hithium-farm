package com.bugull.hithiumfarmweb;


import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bugull.hithiumfarmweb.common.BuguPageDao;
import com.bugull.hithiumfarmweb.common.Const;
import com.bugull.hithiumfarmweb.config.CustomThreadFactory;
import com.bugull.hithiumfarmweb.enumerate.DataType;
import com.bugull.hithiumfarmweb.http.bo.StatisticBo;
import com.bugull.hithiumfarmweb.http.bo.TimeOfPriceBo;
import com.bugull.hithiumfarmweb.http.dao.*;
import com.bugull.hithiumfarmweb.http.entity.*;
import com.bugull.hithiumfarmweb.http.service.RealTimeDataService;
import com.bugull.hithiumfarmweb.http.vo.IncomeStatisticOfDayVo;
import com.bugull.hithiumfarmweb.http.vo.IncomeStatisticVo;
import com.bugull.hithiumfarmweb.http.vo.PriceOfPercenVo;
import com.bugull.hithiumfarmweb.utils.DateUtils;
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
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.integration.history.MessageHistory;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.bugull.hithiumfarmweb.common.Const.*;
import static com.bugull.hithiumfarmweb.http.service.StatisticsService.INCOME_RECORD_PREFIX;

public class HithiumFarmWebApplicationTests {

    @Test
    public void contextLoads() {
        String salt = RandomStringUtils.randomAlphanumeric(20);
        String s = new Sha256Hash("123456", salt).toHex();
        System.out.println(salt);
        System.out.println(s);
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
        int i=-1;
        if(i == 0 || i==1){
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
        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
        conn2.setHost("192.168.241.162");
        conn2.setPort(27017);
        conn2.setUsername("ess");
        conn2.setPassword("ess");
        conn2.setDatabase("ess");
        conn2.connect();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        DeviceDao deviceDao = new DeviceDao();
        Device device = deviceDao.query().is("deviceName", "d06a4137967744efa6a24dd564480e0a").result();
        List<TimeOfPriceBo> priceOfTime = device.getPriceOfTime();
        Map<Integer, List<String>> map = new HashMap<>();
        for (TimeOfPriceBo timeOfPriceBo : priceOfTime) {
            String time = timeOfPriceBo.getTime();
            String[] split = time.split(",");
            List<String> list = new ArrayList<>();
            for (String spl : split) {
                list.add(spl);
            }
            map.put(timeOfPriceBo.getType(), list);
        }
        Map<Integer, String> typeOfMap = null;
        try {
            Set<Map.Entry<Integer, List<String>>> entries = map.entrySet();
            for (Map.Entry<Integer, List<String>> entry : entries) {
                List<String> entryValue = entry.getValue();
                for (String value : entryValue) {
                    String[] split = value.split("-");
                    Calendar now = Calendar.getInstance();
                    now.setTime(df.parse(df.format(new Date())));
                    Calendar begin = Calendar.getInstance();
                    begin.setTime(df.parse(split[0]));
                    Calendar end = Calendar.getInstance();
                    end.setTime(df.parse(split[1]));
                    /**
                     * 不相等时候在查看是否在区间内
                     */
                    if (!now.equals(begin) && !now.equals(end)) {
                        if (now.after(begin) && now.before(end)) {
                            System.out.println("不相等" + begin.getTime() + "---" + end.getTime());
                            if (typeOfMap == null) {
                                typeOfMap = new HashMap<>();
                            }
                            typeOfMap.put(entry.getKey(), split[0] + "-" + split[1]);
                        }
                    } else {
                        /**
                         * 相同的话  会有两个区间出来 取哪个？
                         */
                        if (typeOfMap == null) {
                            typeOfMap = new HashMap<>();
                        }
                        typeOfMap.put(entry.getKey(), split[0] + "-" + split[1]);
                        System.out.println("相等" + entry.getKey() + "#####" + begin.getTime() + "---" + end.getTime());
                    }
                }
            }
        } catch (Exception e) {

        }
        List<BigDecimal> bigDecimalList = null;
        for (Map.Entry<Integer, String> entry : typeOfMap.entrySet()) {
            Integer key = entry.getKey();
            List<TimeOfPriceBo> ofPriceBos = priceOfTime.stream().filter(timeOfPriceBo -> {
                return timeOfPriceBo.getType() == key;
            }).collect(Collectors.toList());
            BigDecimal bigDecimal = new BigDecimal(ofPriceBos.get(0).getPrice());
            if (bigDecimalList == null) {
                bigDecimalList = new ArrayList<>();
            }
            bigDecimalList.add(bigDecimal);
        }
        /**
         * 放电降序取第一个  充电升序取第一个
         */
        bigDecimalList = bigDecimalList.stream().sorted(new Comparator<BigDecimal>() {
            @Override
            public int compare(BigDecimal o1, BigDecimal o2) {
                return o2.compareTo(o1);
            }
        }).collect(Collectors.toList());

        for (BigDecimal bigDecimal : bigDecimalList) {
            System.out.println(bigDecimal);
        }

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

        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
        conn2.setHost("172.24.63.229");
        conn2.setPort(27017);
        conn2.setUsername("farm");
        conn2.setPassword("hithium.db.mongo");
        conn2.setDatabase("farm");
        conn2.connect();
        DeviceDao deviceDao = new DeviceDao();
        DBObject group = new BasicDBObject();
        group.put("_id", null);
        DBObject toDouble = new BasicDBObject();
        toDouble.put("$toDouble", "$longitude");
        group.put("sum", new BasicDBObject("$sum", toDouble));
        Iterable<DBObject> iterable = deviceDao.aggregate().group(group).results();
        if (iterable != null) {
            for (DBObject object : iterable) {
                Double sum = (Double) object.get("sum");
                System.out.println(sum);
            }
        }

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
        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
        conn2.setHost("192.168.241.162");
        conn2.setPort(27017);
        conn2.setUsername("ess");
        conn2.setPassword("ess");
        conn2.setDatabase("ess");
        conn2.connect();
        DeviceDao deviceDao = new DeviceDao();

        Device result = deviceDao.query().is("deviceName", "d06a4137967744efa6a24dd564480e0a").result();
        System.out.println(result);
    }

    @Test
    public void testRemoveTemp() {
        /**
         * 根据当前时间查询出当前电价 计算充放电收益
         */
        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
        conn2.setHost("172.24.63.229");
        conn2.setPort(27017);
        conn2.setUsername("farm");
        conn2.setPassword("hithium.db.mongo");
        conn2.setDatabase("farm");
        conn2.connect();
        DeviceDao deviceDao = new DeviceDao();
        Iterable<DBObject> objs = deviceDao.aggregate()
                .group("{_id:{ $dateToString: { format: '%Y-%m', date: '$accessTime' } },count:{$sum:{$toDouble:'$stationPower'}}}").results();
        Map<String, StatisticBo> map = getDefaultBo(2021);
        if (objs != null) {
            for (DBObject obj : objs) {
                String name = obj.get("_id").toString();
                Double count = Double.valueOf(obj.get("count").toString());
                StatisticBo bo = map.get(name);
                if (bo != null) {
                    bo.setCount(count);
                }
            }
        }
        List<StatisticBo> aim = new ArrayList<>(map.values());

        Collections.sort(aim, new Comparator<StatisticBo>() {
            @Override
            public int compare(StatisticBo o1, StatisticBo o2) {
                return Integer.parseInt(o1.getDate()) - Integer.parseInt(o2.getDate());
            }
        });
        List<StatisticBo> result = new ArrayList<>(map.values().size());
//        for (int i = 0; i < 4; i++) {
//            result.add(i,getStatisticBo(i,aim));
//        }
        /**
         * 生成12个集合
         */
        /**
         * 第一个集合
         */
        for (int i = 0; i < 11; i++) {
            result.add(getStatisticBo(aim, i));
        }
        for (StatisticBo statisticBo1 : result) {
            System.out.println(statisticBo1);
        }


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
        Jedis jedis = new Jedis("192.168.241.162", 6379);
        jedis.auth("redis.hithium.pwd");
//        Set<String> keys = jedis.keys("DEIVCE_INCOME_RECORED_广东省_*");
//        for(String key:keys){
//            System.out.println(key);
//        }
//        System.out.println(getDate(new Date()));
        Set<String> keys = jedis.keys("DEIVCE_INCOME_RECORED_2021-04-??_广东省_珠海市");
        System.out.println(keys.size());
//        Set<String> keys = jedis.keys(getDate(new Date()));
        for (String key : keys) {
            System.out.println(key);
        }
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
        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
        conn2.setHost("172.24.63.229");
        conn2.setPort(27017);
        conn2.setUsername("farm");
        conn2.setPassword("hithium.db.mongo");
        conn2.setDatabase("farm");
        conn2.connect();
        DeviceDao deviceDao = new DeviceDao();
        Device device = deviceDao.query().is("deviceName", "705780da821448599201d2f4cf68d6a1").result();

        List<TimeOfPriceBo> priceOfTime = device.getPriceOfTime();
        Map<Integer, List<String>> map = new HashMap<>();
        for (TimeOfPriceBo priceBo : priceOfTime) {
            String[] split = priceBo.getTime().split(",");
            List<String> list = new ArrayList<>();
            for (String spl : split) {
                list.add(spl);
            }
            map.put(priceBo.getType(), list);
        }
        List<PriceOfPercenVo> priceOfPercenVoList = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
            List<String> entryValue = entry.getValue();
            try {
                for (String value : entryValue) {
                    PriceOfPercenVo priceOfPercenVo = new PriceOfPercenVo();
                    String[] split = value.split("-");
                    Calendar begin = Calendar.getInstance();
                    begin.setTime(DateUtils.dateToStrWithHHmmWith(split[0]));
                    Calendar end = Calendar.getInstance();
                    end.setTime(DateUtils.dateToStrWithHHmmWith(split[1]));
                    /**
                     * 不相等时候在查看是否在区间内
                     */
                    String s = null;
                    if (begin.before(end)) {
                        s = DateUtils.timeDifferent(end.getTime(), begin.getTime());
                    } else {
                        Date endDate = DateUtils.addDateHours(end.getTime(), 24);
                        s = DateUtils.timeDifferent(endDate, begin.getTime());
                    }
                    priceOfPercenVo.setType(entry.getKey());
                    priceOfPercenVo.setTime(value);
                    priceOfPercenVo.setMinute(s);
                    priceOfPercenVoList.add(priceOfPercenVo);
                }
            } catch (Exception e) {

            }

        }
        for (PriceOfPercenVo priceOfPercenVo : priceOfPercenVoList) {
            System.out.println(priceOfPercenVo);
        }

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
        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
        conn2.setHost("192.168.241.162");
        conn2.setPort(27017);
        conn2.setUsername("ess");
        conn2.setPassword("ess");
        conn2.setDatabase("ess");
        conn2.connect();
        DeviceDao deviceDao = new DeviceDao();
        Iterable<DBObject> iterable = deviceDao.aggregate().group("{_id:null,count:{$sum:{$toDouble:'$chargeCapacitySum'}}}").results();
        for (DBObject object : iterable) {
            Double o = (Double) object.get("count");
            System.out.println(o);
        }
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
    @Test
    public void noModelWrite() {
        // 写法1
        String fileName = "D://noModelWrite" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcel.write(fileName).head(head()).sheet("模板").doWrite(dataList());
    }

    private List<List<String>> head() {
        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
        conn2.setHost("192.168.241.162");
        conn2.setPort(27017);
        conn2.setUsername("ess");
        conn2.setPassword("ess");
        conn2.setDatabase("ess");
        conn2.connect();
        BmsCellTempDataDicDao bmsCellTempDataDicDao = new BmsCellTempDataDicDao();
        BmsCellTempDataDic results = bmsCellTempDataDicDao.query().is("deviceName", "d06a4137967744efa6a24dd564480e0a").result();
        Map<String, Integer> tempMap = results.getTempMap();
        List<List<String>> list = new ArrayList<List<String>>();
        List<String> head0 = new ArrayList<String>();
        head0.add("字符串");
        List<String> head1 = new ArrayList<String>();
        head1.add("数字" + System.currentTimeMillis());
        List<String> head2 = new ArrayList<String>();
        head2.add("日期" + System.currentTimeMillis());
        list.add(head0);
        list.add(head1);
        list.add(head2);
        for (Map.Entry<String, Integer> entry : tempMap.entrySet()) {
            List<String> head3 = new ArrayList<String>();
            head3.add(entry.getKey());
            list.add(head3);
        }
        return list;
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
        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
        conn2.setHost("192.168.241.162");
        conn2.setPort(27017);
        conn2.setUsername("ess");
        conn2.setPassword("ess");
        conn2.setDatabase("ess");
        conn2.connect();
        IncomeEntityDao incomeEntityDao = new IncomeEntityDao();
        BuguQuery<IncomeEntity> incomeEntityBuguQuery = incomeEntityDao.query().is("deviceName", "d06a4137967744efa6a24dd564480e0a");
        Iterable<DBObject> iterable = incomeEntityDao.aggregate().match(incomeEntityBuguQuery).group(INCOMEOFDAY).sort("{_id:-1}").limit(7).results();
        List<IncomeStatisticOfDayVo> incomeStatisticOfDayVoList = new ArrayList<>();
        if (iterable != null) {
            for (DBObject object : iterable) {
                String time = (String) object.get("_id");
                Double count = Double.valueOf(object.get("count").toString());
                IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
                incomeStatisticOfDayVo.setDay(time);
                incomeStatisticOfDayVo.setIncome(BigDecimal.valueOf(count).toString());
                incomeStatisticOfDayVoList.add(incomeStatisticOfDayVo);
            }
        }

        /**
         * 创建 空的List<IncomeStatisticOfDayVo></>  将有值的替换 无值的为空  年份就先创建3年吧  月份6个月  天数7天
         */
        List<IncomeStatisticOfDayVo> incomeStatisticOfDayVos = new ArrayList<>();


        for (int i = 0; i < 7; i++) {
            IncomeStatisticOfDayVo incomeStatisticOfDayVo = new IncomeStatisticOfDayVo();
            incomeStatisticOfDayVo.setDay(DateUtils.format(DateUtils.addDateDays(new Date(), -i)));
            incomeStatisticOfDayVos.add(incomeStatisticOfDayVo);
        }

        for (IncomeStatisticOfDayVo incomeStatisticOfDayVo : incomeStatisticOfDayVos) {
            for (IncomeStatisticOfDayVo statisticOfDayVo : incomeStatisticOfDayVoList) {
                if (incomeStatisticOfDayVo.getDay().equals(statisticOfDayVo.getDay())) {
                    incomeStatisticOfDayVo.setIncome(statisticOfDayVo.getIncome());
                }
            }
        }

        System.out.println(incomeStatisticOfDayVos.isEmpty());
        List<String> device = new ArrayList<>();
        System.out.println(device.isEmpty());
    }

    @Test
    public void testArraysList() {
        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
        conn2.setHost("192.168.241.162");
        conn2.setPort(27017);
        conn2.setUsername("ess");
        conn2.setPassword("ess");
        conn2.setDatabase("ess");
        conn2.connect();
        DeviceDao deviceDao = new DeviceDao();
        Device device = deviceDao.query().is("deviceName", "d06a4137967744efa6a24dd564480e0a").result();
        Map<Integer, List<String>> map = new HashMap<>();
        for (TimeOfPriceBo priceBo : device.getPriceOfTime()) {
            String[] split = priceBo.getTime().split(",");
            List<String> list = new ArrayList<>();
            for (String spl : split) {
                list.add(spl);
            }
            map.put(priceBo.getType(), list);
        }

        System.out.println(map.size());


        Map<Integer, List<String>> map2 = new HashMap<>();
        for (TimeOfPriceBo priceBo : device.getPriceOfTime()) {
            String[] split = priceBo.getTime().split(",");
//            List<String> list = new ArrayList<>();
//            for (String spl : split) {
//                list.add(spl);
//            }
            map2.put(priceBo.getType(), Arrays.asList(split));
        }

        System.out.println(map2.size());
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
        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
        conn2.setHost("192.168.241.162");
        conn2.setPort(27017);
        conn2.setUsername("ess");
        conn2.setPassword("ess");
        conn2.setDatabase("ess");
        conn2.connect();

//        int i=-1;
//        int y=-1;
//        BigDecimal bigDecimalOfI = new BigDecimal(i);
//        BigDecimal bigDecimalOfY = new BigDecimal(y);
//
//        BigDecimal multiply = bigDecimalOfI.multiply(bigDecimalOfY);
//        System.out.println(multiply);
        DeviceDao deviceDao = new DeviceDao();
        System.out.println("执行了" + new Date());
        long count = deviceDao.count();
        int pageSize = 100;
        Date date = new Date();
//        Date time = DateUtils.addDateDays(date, -1);
        String time = "2021-04-16";
        RealTimeDataService realTimeDataService = new RealTimeDataService();
        ExportRecordDao exportRecordDao = new ExportRecordDao();
        int ceil = (int) Math.ceil((double) count / (double) pageSize);
        System.out.println(ceil);
        Date date1 = new Date();
        Date beforeDate = DateUtils.addDateDays(date1, -1);
        String time1 = DateUtils.format(beforeDate);
        System.out.println(time1);
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
        BuguConnection conn2 = BuguFramework.getInstance().createConnection();
        conn2.setHost("192.168.241.162");
        conn2.setPort(27017);
        conn2.setUsername("ess");
        conn2.setPassword("ess");
        conn2.setDatabase("ess");
        conn2.connect();
        //TODO delete 删除超过一个月的excel
        ExportRecordDao exportRecordDao = new ExportRecordDao();
        List<ExportRecord> exportRecords = exportRecordDao.query().lessThanEquals("recordTime", new Date()).results();

        BuguFS fs = BuguFSFactory.getInstance().create();
        for(ExportRecord exportRecord:exportRecords){
            fs.remove(exportRecord.getFilename());
            exportRecordDao.remove(exportRecord);
        }
    }
}
