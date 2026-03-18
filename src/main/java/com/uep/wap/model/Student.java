package com.uep.wap.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student extends User {

    @Column(name = "points")
    private Integer points;

    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrolledCourses = new ArrayList<>();

    @OneToMany
    @JoinTable(
        name = "student_completed_resources",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    private List<Resource> completedResources = new ArrayList<>();

    public Student() {}

    public Student(String name, Integer points) {
        setName(name);
        this.points = points;
    }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public List<Enrollment> getEnrolledCourses() { return enrolledCourses; }
    public void setEnrolledCourses(List<Enrollment> enrolledCourses) { this.enrolledCourses = enrolledCourses; }

    public List<Resource> getCompletedResources() { return completedResources; }
    public void setCompletedResources(List<Resource> completedResources) { this.completedResources = completedResources; }

    public void markComplete(Long resourceId) {
        // logika oznaczania zasobu jako ukończonego
    }

    public double getProgress(Long courseId) {
        return 0.0;
    }
}
