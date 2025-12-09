package com.unimelbproject.healthcareequipmentassistant.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimelbproject.healthcareequipmentassistant.models.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class ResponseFormatInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (!(response instanceof ContentCachingResponseWrapper)) {
            return;
        }

        ContentCachingResponseWrapper responseWrapper = (ContentCachingResponseWrapper) response;
        if (response.getContentType() == null || !response.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
            return;
        }

        try {
            // 获取原始响应内容
            byte[] responseBody = responseWrapper.getContentAsByteArray();
            if (responseBody.length == 0) {
                return;
            }

            String responseBodyStr = new String(responseBody, responseWrapper.getCharacterEncoding());
            
            // 如果响应已经是Response格式，则不需要再次包装
            if (responseBodyStr.contains("\"success\":") && responseBodyStr.contains("\"message\":")) {
                return;
            }

            // 包装响应
            Response<?> wrappedResponse = Response.success("Success", objectMapper.readValue(responseBodyStr, Object.class));
            String wrappedResponseBody = objectMapper.writeValueAsString(wrappedResponse);

            // 设置新的响应内容
            responseWrapper.resetBuffer();
            responseWrapper.getWriter().write(wrappedResponseBody);
        } catch (Exception e) {
            // 如果处理过程中出现异常，记录日志但不影响原始响应
            e.printStackTrace();
        }
    }
} 