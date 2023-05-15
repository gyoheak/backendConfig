package com.myserver.myApp.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myserver.myApp.dto.MemberRequestForm;
import com.myserver.myApp.dto.MemberResponseForm;
import com.myserver.myApp.dto.auth.TokenForm;
import com.myserver.myApp.dto.auth.TokenRequestForm;
import com.myserver.myApp.service.auth.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponseForm> signup(@RequestBody MemberRequestForm memberRequestForm) {
        log.info("회원가입 요청: {}", memberRequestForm);
        return ResponseEntity.ok(authService.signup(memberRequestForm));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenForm> login(@RequestBody MemberRequestForm memberRequestForm) {
        log.info("로그인 요청: {}", memberRequestForm);
        return ResponseEntity.ok(authService.login(memberRequestForm));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenForm> reissue(@RequestBody TokenRequestForm tokenRequestDto) {
        return ResponseEntity.ok(authService.reissue(tokenRequestDto));
    }
}
