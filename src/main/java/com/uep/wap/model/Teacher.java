package com.uep.wap.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teachers")
public class Teacher extends User {

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<Course> courses = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "teacher_categories",
        joinColumns = @JoinColumn(name = "teacher_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();

    public Teacher() {}

    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }

    public List<Category> getCategories() { return categories; }
    public void setCategories(List<Category> categories) { this.categories = categories; }

    public Course createCourse(String title) {
        Course course = new Course();
        course.setTitle(title);
        course.setTeacher(this);
        courses.add(course);
        return course;
    }

    public void removeCourse(Long courseId) {
        courses.removeIf(c -> c.getId().equals(courseId));
    }
}
