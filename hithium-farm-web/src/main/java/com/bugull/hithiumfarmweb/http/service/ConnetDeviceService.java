package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.Const;
import com.bugull.hithiumfarmweb.config.RedisPoolUtil;
import com.bugull.hithiumfarmweb.http.bo.ConnetDeviceBo;
import com.bugull.hithiumfarmweb.http.dao.ConnetDeviceDao;
import com.bugull.hithiumfarmweb.http.entity.ConnetDevice;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.Date;

import static com.bugull.hithiumfarmweb.common.Const.PROJECT_TYPE_ESS;

@Service
public class ConnetDeviceService {

    @Resource
    private ConnetDeviceDao connetDeviceDao;
    @Resource
    private RedisPoolUtil redisPoolUtil;

    public ResHelper<Void> registerConnetDevice(ConnetDeviceBo connetDeviceBo) {
        /**
         * 存入redis操作
         */
        Jedis jedis = null;
        try {
            jedis = redisPoolUtil.getJedis();
            String connetMsg = jedis.get(connetDeviceBo.getConnetName());
            if (StringUtils.isEmpty(connetMsg)) {
                /**
                 * redis为空
                 */
                Query existsQuery = new Query();
                existsQuery.addCriteria(Criteria.where("connetName").is(connetDeviceBo.getConnetName()));
                if (!connetDeviceDao.exists(existsQuery)) {
                    ConnetDevice connetDevice = new ConnetDevice();
                    BeanUtils.copyProperties(connetDeviceBo, connetDevice);
                    connetDevice.setClientId(connetDevice.getConnetName());
                    connetDevice.setCreateDate(new Date());
                    if (connetDeviceBo.getTopic() != null && connetDeviceBo.getTopic().equals("#")) {
                        connetDevice.setDTStopic("#");
                        connetDevice.setSTDtopic("#");
                    } else {
                        connetDevice.setSTDtopic(Const.getS2DTopic(connetDevice.getProjectType(), connetDevice.getConnetName()));
                        connetDevice.setDTStopic(Const.getD2STopic(connetDevice.getProjectType(), connetDevice.getConnetName()));
                    }
                    connetDeviceDao.saveConnetDevice(connetDevice);
                    String deviceInfo = connetDevice.getClientId() + "," + connetDevice.getDTStopic() + "," + connetDevice.getSTDtopic();
                    jedis.set(PROJECT_TYPE_ESS + connetDevice.getConnetName(), deviceInfo);
                    return ResHelper.success("添加设备白名单成功");
                } else {
                    /**
                     * 数据库存在 取出存入redis
                     */
                    Query findQuery=new Query();
                    findQuery.addCriteria(Criteria.where("connetName").is(connetDeviceBo.getConnetName()));
                    ConnetDevice connetDevice =connetDeviceDao.findConnetDevice(findQuery);
                    String deviceInfo = connetDevice.getClientId() + "," + connetDevice.getDTStopic() + "," + connetDevice.getSTDtopic();
                    jedis.set(PROJECT_TYPE_ESS + connetDevice.getConnetName(), deviceInfo);
                }
            }
            return ResHelper.error("该设备已经存在，无法在进行注册");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean queryByClinetIdOrUsername(String username) {
        Jedis jedis = null;
        try {
            jedis = redisPoolUtil.getJedis();
            if (jedis.exists(username)) {
                return true;
            }
            Query findQuery=new Query();
            findQuery.addCriteria(Criteria.where("connectName").is(username));
            ConnetDevice connetName = connetDeviceDao.findConnetDevice(findQuery);
            if (connetName != null) {
                String deviceInfo = connetName.getClientId() + "," + connetName.getDTStopic() + "," + connetName.getSTDtopic();
                jedis.set(PROJECT_TYPE_ESS + connetName.getConnetName(), deviceInfo);
                return true;
            }
            return connetDeviceDao.exists(findQuery);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public ConnetDevice queryByClinetIdResult(String username) {
        Jedis jedis = null;
        try {
            jedis = redisPoolUtil.getJedis();
            String deviceInfo = jedis.get(username);
            if (!StringUtils.isEmpty(deviceInfo)) {
                String[] str = deviceInfo.split(",");
                ConnetDevice connetDevice = new ConnetDevice();
                connetDevice.setSTDtopic(str[2]);
                connetDevice.setDTStopic(str[1]);
                return connetDevice;
            }
            Query findQuery=new Query();
            findQuery.addCriteria(Criteria.where("connetName").is(username));
           return connetDeviceDao.findConnetDevice(findQuery);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
