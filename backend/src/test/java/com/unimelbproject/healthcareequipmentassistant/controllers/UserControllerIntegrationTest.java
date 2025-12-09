package com.unimelbproject.healthcareequipmentassistant.controllers;

import com.unimelbproject.healthcareequipmentassistant.dto.CreateUserRequest;
import com.unimelbproject.healthcareequipmentassistant.dto.UpdateUserRoleRequest;
import com.unimelbproject.healthcareequipmentassistant.dto.UserResponse;
import com.unimelbproject.healthcareequipmentassistant.models.Response;
import com.unimelbproject.healthcareequipmentassistant.models.User;
import com.unimelbproject.healthcareequipmentassistant.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerIntegrationTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponse testUserResponse;
    private CreateUserRequest createUserRequest;
    private UpdateUserRoleRequest updateUserRoleRequest;

    @BeforeEach
    void setUp() {
        testUserResponse = new UserResponse();
        testUserResponse.setId("test-user-id");
        testUserResponse.setUsername("testuser");
        testUserResponse.setEmail("test@example.com");
        testUserResponse.setRole(User.UserRole.partner);
        testUserResponse.setLevel(1);

        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("newuser");
        createUserRequest.setEmail("newuser@example.com");
        createUserRequest.setPassword("password123");
        createUserRequest.setRole(User.UserRole.partner);
        createUserRequest.setLevel(1);

        updateUserRoleRequest = new UpdateUserRoleRequest();
        updateUserRoleRequest.setRole(User.UserRole.admin);
    }

    @Test
    @DisplayName("Controller should get all users successfully")
    void shouldGetAllUsersSuccessfully() {
        // Arrange
        List<UserResponse> users = Arrays.asList(testUserResponse);
        Response<List<UserResponse>> serviceResponse = Response.success("Successfully retrieved all users", users);
        when(userService.getAllUsers()).thenReturn(serviceResponse);

        // Act
        ResponseEntity<Response<List<UserResponse>>> result = userController.getAllUsers();

        // Assert
        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isSuccess());
        assertEquals("Successfully retrieved all users", result.getBody().getMessage());
        assertEquals(1, result.getBody().getData().size());
        assertEquals("testuser", result.getBody().getData().get(0).getUsername());
    }

    @Test
    @DisplayName("Controller should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Arrange
        Response<UserResponse> serviceResponse = Response.success("User created successfully", testUserResponse);
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(serviceResponse);

        // Act
        ResponseEntity<Response<UserResponse>> result = userController.createUser(createUserRequest);

        // Assert
        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isSuccess());
        assertEquals("User created successfully", result.getBody().getMessage());
        assertEquals("testuser", result.getBody().getData().getUsername());
    }

    @Test
    @DisplayName("Controller should update user role successfully")
    void shouldUpdateUserRoleSuccessfully() {
        // Arrange
        testUserResponse.setRole(User.UserRole.admin);
        testUserResponse.setLevel(0);
        Response<UserResponse> serviceResponse = Response.success("User role updated successfully", testUserResponse);
        when(userService.updateUserRole(eq("test-user-id"), any(UpdateUserRoleRequest.class))).thenReturn(serviceResponse);

        // Act
        ResponseEntity<Response<UserResponse>> result = userController.updateUserRole("test-user-id", updateUserRoleRequest);

        // Assert
        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isSuccess());
        assertEquals("User role updated successfully", result.getBody().getMessage());
        assertEquals(User.UserRole.admin, result.getBody().getData().getRole());
        assertEquals(0, result.getBody().getData().getLevel());
    }

    @Test
    @DisplayName("Controller should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Arrange
        Response<String> serviceResponse = Response.success("User deleted successfully", "User testuser has been deleted");
        when(userService.deleteUser("test-user-id")).thenReturn(serviceResponse);

        // Act
        ResponseEntity<Response<String>> result = userController.deleteUser("test-user-id");

        // Assert
        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isSuccess());
        assertEquals("User deleted successfully", result.getBody().getMessage());
        assertEquals("User testuser has been deleted", result.getBody().getData());
    }

    @Test
    @DisplayName("Controller should handle service failures gracefully")
    void shouldHandleServiceFailures() {
        // Arrange
        Response<List<UserResponse>> failureResponse = Response.failure("Service error occurred");
        when(userService.getAllUsers()).thenReturn(failureResponse);

        // Act
        ResponseEntity<Response<List<UserResponse>>> result = userController.getAllUsers();

        // Assert
        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().isSuccess());
        assertEquals("Service error occurred", result.getBody().getMessage());
        assertNull(result.getBody().getData());
    }

    @Test
    @DisplayName("Controller should handle user not found when updating role")
    void shouldHandleUserNotFoundWhenUpdatingRole() {
        // Arrange
        Response<UserResponse> notFoundResponse = Response.failure("User does not exist");
        when(userService.updateUserRole(eq("non-existent-id"), any(UpdateUserRoleRequest.class))).thenReturn(notFoundResponse);

        // Act
        ResponseEntity<Response<UserResponse>> result = userController.updateUserRole("non-existent-id", updateUserRoleRequest);

        // Assert
        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().isSuccess());
        assertEquals("User does not exist", result.getBody().getMessage());
        assertNull(result.getBody().getData());
    }

    @Test
    @DisplayName("Controller should handle user not found when deleting")
    void shouldHandleUserNotFoundWhenDeleting() {
        // Arrange
        Response<String> notFoundResponse = Response.failure("User does not exist");
        when(userService.deleteUser("non-existent-id")).thenReturn(notFoundResponse);

        // Act
        ResponseEntity<Response<String>> result = userController.deleteUser("non-existent-id");

        // Assert
        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().isSuccess());
        assertEquals("User does not exist", result.getBody().getMessage());
        assertNull(result.getBody().getData());
    }

    @Test
    @DisplayName("Controller should prevent deleting admin user")
    void shouldPreventDeletingAdminUser() {
        // Arrange
        Response<String> adminDeletionResponse = Response.failure("Cannot delete admin account");
        when(userService.deleteUser("admin-user-id")).thenReturn(adminDeletionResponse);

        // Act
        ResponseEntity<Response<String>> result = userController.deleteUser("admin-user-id");

        // Assert
        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().isSuccess());
        assertEquals("Cannot delete admin account", result.getBody().getMessage());
        assertNull(result.getBody().getData());
    }
}