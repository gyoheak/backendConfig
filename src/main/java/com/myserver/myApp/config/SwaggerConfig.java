package com.myserver.myApp.config;


import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "Mint API", version = "0.0.1", description = "Mint API"))
@Configuration
public class SwaggerConfig {
    private String API_VERSION = "0.0.1";
    private String API_NAME = "Mint API";
    private String API_DESCRIPTION = "Mint API";

    @Bean
    public GroupedOpenApi publicApi() {
        String[] paths = {"/api/**"};
        return GroupedOpenApi.builder()
                .group("public-api").pathsToMatch(paths).build();
    }
}