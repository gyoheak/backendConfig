package com.example.demo.controller;

import com.example.demo.domain.QUserEntity;
import com.example.demo.domain.UserEntity;
import com.example.demo.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final JPAQueryFactory jpaQueryFactory;

    @GetMapping("/user")
    public UserEntity findUserByJWT(@AuthenticationPrincipal User user){
        QUserEntity qUser = QUserEntity.userEntity;
        UserEntity result = jpaQueryFactory
                .selectFrom(qUser)
                .where(qUser.email.eq(user.getUsername()))
                .fetchOne();
        return result;
    }
}
