package com.unimelbproject.healthcareequipmentassistant.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimelbproject.healthcareequipmentassistant.models.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ExceptionHandlerInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerInterceptor.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex == null) {
            return;
        }

        try {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            
            Response<?> errorResponse;
            if (ex instanceof IllegalArgumentException) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                errorResponse = Response.failure(ex.getMessage());
            } else if (ex instanceof SecurityException) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                errorResponse = Response.failure("Access denied: " + ex.getMessage());
            } else {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                errorResponse = Response.failure("Internal server error: " + ex.getMessage());
                logger.error("Unhandled exception", ex);
            }

            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(jsonResponse);
        } catch (Exception e) {
            logger.error("Error while handling exception", e);
        }
    }
} 