package com.unimelbproject.healthcareequipmentassistant.services;

import com.unimelbproject.healthcareequipmentassistant.dto.EmailContent;
import com.unimelbproject.healthcareequipmentassistant.enums.EmailTemplate;
import com.unimelbproject.healthcareequipmentassistant.interfaces.EmailCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailTemplateService templateService;

    @Autowired
    private DefaultEmailCallback defaultCallback;

    @Value("${spring.mail.username:test@example.com}")
    private String fromEmail;

    /**
     * Send email to recipient
     * @param to recipient email address
     * @param subject email subject
     * @param body email content
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendEmail(String to, String subject, String body) {
        try {
            // Parameter validation
            if (to == null || to.trim().isEmpty()) {
                logger.error("Email sending failed: recipient email is null or empty");
                return false;
            }
            
            if (subject == null || subject.trim().isEmpty()) {
                logger.warn("Email subject is empty for recipient: {}", to);
            }
            
            if (body == null) {
                logger.warn("Email body is null for recipient: {}", to);
                body = "";
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to.trim());
            message.setSubject(subject != null ? subject.trim() : "");
            message.setText(body);
            
            logger.info("Attempting to send email to: {} with subject: {}", to, subject);
            mailSender.send(message);
            logger.info("Email sent successfully to: {}", to);
            
            return true;
            
        } catch (MailException e) {
            logger.error("Failed to send email to: {} due to mail exception: {}", to, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while sending email to: {}. Error: {}", to, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Send email to recipient (legacy void method for backward compatibility)
     * @param to recipient email address
     * @param subject email subject
     * @param body email content
     * @deprecated Use sendEmail method that returns boolean for sending result
     */
    @Deprecated
    public void sendEmailVoid(String to, String subject, String body) {
        boolean success = sendEmail(to, subject, body);
        if (!success) {
            logger.warn("Email sending failed but using void method - error details logged above");
        }
    }

    /**
     * Send email asynchronously
     * @param to recipient email address
     * @param subject email subject
     * @param body email content
     * @return CompletableFuture with sending result
     */
    @Async("emailTaskExecutor")
    public CompletableFuture<Boolean> sendEmailAsync(String to, String subject, String body) {
        logger.info("Starting async email sending to: {}", to);
        boolean result = sendEmail(to, subject, body);
        logger.info("Async email sending completed for: {} with result: {}", to, result);
        return CompletableFuture.completedFuture(result);
    }

    /**
     * Send email asynchronously without return value (fire and forget)
     * @param to recipient email address
     * @param subject email subject
     * @param body email content
     */
    @Async("emailTaskExecutor")
    public void sendEmailAsyncVoid(String to, String subject, String body) {
        logger.info("Starting fire-and-forget async email sending to: {}", to);
        sendEmail(to, subject, body);
    }

    // === Template-based Email Methods ===

    /**
     * Send email using template synchronously
     * @param template email template to use
     * @param recipientEmail recipient email address
     * @param templateParams parameters for template generation
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendTemplateEmail(EmailTemplate template, String recipientEmail, Map<String, Object> templateParams) {
        return sendTemplateEmail(template, recipientEmail, templateParams, defaultCallback);
    }

    /**
     * Send email using template synchronously with callback
     * @param template email template to use
     * @param recipientEmail recipient email address
     * @param templateParams parameters for template generation
     * @param callback callback for handling send results
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendTemplateEmail(EmailTemplate template, String recipientEmail, 
                                   Map<String, Object> templateParams, EmailCallback callback) {
        try {
            EmailContent content = templateService.generateContent(template, templateParams);
            
            callback.onStart(template, recipientEmail, content.getSubject());
            
            boolean result = sendEmail(recipientEmail, content.getSubject(), content.getBody());
            
            if (result) {
                callback.onSuccess(template, recipientEmail, content.getSubject());
            } else {
                callback.onFailure(template, recipientEmail, content.getSubject(), 
                                 new RuntimeException("Email sending failed"));
            }
            
            return result;
            
        } catch (Exception e) {
            callback.onFailure(template, recipientEmail, "Unknown", e);
            return false;
        }
    }

    /**
     * Send email using template asynchronously
     * @param template email template to use
     * @param recipientEmail recipient email address
     * @param templateParams parameters for template generation
     * @return CompletableFuture with sending result
     */
    @Async("emailTaskExecutor")
    public CompletableFuture<Boolean> sendTemplateEmailAsync(EmailTemplate template, String recipientEmail, 
                                                           Map<String, Object> templateParams) {
        return sendTemplateEmailAsync(template, recipientEmail, templateParams, defaultCallback);
    }

    /**
     * Send email using template asynchronously with callback
     * @param template email template to use
     * @param recipientEmail recipient email address
     * @param templateParams parameters for template generation
     * @param callback callback for handling send results
     * @return CompletableFuture with sending result
     */
    @Async("emailTaskExecutor")
    public CompletableFuture<Boolean> sendTemplateEmailAsync(EmailTemplate template, String recipientEmail, 
                                                           Map<String, Object> templateParams, 
                                                           EmailCallback callback) {
        logger.info("Starting async template email sending - Template: {}, Recipient: {}", template, recipientEmail);
        
        boolean result = sendTemplateEmail(template, recipientEmail, templateParams, callback);
        
        logger.info("Async template email sending completed - Template: {}, Recipient: {}, Result: {}", 
                   template, recipientEmail, result);
        
        return CompletableFuture.completedFuture(result);
    }

    /**
     * Send email using template asynchronously (fire and forget)
     * @param template email template to use
     * @param recipientEmail recipient email address
     * @param templateParams parameters for template generation
     */
    @Async("emailTaskExecutor")
    public void sendTemplateEmailAsyncVoid(EmailTemplate template, String recipientEmail, 
                                         Map<String, Object> templateParams) {
        sendTemplateEmailAsyncVoid(template, recipientEmail, templateParams, defaultCallback);
    }

    /**
     * Send email using template asynchronously (fire and forget) with callback
     * @param template email template to use
     * @param recipientEmail recipient email address
     * @param templateParams parameters for template generation
     * @param callback callback for handling send results
     */
    @Async("emailTaskExecutor")
    public void sendTemplateEmailAsyncVoid(EmailTemplate template, String recipientEmail, 
                                         Map<String, Object> templateParams, EmailCallback callback) {
        logger.info("Starting fire-and-forget template email sending - Template: {}, Recipient: {}", 
                   template, recipientEmail);
        
        sendTemplateEmail(template, recipientEmail, templateParams, callback);
    }
}
