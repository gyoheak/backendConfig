package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("v1-definition")
                .pathsToMatch("/test/**")
                .build();
    }
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Test Swagger")
                        .description("임시 Swagger")
                        .version("v0.0.1"));
    }
}
