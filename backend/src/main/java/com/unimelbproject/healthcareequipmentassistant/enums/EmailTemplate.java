package com.unimelbproject.healthcareequipmentassistant.enums;

public enum EmailTemplate {
    ORDER_CONFIRMATION("Order Confirmation"),
    ORDER_STATUS_UPDATE("Order Status Update"),
    ENQUIRY_RECEIVED("Enquiry Received"),
    ENQUIRY_REPLIED("Enquiry Replied"),
    USER_WELCOME("Welcome"),
    PASSWORD_RESET("Password Reset"),
    NOTIFICATION("General Notification");

    private final String displayName;

    EmailTemplate(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}