package com.unimelbproject.healthcareequipmentassistant.services;

import com.unimelbproject.healthcareequipmentassistant.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.unimelbproject.healthcareequipmentassistant.repositories.HelloRepo;

@Service
public class HelloService {

    private final HelloRepo helloRepo;

    @Autowired
    public HelloService(HelloRepo helloRepo) {
        this.helloRepo = helloRepo;
    }

    public Response<String> getHelloMessage() {
        String message = helloRepo.getHelloMessage();
        if (message == null || message.isEmpty()) {
            return Response.failure("Failed to retrieve message");
        }
        return Response.success("Successfully retrieved message", message);
    }
}
