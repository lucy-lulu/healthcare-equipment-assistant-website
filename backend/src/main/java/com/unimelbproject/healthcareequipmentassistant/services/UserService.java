package com.unimelbproject.healthcareequipmentassistant.services;

import com.unimelbproject.healthcareequipmentassistant.dto.*;
import com.unimelbproject.healthcareequipmentassistant.models.Response;
import com.unimelbproject.healthcareequipmentassistant.models.User;
import com.unimelbproject.healthcareequipmentassistant.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Response<List<UserResponse>> getAllUsers() {
        try {
            List<User> users = userRepo.findAll();
            List<UserResponse> userResponses = users.stream()
                    .map(UserResponse::new)
                    .collect(Collectors.toList());
            return Response.success("Successfully retrieved all users", userResponses);
        } catch (Exception e) {
            return Response.failure("Failed to retrieve user list: " + e.getMessage());
        }
    }

    public Response<UserResponse> createUser(CreateUserRequest request) {
        try {
            // 检查用户名是否已存在
            if (userRepo.existsByUsername(request.getUsername())) {
                return Response.failure("Username already exists");
            }

            // 检查邮箱是否已存在
            if (userRepo.existsByEmail(request.getEmail())) {
                return Response.failure("Email already exists");
            }

            // 创建新用户
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole());
            user.setCreatedAt(LocalDateTime.now());
            user.setShippingAddress(request.getShippingAddress());
            user.setBillingAddress(request.getBillingAddress());

            //user level setting
            Integer requestedLevel = request.getLevel();
            if (user.getRole() == User.UserRole.partner) {
                //partners must have level 1–4; default to 1 if not provided
                int level = (requestedLevel == null) ? 1 : requestedLevel;
                if (level < 1 || level > 4) {
                    return Response.failure("Invalid level for partner. Must be 1–4.");
                }
                user.setLevel(level);
            } else {
                // NOVIS staff always level 0, ignore any provided value
                user.setLevel(0);
            }

            User savedUser = userRepo.save(user);
            return Response.success("User created successfully", new UserResponse(savedUser));

        } catch (Exception e) {
            return Response.failure("Failed to create user: " + e.getMessage());
        }
    }

    public Response<UserResponse> updateUserRole(String userId, UpdateUserRoleRequest request) {
        try {
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isEmpty()) {
                return Response.failure("User does not exist");
            }

            User user = userOptional.get();
            user.setRole(request.getRole());
            User updatedUser = userRepo.save(user);

            if (user.getRole() == User.UserRole.partner) {
                // If switching to partner, keep existing level if valid, else default to 1
                Integer currentLevel = user.getLevel();
                if (currentLevel == null || currentLevel < 1 || currentLevel > 4) {
                    user.setLevel(1);
                }
            } else {
                // If switching away from partner, force level 0
                user.setLevel(0);
            }
            
            return Response.success("User role updated successfully", new UserResponse(updatedUser));

        } catch (Exception e) {
            return Response.failure("Failed to update user role: " + e.getMessage());
        }
    }

    public Response<String> deleteUser(String userId) {
        try {
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isEmpty()) {
                return Response.failure("User does not exist");
            }

            User user = userOptional.get();
            
            // 不允许删除管理员账户
            if (user.getRole() == User.UserRole.admin) {
                return Response.failure("Cannot delete admin account");
            }

            userRepo.deleteById(userId);
            return Response.success("User deleted successfully", "User " + user.getUsername() + " has been deleted");

        } catch (Exception e) {
            return Response.failure("Failed to delete user: " + e.getMessage());
        }
    }

    public Response<UserResponse> updateUserLevelByEmail(UpdateUserLevelRequest request) {
        try {
            if (request.getLevel() == null) {
                return Response.failure("Level must not be null");
            }
            return (Response<UserResponse>) userRepo.findByEmail(request.getEmail())
                    .map(user -> {
                        Integer level = request.getLevel();

                        if (user.getRole() == User.UserRole.partner) {
                            // Partners must be 1–4
                            if (level < 1 || level > 4) {
                                return Response.failure("Invalid level for partner. Must be 1–4.");
                            }
                            user.setLevel(level);
                        } else {
                            // Non-partners (staff/sales/ot/admin) must be 0
                            if (level != 0) {
                                return Response.failure("Non-partner roles must have level 0.");
                            }
                            user.setLevel(0);
                        }

                        User saved = userRepo.save(user);
                        return Response.success("User level updated successfully", new UserResponse(saved));
                    })
                    .orElseGet(() -> Response.failure("User with given email does not exist"));
        } catch (Exception e) {
            return Response.failure("Failed to update user level: " + e.getMessage());
        }
    }

    public Response<UserResponse> manageUserLevel(ManageUserLevelRequest request) {
        try {
            return userRepo.findByEmail(request.getEmail())
                    .map(user -> {
                        user.setLevel(request.getLevel());
                        User saved = userRepo.save(user);
                        return Response.success("User level forcibly updated", new UserResponse(saved));
                    })
                    .orElseGet(() -> Response.failure("User with given email does not exist"));
        } catch (Exception e) {
            return Response.failure("Failed to manage user level: " + e.getMessage());
        }
    }

}