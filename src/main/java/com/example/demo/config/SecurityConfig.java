package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("kmg")
                .password("tmax1234")
                .roles("ADMIN")
                .build();
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("mgko")
                .password("1234")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authz) -> authz
            .requestMatchers("/api-docs", "/swagger-ui/**").hasRole("ADMIN")
            .anyRequest().permitAll()
        );
        http.formLogin().usernameParameter("username").passwordParameter("password")
                        .successHandler((request, response, authentication)->{
                            response.sendRedirect("/api-docs");
                        });
        http.logout()
                .logoutUrl("/api-docs/logout")
                .logoutSuccessUrl("/api-docs")
                .deleteCookies("JSESSIONID");
        return http.build();
    }
}
