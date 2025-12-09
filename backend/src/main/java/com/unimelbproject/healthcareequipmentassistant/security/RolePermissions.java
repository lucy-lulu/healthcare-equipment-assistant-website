package com.unimelbproject.healthcareequipmentassistant.security;

import com.unimelbproject.healthcareequipmentassistant.models.User;
import java.util.*;

/**
 * Role Permissions Manager
 * This class manages the mapping between user roles and their associated permissions.
 * It provides a centralized way to check if a role has a specific permission.
 * 
 * Role-based access control (RBAC) is implemented through this class, where:
 * - Each role has a predefined set of permissions
 * - Permissions are checked at runtime through the PermissionInterceptor
 * - Role-permission mappings are defined statically for security
 */
public class RolePermissions {
    /**
     * Map storing role-permission relationships
     * Key: UserRole enum
     * Value: Set of permissions associated with the role
     * 
     * This map is initialized statically to ensure thread safety and
     * prevent runtime modifications to permission mappings.
     */
    private static final Map<User.UserRole, Set<Permission>> ROLE_PERMISSIONS = new EnumMap<>(User.UserRole.class);

    // Initialize role-permission mappings
    static {
        // Admin role has all available permissions
        // Admins can perform any action in the system
        Set<Permission> adminPermissions = new HashSet<>(Arrays.asList(Permission.values()));
        ROLE_PERMISSIONS.put(User.UserRole.admin, adminPermissions);

        // Partner role permissions
        // Partners can:
        // - View equipment information
        // - Create and manage orders
        // - View reports
        // But cannot modify equipment or system settings
        Set<Permission> partnerPermissions = new HashSet<>(Arrays.asList(
            Permission.EQUIPMENT_READ,
            Permission.ORDER_CREATE,
            Permission.ORDER_READ,
            Permission.ORDER_UPDATE,
            Permission.REPORT_VIEW
        ));
        ROLE_PERMISSIONS.put(User.UserRole.partner, partnerPermissions);

        // Sales role permissions
        // Sales staff can:
        // - View equipment information
        // - Create and manage orders
        // - View and export reports
        // But cannot modify equipment or system settings
        Set<Permission> salesPermissions = new HashSet<>(Arrays.asList(
            Permission.EQUIPMENT_READ,
            Permission.ORDER_CREATE,
            Permission.ORDER_READ,
            Permission.ORDER_UPDATE,
            Permission.REPORT_VIEW,
            Permission.REPORT_EXPORT
        ));
        ROLE_PERMISSIONS.put(User.UserRole.sales, salesPermissions);

        // OT (Operation Team) role permissions
        // OT staff can:
        // - Create and manage equipment
        // - View orders
        // - View system logs
        // But cannot manage users or system settings
        Set<Permission> otPermissions = new HashSet<>(Arrays.asList(
            Permission.EQUIPMENT_CREATE,
            Permission.EQUIPMENT_READ,
            Permission.EQUIPMENT_UPDATE,
            Permission.ORDER_READ,
            Permission.SYSTEM_LOG
        ));
        ROLE_PERMISSIONS.put(User.UserRole.ot, otPermissions);
    }

    /**
     * Get all permissions associated with a specific role
     * 
     * @param role The user role to check
     * @return Set of permissions for the role, or empty set if role not found
     * @throws IllegalArgumentException if role is null
     */
    public static Set<Permission> getPermissionsForRole(User.UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        return ROLE_PERMISSIONS.getOrDefault(role, Collections.emptySet());
    }

    /**
     * Check if a role has a specific permission
     * This is the primary method used by the PermissionInterceptor
     * to verify access rights for protected endpoints.
     * 
     * @param role The user role to check
     * @param permission The permission to verify
     * @return true if the role has the permission, false otherwise
     * @throws IllegalArgumentException if either role or permission is null
     */
    public static boolean hasPermission(User.UserRole role, Permission permission) {
        if (role == null || permission == null) {
            throw new IllegalArgumentException("Role and permission cannot be null");
        }
        Set<Permission> permissions = ROLE_PERMISSIONS.get(role);
        return permissions != null && permissions.contains(permission);
    }
} 