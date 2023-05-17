package metaverse.backend.environment.security.jwt;

import io.jsonwebtoken.IncorrectClaimException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import metaverse.backend.dto.AuthDto;
import metaverse.backend.redis.RedisSecurityService;
import metaverse.backend.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    private final RedisSecurityService redisSecurityService;

    private final String SERVER = "Server";

    private final long COOKIE_EXPIRATION = 7776000; // 90일

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Access Token 추출
        String accessToken = resolveToken(request);
        String refreshToken = resolveRefreshToken(request);

        try { // 정상 토큰인지 검사
            if (accessToken != null && jwtTokenProvider.validateAccessToken(accessToken)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Save authentication in SecurityContextHolder.");
            } else if (accessToken != null && refreshToken != null && !jwtTokenProvider.validateAccessToken(accessToken) && jwtTokenProvider.validateRefreshToken(refreshToken)) { // AT 만료

                AuthDto.TokenDto reissuedTokenDto = reissue(accessToken, refreshToken);
                System.out.println("expirationTime" + jwtTokenProvider.getTokenExpirationTime(accessToken));

                if (reissuedTokenDto != null) { // 토큰 재발급 성공
//                    long expiration = jwtTokenProvider.getTokenExpirationTime(accessToken) - new Date().getTime();
//                    redisSecurityService.setValuesWithTimeout(accessToken,
//                            "expiration",
//                            expiration);
                    response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + reissuedTokenDto.getAccessToken());

                } else { // Refresh Token 탈취 가능성
                    // Cookie 삭제 후 재로그인 유도
                    ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                            .maxAge(0)
                            .path("/")
                            .build();

                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
                }
            }
        } catch (IncorrectClaimException e) { // 잘못된 토큰일 경우
            SecurityContextHolder.clearContext();
            log.debug("Invalid JWT token.");
            response.sendError(403);
        } catch (UsernameNotFoundException e) { // 회원을 찾을 수 없을 경우
            SecurityContextHolder.clearContext();
            log.debug("Can't find user.");
            response.sendError(403);
        }

        filterChain.doFilter(request, response);
    }

    // HTTP Request 헤더로부터 토큰 추출 Bearer
    public String resolveToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String resolveRefreshToken(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (cookie.getName().equals("refresh-token")) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    @Transactional
    public AuthDto.TokenDto reissue(String accessToken, String refreshToken) {
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String principal = getPrincipal(accessToken);

        String refreshTokenInRedis = redisSecurityService.getValues("RT(" + SERVER + "):" + principal);

        if (refreshTokenInRedis == null) { // Redis에 저장되어 있는 RT가 없을 경우
            return null; // -> 재로그인 요청
        }

        // 요청된 RT의 유효성 검사 & Redis에 저장되어 있는 RT와 같은지 비교
        if (!jwtTokenProvider.validateRefreshToken(refreshToken) || !refreshTokenInRedis.equals(refreshToken)) {
            redisSecurityService.deleteValues("RT(" + SERVER + "):" + principal); // 탈취 가능성 -> 삭제
            return null; // -> 재로그인 요청
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String authorities = getAuthorities(authentication);

        return jwtTokenProvider.createToken(principal, authorities);
    }

    private String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private String getPrincipal(String requestAccessToken) {
        return jwtTokenProvider.getAuthentication(requestAccessToken).getName();
    }
}
