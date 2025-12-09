package com.unimelbproject.healthcareequipmentassistant.interfaces;

import com.unimelbproject.healthcareequipmentassistant.enums.EmailTemplate;

public interface EmailCallback {
    
    /**
     * Called when email sending is successful
     * @param template the email template used
     * @param recipientEmail recipient email address
     * @param subject email subject
     */
    void onSuccess(EmailTemplate template, String recipientEmail, String subject);
    
    /**
     * Called when email sending fails
     * @param template the email template used
     * @param recipientEmail recipient email address
     * @param subject email subject
     * @param error the error that occurred
     */
    void onFailure(EmailTemplate template, String recipientEmail, String subject, Throwable error);
    
    /**
     * Called before email sending starts
     * @param template the email template used
     * @param recipientEmail recipient email address
     * @param subject email subject
     */
    default void onStart(EmailTemplate template, String recipientEmail, String subject) {
        // Default empty implementation
    }
}