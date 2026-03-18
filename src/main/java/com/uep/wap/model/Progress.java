package com.uep.wap.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress")
public class Progress {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public Progress() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
