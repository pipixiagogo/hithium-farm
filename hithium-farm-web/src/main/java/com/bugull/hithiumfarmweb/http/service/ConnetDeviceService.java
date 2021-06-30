package com.bugull.hithiumfarmweb.http.service;

import com.bugull.hithiumfarmweb.common.Const;
import com.bugull.hithiumfarmweb.config.RedisPoolUtil;
import com.bugull.hithiumfarmweb.http.bo.ConnetDeviceBo;
import com.bugull.hithiumfarmweb.http.dao.ConnetDeviceDao;
import com.bugull.hithiumfarmweb.http.entity.ConnetDevice;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
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
                if (!connetDeviceDao.query().is("connetName", connetDeviceBo.getConnetName()).exists()) {
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
                    connetDeviceDao.insert(connetDevice);
                    String deviceInfo = connetDevice.getClientId() + "," + connetDevice.getDTStopic() + "," + connetDevice.getSTDtopic();
                    jedis.set(PROJECT_TYPE_ESS + connetDevice.getConnetName(), deviceInfo);
                    return ResHelper.success("添加设备白名单成功");
                } else {
                    /**
                     * 数据库存在 取出存入redis
                     */
                    ConnetDevice connetDevice = connetDeviceDao.query().is("connetName", connetDeviceBo.getConnetName()).result();
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
            ConnetDevice connetName = connetDeviceDao.query().is("connetName", username).result();
            if (connetName != null) {
                String deviceInfo = connetName.getClientId() + "," + connetName.getDTStopic() + "," + connetName.getSTDtopic();
                jedis.set(PROJECT_TYPE_ESS + connetName.getConnetName(), deviceInfo);
                return true;
            }
            return connetDeviceDao.query().is("connetName", username).exists();
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
            return connetDeviceDao.query().is("connetName", username).result();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
