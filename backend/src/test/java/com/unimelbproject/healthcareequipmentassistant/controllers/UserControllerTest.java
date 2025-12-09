package com.unimelbproject.healthcareequipmentassistant.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimelbproject.healthcareequipmentassistant.dto.CreateUserRequest;
import com.unimelbproject.healthcareequipmentassistant.dto.UpdateUserRoleRequest;
import com.unimelbproject.healthcareequipmentassistant.dto.UserResponse;
import com.unimelbproject.healthcareequipmentassistant.models.Response;
import com.unimelbproject.healthcareequipmentassistant.models.User;
import com.unimelbproject.healthcareequipmentassistant.security.Permission;
import com.unimelbproject.healthcareequipmentassistant.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

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
    @DisplayName("Should get all users successfully")
    @WithMockUser(roles = "ADMIN", authorities = {"USER_READ"})
    void shouldGetAllUsersSuccessfully() throws Exception {
        // Arrange
        List<UserResponse> users = Arrays.asList(testUserResponse);
        Response<List<UserResponse>> response = Response.success("Successfully retrieved all users", users);
        when(userService.getAllUsers()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Successfully retrieved all users"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value("test-user-id"))
                .andExpect(jsonPath("$.data[0].username").value("testuser"))
                .andExpect(jsonPath("$.data[0].email").value("test@example.com"))
                .andExpect(jsonPath("$.data[0].role").value("partner"))
                .andExpect(jsonPath("$.data[0].level").value(1));
    }

    @Test
    @DisplayName("Should return 403 when user lacks permission")
    @WithMockUser(roles = "USER", authorities = {"WRONG_PERMISSION"})
    void shouldReturn403WhenNoPermission() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should create user successfully")
    @WithMockUser(roles = "ADMIN", authorities = {"USER_CREATE"})
    void shouldCreateUserSuccessfully() throws Exception {
        // Arrange
        Response<UserResponse> response = Response.success("User created successfully", testUserResponse);
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data.id").value("test-user-id"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    @DisplayName("Should return 400 when required fields are missing")
    @WithMockUser(roles = "ADMIN", authorities = {"USER_CREATE"})
    void shouldReturn400WhenMissingRequiredFields() throws Exception {
        // Arrange
        CreateUserRequest invalidRequest = new CreateUserRequest();
        // Do not set required fields

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update user role successfully")
    @WithMockUser(roles = "ADMIN", authorities = {"USER_UPDATE"})
    void shouldUpdateUserRoleSuccessfully() throws Exception {
        // Arrange
        String userId = "test-user-id";
        testUserResponse.setRole(User.UserRole.admin);
        testUserResponse.setLevel(0);
        Response<UserResponse> response = Response.success("User role updated successfully", testUserResponse);
        when(userService.updateUserRole(eq(userId), any(UpdateUserRoleRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/users/{id}/role", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRoleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User role updated successfully"))
                .andExpect(jsonPath("$.data.role").value("admin"))
                .andExpect(jsonPath("$.data.level").value(0));
    }

    @Test
    @DisplayName("Should handle user not found when updating role")
    @WithMockUser(roles = "ADMIN", authorities = {"USER_UPDATE"})
    void shouldHandleUserNotFoundWhenUpdatingRole() throws Exception {
        // Arrange
        String nonExistentUserId = "non-existent-id";
        Response<UserResponse> response = Response.failure("User does not exist");
        when(userService.updateUserRole(eq(nonExistentUserId), any(UpdateUserRoleRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/users/{id}/role", nonExistentUserId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRoleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User does not exist"));
    }

    @Test
    @DisplayName("Should delete user successfully")
    @WithMockUser(roles = "ADMIN", authorities = {"USER_DELETE"})
    void shouldDeleteUserSuccessfully() throws Exception {
        // Arrange
        String userId = "test-user-id";
        Response<String> response = Response.success("User deleted successfully", "User testuser has been deleted");
        when(userService.deleteUser(userId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(delete("/api/users/{id}", userId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User deleted successfully"))
                .andExpect(jsonPath("$.data").value("User testuser has been deleted"));
    }

    @Test
    @DisplayName("Should handle user not found when deleting")
    @WithMockUser(roles = "ADMIN", authorities = {"USER_DELETE"})
    void shouldHandleUserNotFoundWhenDeleting() throws Exception {
        // Arrange
        String nonExistentUserId = "non-existent-id";
        Response<String> response = Response.failure("User does not exist");
        when(userService.deleteUser(nonExistentUserId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(delete("/api/users/{id}", nonExistentUserId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User does not exist"));
    }

    @Test
    @DisplayName("Should prevent deleting admin user")
    @WithMockUser(roles = "ADMIN", authorities = {"USER_DELETE"})
    void shouldPreventDeletingAdminUser() throws Exception {
        // Arrange
        String adminUserId = "admin-user-id";
        Response<String> response = Response.failure("Cannot delete admin account");
        when(userService.deleteUser(adminUserId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(delete("/api/users/{id}", adminUserId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Cannot delete admin account"));
    }

    @Test
    @DisplayName("Should return 403 when lacking deletion permission")
    @WithMockUser(roles = "USER", authorities = {"WRONG_PERMISSION"})
    void shouldReturn403WhenNoDeletionPermission() throws Exception {
        mockMvc.perform(delete("/api/users/test-user-id")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}