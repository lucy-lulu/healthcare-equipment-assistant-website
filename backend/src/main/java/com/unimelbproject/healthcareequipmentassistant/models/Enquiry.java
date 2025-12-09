package com.unimelbproject.healthcareequipmentassistant.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enquire")
public class Enquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enquire_id")
    private Long id;

    @Column(name = "asker_id", columnDefinition = "CHAR(36)", nullable = false)
    private String askerId;

    @Column(name = "responder_id", columnDefinition = "CHAR(36)")
    private String responderId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(length = 50, nullable = false)
    private String status = "pending"; // pending | answered

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime timestamp = LocalDateTime.now();

    // getters & setters
    public Long getId() { return id; }
    public String getAskerId() { return askerId; }
    public void setAskerId(String askerId) { this.askerId = askerId; }
    public String getResponderId() { return responderId; }
    public void setResponderId(String responderId) { this.responderId = responderId; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
