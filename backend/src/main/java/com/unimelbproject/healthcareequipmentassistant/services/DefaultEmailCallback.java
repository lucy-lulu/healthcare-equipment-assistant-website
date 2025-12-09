package com.unimelbproject.healthcareequipmentassistant.services;

import com.unimelbproject.healthcareequipmentassistant.enums.EmailTemplate;
import com.unimelbproject.healthcareequipmentassistant.interfaces.EmailCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultEmailCallback implements EmailCallback {

    private static final Logger logger = LoggerFactory.getLogger(DefaultEmailCallback.class);

    @Override
    public void onStart(EmailTemplate template, String recipientEmail, String subject) {
        logger.info("Starting email sending - Template: {}, Recipient: {}, Subject: {}", 
                   template, recipientEmail, subject);
    }

    @Override
    public void onSuccess(EmailTemplate template, String recipientEmail, String subject) {
        logger.info("Email sent successfully - Template: {}, Recipient: {}, Subject: {}", 
                   template, recipientEmail, subject);
    }

    @Override
    public void onFailure(EmailTemplate template, String recipientEmail, String subject, Throwable error) {
        logger.error("Email sending failed - Template: {}, Recipient: {}, Subject: {}, Error: {}", 
                    template, recipientEmail, subject, error.getMessage(), error);
    }
}