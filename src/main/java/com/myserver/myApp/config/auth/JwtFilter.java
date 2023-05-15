package com.myserver.myApp.config.auth;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final static String AUTHORIZATION_HEADER = "Authorization";
    private final static String BEARER_PERFIX = "Bearer ";
    private final TokenProvider tokenProvider;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        log.info("--- FILTER RUNNING ---");

        String jwtToken = resolveToken(request);
        log.info("TOKEN: " + jwtToken);

        if (StringUtils.hasText(jwtToken) && tokenProvider.validateToken(jwtToken)) {
            Authentication authentication = tokenProvider.getAuthentication(jwtToken);
            // SecurityContext에 Authentication 객체를 저장합니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("토큰 인증 완료 및 저장");
        } else {
            log.info("토큰 인증 실패");
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        log.info("--- RESOLVE TOKEN ---");
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        log.info(request.getHeader(AUTHORIZATION_HEADER));
        log.info("BEARER TOKEN: " + bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PERFIX)) {
            log.info("TOKEN RETURNED");
            return bearerToken.substring(7);
        }
        return null;
    }

}
