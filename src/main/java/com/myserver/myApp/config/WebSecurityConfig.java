package com.myserver.myApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.myserver.myApp.config.auth.JwtAccessDeniedHandler;

import com.myserver.myApp.config.auth.JwtAuthenticationEntryPoint;
import com.myserver.myApp.config.auth.JwtSecurityConfig;
import com.myserver.myApp.config.auth.TokenProvider;

import lombok.RequiredArgsConstructor;

/**
 * WebSecurityConfig
 */
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            web.ignoring().requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/articles/**",
                    "article/**");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                // 만들어 둔 exception 처리 클래스 등록
                .exceptionHandling(handling -> handling.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .headers(headers -> headers.frameOptions().sameOrigin())
                // 세션 미사용
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests().requestMatchers("/**").permitAll()
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));

        return http.build();
    }
}