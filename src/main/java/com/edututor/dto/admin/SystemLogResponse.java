package com.edututor.dto.admin;

import com.edututor.entity.SystemLog;

import java.time.LocalDateTime;

public class SystemLogResponse {

    private Long id;
    private String action;
    private String details;
    private Long userId;
    private String userEmail;
    private LocalDateTime createdAt;

    public static SystemLogResponse from(SystemLog log) {
        SystemLogResponse r = new SystemLogResponse();
        r.id = log.getId();
        r.action = log.getAction();
        r.details = log.getDetails();
        r.userId = log.getUserId();
        r.userEmail = log.getUserEmail();
        r.createdAt = log.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public Long getUserId() { return userId; }
    public String getUserEmail() { return userEmail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
