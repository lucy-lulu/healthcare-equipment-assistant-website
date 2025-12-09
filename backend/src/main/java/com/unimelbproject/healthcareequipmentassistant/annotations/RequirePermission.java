package com.unimelbproject.healthcareequipmentassistant.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.unimelbproject.healthcareequipmentassistant.security.Permission;

/**
 * Permission Requirement Annotation
 * This annotation is used to specify the required permission for accessing a method.
 * It is processed by the PermissionInterceptor to enforce permission-based access control.
 * 
 * Usage example:
 * {@code
 * @RequirePermission(Permission.USER_CREATE)
 * public void createUser() {
 *     // Method implementation
 * }
 * }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    /**
     * The permission required to access the annotated method
     * 
     * @return The required permission
     */
    Permission value();
} 