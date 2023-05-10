package com.myserver.myApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.myserver.myApp.dto.MailForm;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MailController {
    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("mail")
    public String mail() {
        return "mail/new";
    }

    @PostMapping("/mail/send")
    public String sendMail(MailForm form) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(form.getTo());
        message.setSubject(form.getSubject());
        message.setText(form.getText());
        log.info(message.toString());
        mailSender.send(message);
        return "redirect:/mail";
    }
}
