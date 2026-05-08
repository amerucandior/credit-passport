package com.passport.creditpassport.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI OpenAPI() {
        final String bearerSecuritySchemeName = "bearerAuth";
        final String apiKeySecuritySchemeName = "apiKeyAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Credit-Passport API")
                        .description("API documentation for Credit-Passport System")
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes(bearerSecuritySchemeName,
                                new SecurityScheme()
                                        .name(bearerSecuritySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                        .addSecuritySchemes(apiKeySecuritySchemeName,
                                new SecurityScheme()
                                        .name("X-API-KEY")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                        )
                );
    }
}