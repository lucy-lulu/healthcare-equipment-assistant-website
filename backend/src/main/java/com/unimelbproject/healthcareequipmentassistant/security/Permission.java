package com.unimelbproject.healthcareequipmentassistant.security;

public enum Permission {
    // User Management
    USER_CREATE("user:create"),
    USER_READ("user:read"),
    USER_UPDATE("user:update"),
    USER_DELETE("user:delete"),

    // Equipment Management
    EQUIPMENT_CREATE("equipment:create"),
    EQUIPMENT_READ("equipment:read"),
    EQUIPMENT_UPDATE("equipment:update"),
    EQUIPMENT_DELETE("equipment:delete"),

    // Order Management
    ORDER_CREATE("order:create"),
    ORDER_READ("order:read"),
    ORDER_UPDATE("order:update"),
    ORDER_DELETE("order:delete"),

    // Report Management
    REPORT_VIEW("report:view"),
    REPORT_EXPORT("report:export"),

    // System Management
    SYSTEM_CONFIG("system:config"),
    SYSTEM_LOG("system:log");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
} 