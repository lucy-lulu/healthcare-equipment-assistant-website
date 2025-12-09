package com.unimelbproject.healthcareequipmentassistant.services;

import com.unimelbproject.healthcareequipmentassistant.dto.CreateUserRequest;
import com.unimelbproject.healthcareequipmentassistant.dto.UpdateUserRoleRequest;
import com.unimelbproject.healthcareequipmentassistant.dto.UserResponse;
import com.unimelbproject.healthcareequipmentassistant.models.Response;
import com.unimelbproject.healthcareequipmentassistant.models.User;
import com.unimelbproject.healthcareequipmentassistant.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private CreateUserRequest createUserRequest;
    private UpdateUserRoleRequest updateUserRoleRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("test-user-id");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("encoded-password");
        testUser.setRole(User.UserRole.partner);
        testUser.setLevel(1);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setShippingAddress("Test Address");
        testUser.setBillingAddress("Test Billing Address");

        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("newuser");
        createUserRequest.setEmail("newuser@example.com");
        createUserRequest.setPassword("password123");
        createUserRequest.setRole(User.UserRole.partner);
        createUserRequest.setLevel(2);
        createUserRequest.setShippingAddress("New Address");
        createUserRequest.setBillingAddress("New Billing Address");

        updateUserRoleRequest = new UpdateUserRoleRequest();
        updateUserRoleRequest.setRole(User.UserRole.admin);
    }

    @Test
    @DisplayName("Should get all users successfully")
    void shouldGetAllUsersSuccessfully() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userRepo.findAll()).thenReturn(users);

        // Act
        Response<List<UserResponse>> result = userService.getAllUsers();

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Successfully retrieved all users", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("testuser", result.getData().get(0).getUsername());
        assertEquals("test@example.com", result.getData().get(0).getEmail());
        verify(userRepo).findAll();
    }

    @Test
    @DisplayName("Should handle exception when getting all users")
    void shouldHandleExceptionWhenGettingAllUsers() {
        // Arrange
        when(userRepo.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act
        Response<List<UserResponse>> result = userService.getAllUsers();

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to retrieve user list"));
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Should create partner user successfully")
    void shouldCreatePartnerUserSuccessfully() {
        // Arrange
        when(userRepo.existsByUsername(anyString())).thenReturn(false);
        when(userRepo.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userRepo.save(any(User.class))).thenReturn(testUser);

        // Act
        Response<UserResponse> result = userService.createUser(createUserRequest);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("User created successfully", result.getMessage());
        assertNotNull(result.getData());
        assertEquals("testuser", result.getData().getUsername());
        verify(userRepo).existsByUsername("newuser");
        verify(userRepo).existsByEmail("newuser@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepo).save(any(User.class));
    }

    @Test
    @DisplayName("Should create staff user with level 0")
    void shouldCreateStaffUserWithLevel0() {
        // Arrange
        createUserRequest.setRole(User.UserRole.admin);
        createUserRequest.setLevel(5); // This should be ignored
        when(userRepo.existsByUsername(anyString())).thenReturn(false);
        when(userRepo.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        
        User savedUser = new User();
        savedUser.setId("test-user-id");
        savedUser.setUsername("newuser");
        savedUser.setEmail("newuser@example.com");
        savedUser.setRole(User.UserRole.admin);
        savedUser.setLevel(0); // Should be 0 for staff
        when(userRepo.save(any(User.class))).thenReturn(savedUser);

        // Act
        Response<UserResponse> result = userService.createUser(createUserRequest);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(0, result.getData().getLevel());
    }

    @Test
    @DisplayName("Should fail to create user when username exists")
    void shouldFailToCreateUserWhenUsernameExists() {
        // Arrange
        when(userRepo.existsByUsername(anyString())).thenReturn(true);

        // Act
        Response<UserResponse> result = userService.createUser(createUserRequest);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Username already exists", result.getMessage());
        assertNull(result.getData());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail to create user when email exists")
    void shouldFailToCreateUserWhenEmailExists() {
        // Arrange
        when(userRepo.existsByUsername(anyString())).thenReturn(false);
        when(userRepo.existsByEmail(anyString())).thenReturn(true);

        // Act
        Response<UserResponse> result = userService.createUser(createUserRequest);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Email already exists", result.getMessage());
        assertNull(result.getData());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail to create partner with invalid level")
    void shouldFailToCreatePartnerWithInvalidLevel() {
        // Arrange
        createUserRequest.setLevel(5); // Invalid level for partner
        when(userRepo.existsByUsername(anyString())).thenReturn(false);
        when(userRepo.existsByEmail(anyString())).thenReturn(false);

        // Act
        Response<UserResponse> result = userService.createUser(createUserRequest);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Invalid level for partner. Must be 1–4.", result.getMessage());
        assertNull(result.getData());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user role successfully")
    void shouldUpdateUserRoleSuccessfully() {
        // Arrange
        when(userRepo.findById(anyString())).thenReturn(Optional.of(testUser));
        when(userRepo.save(any(User.class))).thenReturn(testUser);

        // Act
        Response<UserResponse> result = userService.updateUserRole("test-user-id", updateUserRoleRequest);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("User role updated successfully", result.getMessage());
        assertNotNull(result.getData());
        verify(userRepo).findById("test-user-id");
        verify(userRepo).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail to update role when user not found")
    void shouldFailToUpdateRoleWhenUserNotFound() {
        // Arrange
        when(userRepo.findById(anyString())).thenReturn(Optional.empty());

        // Act
        Response<UserResponse> result = userService.updateUserRole("non-existent-id", updateUserRoleRequest);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("User does not exist", result.getMessage());
        assertNull(result.getData());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle exception when updating user role")
    void shouldHandleExceptionWhenUpdatingUserRole() {
        // Arrange
        when(userRepo.findById(anyString())).thenThrow(new RuntimeException("Database error"));

        // Act
        Response<UserResponse> result = userService.updateUserRole("test-user-id", updateUserRoleRequest);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to update user role"));
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Arrange
        when(userRepo.findById(anyString())).thenReturn(Optional.of(testUser));
        doNothing().when(userRepo).deleteById(anyString());

        // Act
        Response<String> result = userService.deleteUser("test-user-id");

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("User deleted successfully", result.getMessage());
        assertEquals("User testuser has been deleted", result.getData());
        verify(userRepo).findById("test-user-id");
        verify(userRepo).deleteById("test-user-id");
    }

    @Test
    @DisplayName("Should fail to delete user when user not found")
    void shouldFailToDeleteUserWhenUserNotFound() {
        // Arrange
        when(userRepo.findById(anyString())).thenReturn(Optional.empty());

        // Act
        Response<String> result = userService.deleteUser("non-existent-id");

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("User does not exist", result.getMessage());
        assertNull(result.getData());
        verify(userRepo, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Should prevent deleting admin user")
    void shouldPreventDeletingAdminUser() {
        // Arrange
        testUser.setRole(User.UserRole.admin);
        when(userRepo.findById(anyString())).thenReturn(Optional.of(testUser));

        // Act
        Response<String> result = userService.deleteUser("admin-user-id");

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Cannot delete admin account", result.getMessage());
        assertNull(result.getData());
        verify(userRepo, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Should handle exception when deleting user")
    void shouldHandleExceptionWhenDeletingUser() {
        // Arrange
        when(userRepo.findById(anyString())).thenThrow(new RuntimeException("Database error"));

        // Act
        Response<String> result = userService.deleteUser("test-user-id");

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to delete user"));
        assertNull(result.getData());
    }

    @Test
    @DisplayName("Should set default level 1 for partner when level not provided")
    void shouldSetDefaultLevel1ForPartnerWhenLevelNotProvided() {
        // Arrange
        createUserRequest.setLevel(null); // No level provided
        when(userRepo.existsByUsername(anyString())).thenReturn(false);
        when(userRepo.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        
        User savedUser = new User();
        savedUser.setId("test-user-id");
        savedUser.setUsername("newuser");
        savedUser.setEmail("newuser@example.com");
        savedUser.setRole(User.UserRole.partner);
        savedUser.setLevel(1); // Should default to 1
        when(userRepo.save(any(User.class))).thenReturn(savedUser);

        // Act
        Response<UserResponse> result = userService.createUser(createUserRequest);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().getLevel());
    }
}