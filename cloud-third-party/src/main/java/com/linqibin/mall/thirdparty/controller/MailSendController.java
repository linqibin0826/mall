package com.linqibin.mall.thirdparty.controller;


import com.linqibin.common.utils.R;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 发送邮件
 * @author linqibin
 * @date   2023/12/25 22:10
 * @email  1214219989@qq.com
 */
@RestController
@RequestMapping("/mail")
@AllArgsConstructor
public class MailSendController {

    private final JavaMailSender javaMailSender;

    @PostMapping("/sendCode")
    public R sendCode(@RequestParam("mail") String mail, @RequestParam("code") String code) {
        Assert.isTrue(StringUtils.isNotBlank(code), "code字段不可为空串");
        Assert.isTrue(StringUtils.isNotBlank(mail), "mail字段不可为空串");
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("linqibin0826@163.com");
            messageHelper.addTo(mail);
            messageHelper.setSubject("大麦商城验证码");
            messageHelper.setText("正在注册商城: 验证码为:" + code);
        });
        return R.ok();
    }
}
