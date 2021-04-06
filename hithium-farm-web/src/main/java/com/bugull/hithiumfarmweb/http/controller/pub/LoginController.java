package com.bugull.hithiumfarmweb.http.controller.pub;

import com.bugull.hithiumfarmweb.http.bo.LoginFormBo;
import com.bugull.hithiumfarmweb.http.bo.UpdateUserBo;
import com.bugull.hithiumfarmweb.http.controller.pri.AbstractController;
import com.bugull.hithiumfarmweb.http.entity.Captcha;
import com.bugull.hithiumfarmweb.http.service.CaptchaService;
import com.bugull.hithiumfarmweb.http.service.UserService;
import com.bugull.hithiumfarmweb.http.vo.LoginVo;
import com.bugull.hithiumfarmweb.http.vo.UserVo;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 登录模块
 */
@RestController
public class LoginController{

    @Resource
    private CaptchaService captchaService;

    @Resource
    private UserService userService;


    @ApiOperation("获取验证码")
    @GetMapping("captcha.jpg")
    @ApiImplicitParam(name = "uuid", value = "UUID随机码", required = true)
    public void captcha(HttpServletResponse response, @RequestParam(name = "uuid",required = true) String uuid ) throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        //获取图片验证码
        BufferedImage image = captchaService.getCaptcha(uuid);

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        IOUtils.closeQuietly(out);
    }

    @ApiOperation("登录接口")
    @PostMapping("login")
    @ApiImplicitParam(name = "loginFormBo", value = "登录接口", required = true, paramType = "body", dataTypeClass = LoginFormBo.class, dataType = "LoginFormBo")
    public ResHelper<LoginVo> login(@RequestBody LoginFormBo loginFormBo) {
        Captcha captcha = captchaService.validaCaptcha(loginFormBo.getUuid());
        if (captcha != null && !captcha.getCode().equalsIgnoreCase(loginFormBo.getCaptcha())) {
            return ResHelper.error("登录失败,验证码错误");
        }
        if (System.currentTimeMillis() > captcha.getExpireTime().getTime()) {
            return ResHelper.error("登录失败,验证码过期");
        }
        return userService.loginByPassword(loginFormBo);
    }

    /**
     * 刷新token接口
     */
    @ApiOperation(value = "刷新token接口", response = ResHelper.class)
    @RequestMapping(value = "/refreshToken", method = RequestMethod.POST)
    @ApiImplicitParam(name = "refreshToken", value = "刷新的token", required = true)
    public ResHelper<LoginVo> refreshToken(@RequestParam(value = "refreshToken", required = true) String refreshToken) {
        return userService.refreshToken(refreshToken);
    }



}
