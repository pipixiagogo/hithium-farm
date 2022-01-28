package com.bugull.hithiumfarmweb.http.controller;

import com.bugull.hithiumfarmweb.http.service.ConnetDeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 连接EMQ权限认证接口
 * TODO 连接数据库
 */
@Controller
@RequestMapping("/auth_verify")
public class VerifyClientController {


    @Resource
    private ConnetDeviceService connetDeviceService;

    private static final Logger log = LoggerFactory.getLogger(VerifyClientController.class);

    /**
     * 设备连接权限校验
     *
     * @param clientid 客户端ID
     * @param username 用户名
     * @param password 密码
     * @return 认证失败，API 返回4xx
     * 认证成功，API 返回200
     */
    @PostMapping(value = "/auth")
    public void auth(String clientid, String username
            , String password, HttpServletResponse response) {
        try {
            if (connetDeviceService.queryByClinetIdOrUsername(username)) {
                log.info("验证权限,成功接入 clientid:{},username:{},password:{}", clientid, username, password);
                response.setStatus(200);
            } else {
                log.info("验证权限失败,无法接入 clientid:{},username:{},password:{}", clientid, username, password);
                response.setStatus(401);
            }
        }catch (Exception e){
            log.info("验证权限失败,无法接入 clientid:{},username:{},password:{},e:{}", clientid, username, password,e);
            response.setStatus(401);
        }

    }

    /**
     * 验证是否为超级用户
     *
     * @param clientid 客户端ID
     * @param username 用户名
     * @return 认证失败，API 返回4xx
     * 认证成功，API 返回200
     *
     * 确认为: 超级用户
     */
    @PostMapping("/isSuper")
    public void isSuper(String clientid, String username, HttpServletResponse response) {
        /**
         * 能接入即为超级用户
         */
        response.setStatus(200);
    }

    /**
     * 设备订阅主题、发布内容时执行，当设备为超级用户时 不执行
     *
     * @return ACL失败，API 返回4xx
     * ACL成功，API 返回200
     * <p>
     * publish access 2
     * subscibe access 1
     *
     * 方案1  只进行接入的权限判断 发布订阅权限不管
     *
     * 方案2  接入权限判断
     *
     * TODO 日志问题
     */
    @GetMapping("/acl")
    public void acl(String access, String username, String clientid, String ipaddr, String topic, HttpServletResponse response) {

        response.setStatus(200);
        /**
         * 发布订阅、只能订阅对应的主题
         */
//        ConnetDevice connetDevice = connetDeviceService.queryByClinetIdResult(username);
//        try {
//            if (access.equals("2")) {
//                /**
//                 * 发布 D2S
//                 */
//                if (connetDevice != null) {
//                    if (!connetDevice.getDTStopic().equals("#")) {
//                        if (topic.equals(connetDevice.getDTStopic())) {
//                            log.info("验证权限成功发布 access:{},clientid:{},username:{},ipaddr:{},topic:{}", access, clientid, username, ipaddr, topic);
//                            response.setStatus(200);
//                        } else {
//                            log.info("验证权限失败发布 access:{},clientid:{},username:{},ipaddr:{},topic:{}", access, clientid, username, ipaddr, topic);
//                            response.setStatus(401);
//                        }
//                        return;
//                    }
//                    log.info("验证权限成功发布 access:{},clientid:{},username:{},ipaddr:{},topic:{}", access, clientid, username, ipaddr, topic);
//                    response.setStatus(200);
//                    return;
//                }
//            }
//
//            if (access.equals("1")) {
//                /**
//                 * 订阅 D2S
//                 */
//                if (connetDevice != null) {
//                    if (!connetDevice.getSTDtopic().equals("#")) {
//                        if (topic.equals(connetDevice.getSTDtopic())) {
//                            log.info("验证权限成功订阅 access:{},clientid:{},username:{},ipaddr:{},topic:{}", access, clientid, username, ipaddr, topic);
//                            response.setStatus(200);
//                        } else {
//                            log.info("验证权限失败订阅 access:{},clientid:{},username:{},ipaddr:{},topic:{}", access, clientid, username, ipaddr, topic);
//                            response.setStatus(401);
//                        }
//                        return;
//                    }
//                    log.info("验证权限成功订阅 access:{},clientid:{},username:{},ipaddr:{},topic:{}", access, clientid, username, ipaddr, topic);
//                    response.setStatus(200);
//                    return;
//                }
//            }
//            response.setStatus(401);
//        }catch (Exception e){
//            log.info("验证权限失败订阅 access:{},clientid:{},username:{},ipaddr:{},topic:{},e:{}", access, clientid, username, ipaddr, topic,e);
//            response.setStatus(401);
//        }

    }
}
