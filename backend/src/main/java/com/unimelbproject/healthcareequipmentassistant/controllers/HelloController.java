package com.unimelbproject.healthcareequipmentassistant.controllers;

import com.unimelbproject.healthcareequipmentassistant.models.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.unimelbproject.healthcareequipmentassistant.services.HelloService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/hello")
@Tag(name = "Hello API", description = "Simple test endpoint for API connectivity")
public class HelloController {

    private final HelloService helloService;
    @Autowired
    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @Operation(
        summary = "Get hello message", 
        description = "Returns a simple hello message for API connectivity testing"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved hello message", 
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Response.class),
                examples = @ExampleObject(
                    name = "Successful hello response",
                    value = """
                    {
                      "success": true,
                      "message": "Hello World!",
                      "data": "API is working correctly"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Response.class)
            )
        )
    })
    @GetMapping
    public ResponseEntity<Response<?>> getHelloMessage() {
        Response<?> response = helloService.getHelloMessage();
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(500).body(response);
        }
    }


}
