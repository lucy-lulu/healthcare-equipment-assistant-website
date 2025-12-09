package com.unimelbproject.healthcareequipmentassistant.config;

import com.unimelbproject.healthcareequipmentassistant.filters.ResponseWrapperFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ResponseWrapperFilter> responseWrapperFilterRegistration(ResponseWrapperFilter filter) {
        FilterRegistrationBean<ResponseWrapperFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
} 