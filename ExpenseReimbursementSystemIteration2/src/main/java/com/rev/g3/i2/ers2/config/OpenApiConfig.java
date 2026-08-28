package com.rev.g3.i2.ers2.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

// Applies bearer JWT security to the API documentation
@OpenAPIDefinition(security = @SecurityRequirement(name = "bearerAuth")
)

// Defines how Swagger/OpenAPI should send the JWT token
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)

// Tells Spring this class contains configuration
@Configuration
public class OpenApiConfig {
}
