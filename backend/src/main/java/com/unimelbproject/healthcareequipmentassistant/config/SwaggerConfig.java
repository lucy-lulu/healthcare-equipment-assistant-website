package com.unimelbproject.healthcareequipmentassistant.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

        private static final String SECURITY_SCHEME_NAME = "bearerAuth";

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .servers(List.of(
                                        new Server().url("/").description("Current server"),
                                        new Server().url("http://localhost:8080").description("Local development server")
                                ))
                                .info(new Info()
                                                .title("Healthcare Equipment Assistant API")
                                                .description("API documentation for the Healthcare Equipment Assistant application. "
                                                                +
                                                                "To authenticate: 1) First call /api/auth/login to get a JWT token, "
                                                                +
                                                                "2) Copy the token from the response, " +
                                                                "3) Click the 'Authorize' button above and paste the token (without 'Bearer ' prefix)")
                                                .version("v1.0.0")
                                                .contact(new Contact()
                                                                .name("NOV Healthcare Team")
                                                                .email("healthcare@nov.com")
                                                                .url("https://github.com/NOV-healthcare-equipment-assistant"))
                                                .license(new License()
                                                                .name("MIT License")
                                                                .url("https://opensource.org/licenses/MIT")))
                                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                                .components(new Components()
                                                .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                                                .name(SECURITY_SCHEME_NAME)
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")
                                                                .description("JWT Authentication token. Enter the token obtained from /api/auth/login endpoint.")));
        }
}