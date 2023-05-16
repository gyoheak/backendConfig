package com.myserver.myApp.config.auth;

import org.springframework.stereotype.Component;

import com.myserver.myApp.dto.auth.TokenForm;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Component
@Slf4j
public class TokenProvider {
    private final static String AUTHORITIES_KEY = "auth";

    private final long tokenValidityInMilliseconds;
    private final Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds) {
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public TokenForm generateTokenDto(Authentication authentication) {
        log.info("토큰 발급");
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        log.info(authorities);
        long now = (new Date()).getTime();

        // Access Token
        Date accessTokenExpiresIn = new Date(now + this.tokenValidityInMilliseconds);
        log.info(authentication.toString());
        log.info(authorities);
        log.info(AUTHORITIES_KEY);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName()) // payload "sub": "name"
                .claim(AUTHORITIES_KEY, authorities) // payload "auth": "ROLE_USER"
                .signWith(key, SignatureAlgorithm.HS512) // header "alg": "HS512"
                .setExpiration(accessTokenExpiresIn) // payload "exp": 1516239022 (예시)
                .compact();
        log.info(accessToken);
        // Refresh Token
        Date refreshTokenExpiresIn = new Date(now + this.tokenValidityInMilliseconds * 10000);
        String refreshToken = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(refreshTokenExpiresIn)
                .compact();

        return TokenForm.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        // 토큰 값으로 claim 생성
        // 권한 get
        log.info("토큰 검증");
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        // claim에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // claim 과 authorities를 활용해 User객체 생성
        // 유저 객체를 구성해야됨 - Username,Password,GrantedAuthority 만 있음, 나머지는 인터페이스
        // 확장(이메일,hashed pw,created at 등 - UserDetails, UserDetails Service)
        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
    }

    public boolean validateToken(String token) {
        try {
            // 토큰 값으로 claim 생성
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 토큰 서명");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰");
        } catch (IllegalArgumentException e) {
            log.error("잘못된 JWT 토큰");
        }
        return false;
    }
}
