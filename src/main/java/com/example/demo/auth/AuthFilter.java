package com.example.demo.auth;

import com.example.demo.service.user.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
@RequiredArgsConstructor
public class AuthFilter extends GenericFilterBean {
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        try{
            String accessToken = getCookieValue(httpServletRequest, "accessToken");
            if(tokenProvider.validateToken(accessToken)){
                Authentication authentication = tokenProvider.getAuthenticationByAccessToken(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        catch(ExpiredJwtException e){
            String refreshToken = getCookieValue(httpServletRequest, "refreshToken");
            if (StringUtils.hasText(refreshToken)){
                Authentication authentication = tokenProvider.getAuthenticationByRefreshToken(refreshToken);
                if (redisTemplate.opsForValue().get(authentication.getName()).equals(refreshToken)){
                    UserDetails user = customUserDetailsService.loadUserByUsername(authentication.getName());
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    Cookie newAccessToken = tokenProvider.createAccessToken(authentication);
                    httpServletResponse.addCookie(newAccessToken);
                }
            }
        }
        finally {
            chain.doFilter(httpServletRequest,httpServletResponse);
        }
    }
    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
