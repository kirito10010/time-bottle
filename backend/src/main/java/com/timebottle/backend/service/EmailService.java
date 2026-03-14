package com.timebottle.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String FROM_EMAIL = "a3025863517@163.com";

    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(toEmail);
        message.setSubject("【拾光瓶】密码重置验证码");
        message.setText("您好！\n\n您正在申请重置拾光瓶账号密码。\n\n验证码：" + code + "\n\n验证码有效期为5分钟，请尽快使用。\n\n如果这不是您的操作，请忽略此邮件。\n\n祝您生活愉快！\n拾光瓶团队");
        
        mailSender.send(message);
    }
}
