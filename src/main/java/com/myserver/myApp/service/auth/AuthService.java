package com.myserver.myApp.service.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.myserver.myApp.config.auth.TokenProvider;
import com.myserver.myApp.dto.MemberRequestForm;
import com.myserver.myApp.dto.MemberResponseForm;
import com.myserver.myApp.dto.auth.TokenForm;
import com.myserver.myApp.dto.auth.TokenRequestForm;
import com.myserver.myApp.entity.Member;
import com.myserver.myApp.entity.auth.RefreshToken;
import com.myserver.myApp.repository.MemberRepository;
import com.myserver.myApp.repository.auth.RefreshTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public MemberResponseForm signup(MemberRequestForm memberRequestForm) {
        if (memberRepository.existsByEmail(memberRequestForm.getEmail())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        // 비밀번호 암호화
        Member member = memberRequestForm.toMember(passwordEncoder);
        return MemberResponseForm.of(memberRepository.save(member));
    }

    @Transactional
    public TokenForm login(MemberRequestForm memberRequestForm) {
        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
        log.info("AUTHENTICATION TOKEN 생성");
        UsernamePasswordAuthenticationToken authenticationToken = memberRequestForm.toAuthentication();
        log.info(authenticationToken.toString());

        // 2. 실제로 검증 (사용자 비밀번호 체크)
        log.info("AUTHENTICATION 검증");
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info(authentication.toString());

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        log.info("JWT 토큰 생성");
        TokenForm tokenForm = tokenProvider.generateTokenDto(authentication);
        log.info(tokenForm.toString());

        // 4. RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(tokenForm.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);

        // 5. 토큰 발급
        return tokenForm;
    }

    @Transactional
    public TokenForm reissue(TokenRequestForm tokenRequestForm) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequestForm.getRefreshToken())) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestForm.getAccessToken());

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByKey(tokenRequestForm.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(tokenRequestForm.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenForm tokenForm = tokenProvider.generateTokenDto(authentication);

        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateTokenValue(tokenForm.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // 7. 토큰 발급
        return tokenForm;
    }
}
