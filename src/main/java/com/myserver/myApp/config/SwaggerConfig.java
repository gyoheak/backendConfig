package com.myserver.myApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
    private String API_VERSION = "0.0.1";
    private String API_NAME = "Mint API";
    private String API_DESCRIPTION = "Mint API";

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info().title(API_NAME).version(API_VERSION).description(API_DESCRIPTION);
        return new OpenAPI().components(new Components()).info(info);

    }
}