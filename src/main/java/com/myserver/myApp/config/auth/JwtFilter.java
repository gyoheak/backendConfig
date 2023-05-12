package com.myserver.myApp.config.auth;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {
    private final static String AUTHORIZATION_HEADER = "auth";
    private final static String BEARER_PERFIX = "Bearer";
    private final TokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        log.info("--- FILTER RUNNING ---");

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String jwtToken = resolveToken(httpServletRequest);
        log.info("TOKEN: " + jwtToken);

        if (jwtToken != null && tokenProvider.validateToken(jwtToken)) {
            Authentication authentication = tokenProvider.getAuthentication(jwtToken);
            // SecurityContext에 Authentication 객체를 저장합니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("토큰 인증 완료 및 저장");
        } else {
            log.info("토큰 인증 실패");
        }
        filterChain.doFilter(httpServletRequest, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        log.info("BEARER TOKEN: " + bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PERFIX)) {
            log.info("TOKEN RETURNED");
            return bearerToken.substring(7);
        }
        return null;
    }

}
