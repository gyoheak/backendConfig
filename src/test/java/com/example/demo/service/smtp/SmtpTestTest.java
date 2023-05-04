package com.example.demo.service.smtp;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmtpTestTest {

    @Autowired
    private SmtpService smtpTest;

    @Test
    void sendMail(){
        smtpTest.sendMail("30megapixels@naver.com", "test mail", "test 안녕");
    }
}