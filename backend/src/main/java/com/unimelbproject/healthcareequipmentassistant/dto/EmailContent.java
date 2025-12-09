package com.unimelbproject.healthcareequipmentassistant.dto;

public class EmailContent {
    private String subject;
    private String body;
    private String recipientName;

    public EmailContent() {}

    public EmailContent(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public EmailContent(String subject, String body, String recipientName) {
        this.subject = subject;
        this.body = body;
        this.recipientName = recipientName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}