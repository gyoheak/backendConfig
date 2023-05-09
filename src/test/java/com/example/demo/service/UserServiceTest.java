package com.example.demo.service;

import com.example.demo.domain.UserEntity;
import com.example.demo.dto.SaveUserDto;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void signInTest(){
        //인증번호
//        String randomNumber = userService.temporaryNumbers("myeonggi_ko@tmax.co.kr");
        //회원가입 시도
//        SaveUserDto testUser1 = new SaveUserDto("myeonggi_ko@tmax.co.kr", "1234", randomNumber);
//        userService.save(testUser1);
//        //결과
//        UserEntity result = userRepository.findByEmail(testUser1.getEmail());
//        System.out.println("result.getEmail() = " + result.getEmail());
//        System.out.println("result.getPassword() = " + result.getPassword());
    }
}