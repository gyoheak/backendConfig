package com.example.demo.controller;

import com.example.demo.auth.TokenProvider;
import com.example.demo.dto.LoginUserDto;
import com.example.demo.dto.SaveUserDto;
import com.example.demo.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;

    @GetMapping("/temp-number")
    public void temporaryNumbers(@RequestParam String email) {
        userService.temporaryNumbers(email);
    }

    @Operation(description = "swagger test controller")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation =  String.class)))
    @PostMapping("/signIn")
    public void signIn(@RequestBody SaveUserDto saveUserDto){
        userService.save(saveUserDto);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginUserDto loginRequestDTO, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        Cookie accessToken = tokenProvider.createAccessToken(authentication);
        Cookie refreshToken = tokenProvider.createRefreshToken(authentication);

        response.addCookie(accessToken);
        response.addCookie(refreshToken);

        return "로그인!";
    }
}
