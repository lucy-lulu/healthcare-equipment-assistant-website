package com.unimelbproject.healthcareequipmentassistant.controllers;

import com.unimelbproject.healthcareequipmentassistant.annotations.RequirePermission;
import com.unimelbproject.healthcareequipmentassistant.dto.*;
import com.unimelbproject.healthcareequipmentassistant.models.Response;
import com.unimelbproject.healthcareequipmentassistant.security.Permission;
import com.unimelbproject.healthcareequipmentassistant.services.UserService;
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

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Management", description = "User Management API - Admin access required")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(
        summary = "Get all users", 
        description = "Retrieve a list of all users in the system. Only administrators can access this endpoint."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved user list", 
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Response.class),
                examples = @ExampleObject(
                    name = "Successful user list retrieval",
                    value = """
                    {
                      "success": true,
                      "message": "Successfully retrieved all users",
                      "data": [
                        {
                          "id": "123e4567-e89b-12d3-a456-426614174000",
                          "username": "testuser",
                          "email": "test@example.com",
                          "role": "partner",
                          "createdAt": "2024-01-01T12:00:00",
                          "shippingAddress": "Test Address",
                          "billingAddress": "Test Billing Address",
                          "level": 1
                        }
                      ]
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Insufficient permissions - Admin access required"
        )
    })
    @RequirePermission(Permission.USER_READ)
    @GetMapping
    public ResponseEntity<Response<List<UserResponse>>> getAllUsers() {
        Response<List<UserResponse>> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Create new user", 
        description = "Create a new user account. Only administrators can create new users."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User created successfully", 
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Response.class),
                examples = @ExampleObject(
                    name = "User created successfully",
                    value = """
                    {
                      "success": true,
                      "message": "User created successfully",
                      "data": {
                        "id": "123e4567-e89b-12d3-a456-426614174000",
                        "username": "newuser",
                        "email": "newuser@example.com",
                        "role": "partner",
                        "createdAt": "2024-01-01T12:00:00",
                        "shippingAddress": null,
                        "billingAddress": null,
                        "level": 1
                      }
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request parameters or user already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Username already exists",
                    value = """
                    {
                      "success": false,
                      "message": "Username already exists",
                      "data": null
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Insufficient permissions - Admin access required"
        )
    })
    @RequirePermission(Permission.USER_CREATE)
    @PostMapping
    public ResponseEntity<Response<UserResponse>> createUser(
        @Parameter(description = "New user information", required = true) 
        @Valid @RequestBody CreateUserRequest request
    ) {
        Response<UserResponse> response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Update user role", 
        description = "Update the role of a specified user. Only administrators can modify user roles."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User role updated successfully", 
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Response.class),
                examples = @ExampleObject(
                    name = "User role updated successfully",
                    value = """
                    {
                      "success": true,
                      "message": "User role updated successfully",
                      "data": {
                        "id": "123e4567-e89b-12d3-a456-426614174000",
                        "username": "testuser",
                        "email": "test@example.com",
                        "role": "admin",
                        "createdAt": "2024-01-01T12:00:00",
                        "shippingAddress": "Test Address",
                        "billingAddress": "Test Billing Address",
                        "level": 0
                      }
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User does not exist",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "User does not exist",
                    value = """
                    {
                      "success": false,
                      "message": "User does not exist",
                      "data": null
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Insufficient permissions - Admin access required"
        )
    })
    @RequirePermission(Permission.USER_UPDATE)
    @PutMapping("/{id}/role")
    public ResponseEntity<Response<UserResponse>> updateUserRole(
        @Parameter(description = "User ID", required = true) 
        @PathVariable String id,
        @Parameter(description = "New role information", required = true) 
        @Valid @RequestBody UpdateUserRoleRequest request
    ) {
        Response<UserResponse> response = userService.updateUserRole(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Delete user", 
        description = "Delete a specified user account. Only administrators can delete users, and admin accounts cannot be deleted."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User deleted successfully", 
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Response.class),
                examples = @ExampleObject(
                    name = "User deleted successfully",
                    value = """
                    {
                      "success": true,
                      "message": "User deleted successfully",
                      "data": "User testuser has been deleted"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Cannot delete admin account",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Cannot delete admin",
                    value = """
                    {
                      "success": false,
                      "message": "Cannot delete admin account",
                      "data": null
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User does not exist"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Insufficient permissions - Admin access required"
        )
    })
    @RequirePermission(Permission.USER_DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> deleteUser(
        @Parameter(description = "User ID", required = true) 
        @PathVariable String id
    ) {
        Response<String> response = userService.deleteUser(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update user level by email",
            description = """
        Manually set a user's level.
        • Partner: level must be 1–4
        • Staff/Sales/OT/Admin: level must be 0
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User level updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(
                                    name = "Success",
                                    value = """
                {
                  "success": true,
                  "message": "User level updated successfully",
                  "data": {
                    "id": "123e4567-e89b-12d3-a456-426614174000",
                    "username": "partner_jane",
                    "email": "partner@example.com",
                    "role": "partner",
                    "createdAt": "2024-01-01T12:00:00",
                    "shippingAddress": null,
                    "billingAddress": null,
                    "level": 2
                  }
                }
                """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request or level"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions - Admin access required")
    })
    @RequirePermission(Permission.USER_UPDATE)
    @PostMapping("/level")
    public ResponseEntity<Response<UserResponse>> updateUserLevelByEmail(
            @Valid @RequestBody UpdateUserLevelRequest request) {
        Response<UserResponse> resp = userService.updateUserLevelByEmail(request);
        // Map common error messages to appropriate HTTP codes (optional)
        if (!resp.isSuccess()) {
            String msg = resp.getMessage() != null ? resp.getMessage().toLowerCase() : "";
            if (msg.contains("does not exist") || msg.contains("not exist")) {
                return ResponseEntity.status(404).body(resp);
            }
            if (msg.contains("invalid level") || msg.contains("must")) {
                return ResponseEntity.badRequest().body(resp);
            }
        }
        return ResponseEntity.ok(resp);
    }

    @Operation(
            summary = "Force update user level",
            description = "Utility endpoint to manually update a user's level without role or token restrictions."
    )
    @PostMapping("/managelevel")
    public ResponseEntity<Response<UserResponse>> manageUserLevel(
            @Valid @RequestBody ManageUserLevelRequest request) {
        Response<UserResponse> resp = userService.manageUserLevel(request);
        if (!resp.isSuccess()) {
            return ResponseEntity.badRequest().body(resp);
        }
        return ResponseEntity.ok(resp);
    }


}