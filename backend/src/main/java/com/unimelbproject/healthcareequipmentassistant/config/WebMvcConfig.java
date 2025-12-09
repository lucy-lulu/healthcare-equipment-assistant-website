package com.unimelbproject.healthcareequipmentassistant.config;

import com.unimelbproject.healthcareequipmentassistant.interceptors.ExceptionHandlerInterceptor;
import com.unimelbproject.healthcareequipmentassistant.interceptors.LoggingInterceptor;
import com.unimelbproject.healthcareequipmentassistant.interceptors.ResponseFormatInterceptor;
import com.unimelbproject.healthcareequipmentassistant.interceptors.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration
 * This class configures all interceptors and other web-related settings.
 * It implements WebMvcConfigurer to customize Spring MVC's default configuration.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoggingInterceptor loggingInterceptor;

    @Autowired
    private ResponseFormatInterceptor responseFormatInterceptor;

    @Autowired
    private ExceptionHandlerInterceptor exceptionHandlerInterceptor;

    @Autowired
    private PermissionInterceptor permissionInterceptor;

    /**
     * Configure and register all interceptors
     * The order of interceptor registration determines their execution order
     *
     * @param registry The interceptor registry to configure
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register logging interceptor for all paths except error pages and Swagger
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error", "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**", 
                                   "/api/swagger-ui/**", "/api/api-docs/**", "/api/v3/api-docs/**");

        // Register response format interceptor
        // Excludes authentication endpoints, Swagger endpoints, and error pages
        registry.addInterceptor(responseFormatInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error", "/api/auth/**", "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**",
                                   "/api/swagger-ui/**", "/api/api-docs/**", "/api/v3/api-docs/**");

        // Register exception handling interceptor
        // Applies to all paths except error pages and Swagger
        registry.addInterceptor(exceptionHandlerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error", "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**",
                                   "/api/swagger-ui/**", "/api/api-docs/**", "/api/v3/api-docs/**");

        // Register permission interceptor
        // Excludes authentication endpoints, test endpoints, Swagger endpoints, and error pages
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error", "/api/auth/**", "/api/test/**", "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**",
                                   "/api/swagger-ui/**", "/api/api-docs/**", "/api/v3/api-docs/**");
    }
} 