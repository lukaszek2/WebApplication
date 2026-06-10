package com.edututor.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_logs")
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action;

    @Column(length = 500)
    private String details;

    private Long userId;

    private String userEmail;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public SystemLog() {}

    public SystemLog(String action, String details, Long userId, String userEmail) {
        this.action = action;
        this.details = details;
        this.userId = userId;
        this.userEmail = userEmail;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public Long getUserId() { return userId; }
    public String getUserEmail() { return userEmail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
