package com.unimelbproject.healthcareequipmentassistant.services;

import com.unimelbproject.healthcareequipmentassistant.dto.EmailContent;
import com.unimelbproject.healthcareequipmentassistant.enums.EmailTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailTemplateService {

    /**
     * Generate email content based on template and parameters
     * @param template the email template to use
     * @param parameters template parameters for dynamic content
     * @return EmailContent with subject and body
     */
    public EmailContent generateContent(EmailTemplate template, Map<String, Object> parameters) {
        switch (template) {
            case ORDER_CONFIRMATION:
                return generateOrderConfirmation(parameters);
            case ORDER_STATUS_UPDATE:
                return generateOrderStatusUpdate(parameters);
            case ENQUIRY_RECEIVED:
                return generateEnquiryReceived(parameters);
            case ENQUIRY_REPLIED:
                return generateEnquiryReplied(parameters);
            case USER_WELCOME:
                return generateUserWelcome(parameters);
            case PASSWORD_RESET:
                return generatePasswordReset(parameters);
            case NOTIFICATION:
                return generateNotification(parameters);
            default:
                throw new IllegalArgumentException("Unsupported email template: " + template);
        }
    }

    private EmailContent generateOrderConfirmation(Map<String, Object> parameters) {
        String customerName = (String) parameters.getOrDefault("customerName", "Customer");
        String orderNumber = (String) parameters.getOrDefault("orderNumber", "N/A");
        String orderTotal = (String) parameters.getOrDefault("orderTotal", "0.00");
        
        String subject = "Order Confirmation - #" + orderNumber;
        String body = String.format(
            "Dear %s,\n\n" +
            "Thank you for your order! We have received your order and it is being processed.\n\n" +
            "Order Details:\n" +
            "Order Number: %s\n" +
            "Total Amount: $%s\n\n" +
            "You will receive another email when your order has been shipped.\n\n" +
            "Best regards,\n" +
            "Healthcare Equipment Assistant Team",
            customerName, orderNumber, orderTotal
        );
        
        return new EmailContent(subject, body, customerName);
    }

    private EmailContent generateOrderStatusUpdate(Map<String, Object> parameters) {
        String customerName = (String) parameters.getOrDefault("customerName", "Customer");
        String orderNumber = (String) parameters.getOrDefault("orderNumber", "N/A");
        String status = (String) parameters.getOrDefault("status", "updated");
        String trackingNumber = (String) parameters.get("trackingNumber");
        
        String subject = "Order Status Update - #" + orderNumber;
        StringBuilder body = new StringBuilder();
        body.append(String.format("Dear %s,\n\n", customerName));
        body.append(String.format("Your order #%s status has been updated to: %s\n\n", orderNumber, status));
        
        if (trackingNumber != null) {
            body.append(String.format("Tracking Number: %s\n\n", trackingNumber));
        }
        
        body.append("Best regards,\n");
        body.append("Healthcare Equipment Assistant Team");
        
        return new EmailContent(subject, body.toString(), customerName);
    }

    private EmailContent generateEnquiryReceived(Map<String, Object> parameters) {
        String customerName = (String) parameters.getOrDefault("customerName", "Customer");
        String enquiryId = (String) parameters.getOrDefault("enquiryId", "N/A");
        
        String subject = "Enquiry Received - #" + enquiryId;
        String body = String.format(
            "Dear %s,\n\n" +
            "We have received your enquiry and our team will respond within 24 hours.\n\n" +
            "Enquiry ID: %s\n\n" +
            "Thank you for contacting us!\n\n" +
            "Best regards,\n" +
            "Healthcare Equipment Assistant Team",
            customerName, enquiryId
        );
        
        return new EmailContent(subject, body, customerName);
    }

    private EmailContent generateEnquiryReplied(Map<String, Object> parameters) {
        String customerName = (String) parameters.getOrDefault("customerName", "Customer");
        String enquiryId = (String) parameters.getOrDefault("enquiryId", "N/A");
        String answer = (String) parameters.getOrDefault("answer", "");
        
        String subject = "Enquiry Reply - #" + enquiryId;
        String body = String.format(
            "Dear %s,\n\n" +
            "We have replied to your enquiry #%s.\n\n" +
            "Our Response:\n%s\n\n" +
            "If you have any further questions, please don't hesitate to contact us.\n\n" +
            "Best regards,\n" +
            "Healthcare Equipment Assistant Team",
            customerName, enquiryId, answer
        );
        
        return new EmailContent(subject, body, customerName);
    }

    private EmailContent generateUserWelcome(Map<String, Object> parameters) {
        String userName = (String) parameters.getOrDefault("userName", "User");
        String role = (String) parameters.getOrDefault("role", "user");
        
        String subject = "Welcome to Healthcare Equipment Assistant";
        String body = String.format(
            "Dear %s,\n\n" +
            "Welcome to Healthcare Equipment Assistant!\n\n" +
            "Your account has been created with role: %s\n\n" +
            "You can now log in and start using our services.\n\n" +
            "If you have any questions, please don't hesitate to contact our support team.\n\n" +
            "Best regards,\n" +
            "Healthcare Equipment Assistant Team",
            userName, role
        );
        
        return new EmailContent(subject, body, userName);
    }

    private EmailContent generatePasswordReset(Map<String, Object> parameters) {
        String userName = (String) parameters.getOrDefault("userName", "User");
        String resetToken = (String) parameters.getOrDefault("resetToken", "");
        String resetUrl = (String) parameters.getOrDefault("resetUrl", "");
        
        String subject = "Password Reset Request";
        String body = String.format(
            "Dear %s,\n\n" +
            "We received a request to reset your password.\n\n" +
            "Please use the following link to reset your password:\n%s\n\n" +
            "Reset Token: %s\n\n" +
            "If you did not request this password reset, please ignore this email.\n\n" +
            "Best regards,\n" +
            "Healthcare Equipment Assistant Team",
            userName, resetUrl, resetToken
        );
        
        return new EmailContent(subject, body, userName);
    }

    private EmailContent generateNotification(Map<String, Object> parameters) {
        String recipientName = (String) parameters.getOrDefault("recipientName", "User");
        String title = (String) parameters.getOrDefault("title", "Notification");
        String message = (String) parameters.getOrDefault("message", "");
        
        String subject = title;
        String body = String.format(
            "Dear %s,\n\n" +
            "%s\n\n" +
            "Best regards,\n" +
            "Healthcare Equipment Assistant Team",
            recipientName, message
        );
        
        return new EmailContent(subject, body, recipientName);
    }
}