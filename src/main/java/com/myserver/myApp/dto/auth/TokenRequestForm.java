package com.myserver.myApp.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequestForm {
    private String accessToken;
    private String refreshToken;
}
