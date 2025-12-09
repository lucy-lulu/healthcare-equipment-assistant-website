package com.unimelbproject.healthcareequipmentassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HealthcareEquipmentAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthcareEquipmentAssistantApplication.class, args);
    }

}
