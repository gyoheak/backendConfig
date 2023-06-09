package com.example.demo.config.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class TokenProvider {
    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    private final RedisTemplate<String, Object> redisTemplate;


    @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInMilliseconds;
    @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInMilliseconds;

    public TokenProvider(
            @Value("${jwt.access-key}") String accessKey,
            @Value("${jwt.refresh-key}") String refreshKey,
            RedisTemplate<String, Object> redisTemplate
    ){
        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
        this.redisTemplate = redisTemplate;
    }

    public Cookie createAccessToken(Authentication authentication){
        return createToken(authentication, this.accessKey, this.accessTokenValidityInMilliseconds, "accessToken");
    }

    public Cookie createRefreshToken(Authentication authentication){
        return createToken(authentication, this.refreshKey, this.refreshTokenValidityInMilliseconds, "refreshToken");
    }

    private Cookie createToken(Authentication authentication, SecretKey secretKey, long  tokenValidityInMilliSeconds, String cookieName){
        HashMap<String, Object> result = new HashMap<>();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        Date validity = new Date(now + tokenValidityInMilliSeconds);

        String jwt =  Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth",authorities)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();

        int validityInSeconds = (int)tokenValidityInMilliSeconds/1000;

        Cookie tokenCookie = new Cookie(cookieName, jwt);
        tokenCookie.setMaxAge(validityInSeconds);
        tokenCookie.setPath("/");

        if (cookieName.equals("refreshToken")){
            redisTemplate.opsForValue().set(authentication.getName(), jwt,tokenValidityInMilliSeconds, TimeUnit.MILLISECONDS);
        }

        return tokenCookie;
    }

    public Authentication getAuthenticationByAccessToken(String token){
        return getAuthentication(token, accessKey);
    }

    public Authentication getAuthenticationByRefreshToken(String token){
        return getAuthentication(token, refreshKey);
    }

    private Authentication getAuthentication(String token, SecretKey secretKey){
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token){
        try{
            if (token == null){
                throw new ExpiredJwtException(null, null, "Expired or invalid JWT token");
            }
            Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(token);
            return true;
        }
        catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            System.out.println("error = " + e);
        }
        catch ( UnsupportedJwtException | IllegalArgumentException e) { System.out.println("error = " + e); }
        catch ( ExpiredJwtException e){
            throw new ExpiredJwtException(null, null, "Expired or invalid JWT token");
        }
        return false;
    }

}
