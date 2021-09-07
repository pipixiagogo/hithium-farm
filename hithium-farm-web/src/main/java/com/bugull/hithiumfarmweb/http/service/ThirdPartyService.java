package com.bugull.hithiumfarmweb.http.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Set;

@Service
public class ThirdPartyService {
    private static final Logger log = LoggerFactory.getLogger(ThirdPartyService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String from;

    public ThirdPartyService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }


    /**
     * @param toMailReceive 接收邮件集合地址
     * @param subject       邮件主题
     * @param text          邮件文本内容
     * @return              发送邮件是否成功
     */
    public boolean sendEmailWithoutAttachment(String[] toMailReceive, String subject, String text) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mineHelper = new MimeMessageHelper(mimeMessage, true);
            //谁发
            mineHelper.setFrom(from);
            //谁要接收
            mineHelper.setTo(toMailReceive);
            //邮件主题
            mineHelper.setSubject(subject);
            //邮件内容   true 表示带有附件或html
            mineHelper.setText(text,true);
            mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException messagingException) {
            log.error("发送邮件失败:{}");
            return false;
        }
    }

    /**
     * 发送带附件的模板邮件
     * @param toMailReceive 接收邮件集合地址
     * @param subject       邮件主题
     * @return              发送邮件是否成功
     */
    public boolean sendTemplateEmailWithAttachment(String[] toMailReceive, String subject, Map<String,FileSystemResource> attachmentMap) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mineHelper = new MimeMessageHelper(mimeMessage, true);
            //谁发
            mineHelper.setFrom(from);
            //谁要接收
            mineHelper.setTo(toMailReceive);
            //邮件主题
            mineHelper.setSubject(subject);
            //邮件内容   true 表示带有附件或html
            Context context = new Context();
            context.setVariable("github_url", "https://github.com/Folgerjun");
            context.setVariable("blog_url", "http://putop.top/");
            String emailContent = templateEngine.process("mailTemplate", context);
            mineHelper.setText(emailContent,true);
            if(!attachmentMap.isEmpty()){
                Set<Map.Entry<String, FileSystemResource>> entries = attachmentMap.entrySet();
                for(Map.Entry<String, FileSystemResource> entry:entries){
                    mineHelper.addAttachment(entry.getKey(),entry.getValue());
                }
            }
            mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException messagingException) {
            log.error("发送邮件失败:{}");
            return false;
        }
    }

    public boolean sendEmailWithAttachment(String[] toMailReceive, String subject, String text, Map<String,FileSystemResource> attachmentMap){
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mineHelper = new MimeMessageHelper(mimeMessage, true);
            //谁发
            mineHelper.setFrom(from);
            //谁要接收
            mineHelper.setTo(toMailReceive);
            //邮件主题
            mineHelper.setSubject(subject);
            //邮件内容   true 表示带有附件或html
            mineHelper.setText(text,true);
            if(!attachmentMap.isEmpty()){
                Set<Map.Entry<String, FileSystemResource>> entries = attachmentMap.entrySet();
                for(Map.Entry<String, FileSystemResource> entry:entries){
                    mineHelper.addAttachment(entry.getKey(),entry.getValue());
                }
            }
            mailSender.send(mimeMessage);
            return true;
        }catch (MessagingException messagingException){
            log.error("发送邮件失败:{}");
            return false;
        }
    }
}
