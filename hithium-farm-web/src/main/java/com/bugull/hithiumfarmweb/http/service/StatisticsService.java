package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.config.RedisPoolUtil;
import com.bugull.hithiumfarmweb.http.bo.CapacityNumBo;
import com.bugull.hithiumfarmweb.http.bo.StatisticBo;
import com.bugull.hithiumfarmweb.http.dao.DeviceDao;
import com.bugull.hithiumfarmweb.http.entity.Device;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.mongodb.DBObject;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static com.bugull.hithiumfarmweb.common.Const.*;

@Service
public class StatisticsService {

    @Resource
    private DeviceDao deviceDao;
    @Resource
    private RedisPoolUtil redisPoolUtil;
    public static final Map<Integer, String> QUARTER_NAME = new HashMap<>();

    static {
        QUARTER_NAME.put(1, "第一季度");
        QUARTER_NAME.put(2, "第二季度");
        QUARTER_NAME.put(3, "第三季度");
        QUARTER_NAME.put(4, "第四季度");
    }

    public ResHelper<List<StatisticBo>> statisticNetInNum(String type, Integer year) {
        Date startTime = getStartTimeOfYear(year);
        Date endTime = getEndTimeOfYear(year);
        Iterable<DBObject> objs = deviceDao.aggregate().match(deviceDao.query()
                .greaterThanEquals("accessTime", startTime)
                .lessThanEquals("accessTime", endTime))
                .project("{yearMonthDay: { $dateToString: { format: '%Y-%m', date: '$accessTime' } }}")
                .group("{_id:'$yearMonthDay',count:{$sum:1}}").results();
        Map<String, StatisticBo> map = getDefaultBo(year);
        if (objs != null) {
            for (DBObject obj : objs) {
                String name = obj.get("_id").toString();
                int count = Double.valueOf(obj.get("count").toString()).intValue();
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
        if (MONTH.equals(type)) {
            return ResHelper.success("", aim);
        } else {
            List<StatisticBo> list = new ArrayList<>();
            for (int i = 1; i <= 4; i++) {
                StatisticBo bo = new StatisticBo(QUARTER_NAME.get(i), 0);
                for (int j = 0; j < 3; j++) {
                    int count = aim.get(j + (i - 1) * 3).getCount();
                    bo.setCount(bo.getCount() + count);
                }
                list.add(bo);
            }
            return ResHelper.success("", list);
        }
    }

    private Map<String, StatisticBo> getDefaultBo(Integer year) {
        Map<String, StatisticBo> map = new TreeMap<>();
        for (int i = 1; i <= 12; i++) {
            map.put(year + "-" + (i > 9 ? i : "0" + i), new StatisticBo(i + "", 0));
        }
        return map;
    }

    private Date getEndTimeOfYear(Integer year) {
        Calendar yearEnd = Calendar.getInstance();
        yearEnd.set(Calendar.YEAR, year + 1);
        yearEnd.set(Calendar.MONTH, 0);
        yearEnd.set(Calendar.DATE, 1);
        yearEnd.set(Calendar.HOUR_OF_DAY, 0);
        yearEnd.set(Calendar.MINUTE, 0);
        yearEnd.set(Calendar.SECOND, 0);
        yearEnd.set(Calendar.MILLISECOND, -1);
        return yearEnd.getTime();
    }

    private Date getStartTimeOfYear(Integer year) {
        Calendar yearStart = Calendar.getInstance();
        yearStart.set(Calendar.YEAR, year);
        yearStart.set(Calendar.MONTH, 0);
        yearStart.set(Calendar.DATE, 1);
        yearStart.set(Calendar.HOUR_OF_DAY, 0);
        yearStart.set(Calendar.MINUTE, 0);
        yearStart.set(Calendar.SECOND, 0);
        yearStart.set(Calendar.MILLISECOND, 0);
        return yearStart.getTime();
    }

    public ResHelper<CapacityNumBo> statisticCapacityNum() {
        Jedis jedis = null;
        try {
            List<Device> deviceList = deviceDao.findAll();
            jedis=redisPoolUtil.getJedis();
            if (!CollectionUtils.isEmpty(deviceList) && deviceList.size() > 0) {
                BigDecimal discharBig=new BigDecimal(0);
                BigDecimal charBig=new BigDecimal(0);
                for(Device device:deviceList){
                    /**
                     * 放电量
                     */
                    String dischargeCapcitySum = jedis.get(DISCHARGECAPACITYSUM + device.getDeviceName());
                    if(!StringUtils.isEmpty(dischargeCapcitySum)){
                        BigDecimal disChargeBigDecimal=new BigDecimal(dischargeCapcitySum);
                        discharBig=discharBig.add(disChargeBigDecimal);
                    }
                    /**
                     * 充电量
                     */
                    String chargeCapcitySum = jedis.get(CHARGECAPACITYSUM + device.getDeviceName());
                    if(!StringUtils.isEmpty(chargeCapcitySum)) {
                        BigDecimal chargeBigDecimal = new BigDecimal(chargeCapcitySum);
                        charBig=charBig.add(chargeBigDecimal);
                    }
                }
                CapacityNumBo capacityNumBo = new CapacityNumBo();
                capacityNumBo.setDischargeCapacitySum(discharBig.toString());
                capacityNumBo.setChargeCapacitySum(charBig.toString());
                return ResHelper.success("",capacityNumBo);
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return ResHelper.error("");
    }
}
