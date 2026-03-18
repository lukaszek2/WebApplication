package com.uep.wap.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "enrolled_at")
    private LocalDate enrolledAt;

    @ManyToOne
    @JoinColumn(name = "student_fk", insertable = false, updatable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_fk", insertable = false, updatable = false)
    private Course course;

    public Enrollment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public LocalDate getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDate enrolledAt) { this.enrolledAt = enrolledAt; }

    public Student getStudent() { return student; }
    public Course getCourse() { return course; }

    public double getProgress() { return 0.0; }
    public String getStatus() { return "ACTIVE"; }
}
