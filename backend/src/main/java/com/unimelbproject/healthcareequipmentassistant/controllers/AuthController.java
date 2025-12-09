package com.unimelbproject.healthcareequipmentassistant.controllers;

import com.unimelbproject.healthcareequipmentassistant.dto.JwtResponse;
import com.unimelbproject.healthcareequipmentassistant.dto.LoginRequest;
import com.unimelbproject.healthcareequipmentassistant.models.Response;
import com.unimelbproject.healthcareequipmentassistant.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication API for user login and logout")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "User Login", description = "Authenticate user with username and password, returns JWT token", security = {} // This endpoint doesn't require authentication
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class), examples = @ExampleObject(name = "Successful Login", value = """
                    {
                      "success": true,
                      "message": "Successfully login",
                      "data": {
                        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                        "type": "Bearer",
                        "id": "1",
                        "username": "testuser",
                        "email": "test@example.com",
                        "role": "USER"
                      }
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Login Failed", value = """
                    {
                      "success": false,
                      "message": "Invalid username or password",
                      "data": null
                    }
                    """)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
            @Parameter(description = "User login credentials", required = true) @Valid @RequestBody LoginRequest loginRequest) {
        // Log the login request for debugging
        System.out.println("Login request received in controller: " + loginRequest);
        Response jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @Operation(summary = "User Logout", description = "Logout current user and clear security context. Note: JWT tokens are stateless, actual logout is handled by frontend clearing stored token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class), examples = @ExampleObject(name = "Successful Logout", value = """
                    {
                      "success": true,
                      "message": "Successfully logged out",
                      "data": null
                    }
                    """)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        Response response = authService.logoutUser();
        return ResponseEntity.ok(response);
    }
}
