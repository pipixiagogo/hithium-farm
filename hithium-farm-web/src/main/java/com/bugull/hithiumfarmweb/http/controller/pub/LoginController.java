package com.bugull.hithiumfarmweb.http.controller.pub;

import com.bugull.hithiumfarmweb.common.validator.ValidatorUtils;
import com.bugull.hithiumfarmweb.common.validator.group.UpdateGroup;
import com.bugull.hithiumfarmweb.config.PropertiesConfig;
import com.bugull.hithiumfarmweb.http.bo.LoginFormBo;
import com.bugull.hithiumfarmweb.http.service.CaptchaService;
import com.bugull.hithiumfarmweb.http.service.SysUserService;
import com.bugull.hithiumfarmweb.http.vo.LoginVo;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 登录模块
 */
@RestController
public class LoginController {

    @Resource
    private CaptchaService captchaService;

    @Resource
    private SysUserService sysUserService;


    @GetMapping("/captcha.jpg")
    public void captcha(HttpServletResponse response, @RequestParam(name = "uuid", required = false) String uuid) throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        //获取图片验证码
        BufferedImage image = captchaService.getCaptcha(uuid);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        IOUtils.closeQuietly(out);
    }

    @PostMapping("/login")
    public ResHelper<LoginVo> login(@RequestBody LoginFormBo loginFormBo) {
        ValidatorUtils.validateEntity(loginFormBo, UpdateGroup.class);
        String msg = captchaService.validaCaptcha(loginFormBo);
        if (!StringUtils.isEmpty(msg)) {
            return ResHelper.error(msg);
        }
        return sysUserService.loginByPassword(loginFormBo);
    }

    /**
     * 刷新token接口
     */
    @PostMapping(value = "/refreshToken")
    public ResHelper<LoginVo> refreshToken(@RequestParam(value = "refreshToken", required = true) String refreshToken) {
        return sysUserService.refreshToken(refreshToken);
    }


}
