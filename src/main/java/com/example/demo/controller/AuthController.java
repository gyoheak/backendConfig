package com.example.demo.controller;

import com.example.demo.auth.TokenProvider;
import com.example.demo.dto.LoginUserDto;
import com.example.demo.dto.SaveUserDto;
import com.example.demo.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
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
    private final RedisTemplate<String, Object> redisTemplate;

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
    public HashMap<String,Object> login(@RequestBody LoginUserDto loginRequestDTO) {
        HashMap<String, Object> result = new HashMap<>();
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        HashMap<String,Object> accessTokenInfo = tokenProvider.createAccessToken(authentication);
        HashMap<String,Object> refreshTokenInfo = tokenProvider.createRefreshToken(authentication);

        result.put("accessTokenInfo",accessTokenInfo);
        result.put("refreshTokenInfo", refreshTokenInfo);

        redisTemplate.opsForValue().set(authentication.getName(), refreshTokenInfo.get("jwt"),((Date) refreshTokenInfo.get("expiration(ms)")).getTime()-System.currentTimeMillis(), TimeUnit.MILLISECONDS);

        return result;
    }

    @PostMapping("/refresh")
    public HashMap<String,Object> refresh( HttpServletRequest request){
        HashMap<String, Object> result = new HashMap<>();
        String refreshToken = request.getHeader("refreshToken");
        Authentication authentication = tokenProvider.getAuthenticationByRefreshToken(refreshToken);
        if (refreshToken.equals(redisTemplate.opsForValue().get(authentication.getName()))){
            HashMap<String,Object> newAccessTokenInfo = tokenProvider.createAccessToken(authentication);
            result.putAll(newAccessTokenInfo);
        }
        return result;
    }
}
