package com.edututor.dto.course;

import com.edututor.dto.section.SectionResponse;
import com.edututor.entity.Course;
import com.edututor.entity.CourseStatus;

import java.time.LocalDateTime;
import java.util.List;

public class CourseDetailResponse {

    private Long id;
    private String title;
    private String description;
    private CourseStatus status;
    private String coverImage;
    private Long categoryId;
    private String categoryName;
    private Long teacherId;
    private String teacherName;
    private LocalDateTime createdAt;
    private List<SectionResponse> sections;

    public static CourseDetailResponse from(Course c, List<SectionResponse> sections) {
        CourseDetailResponse r = new CourseDetailResponse();
        r.id = c.getId();
        r.title = c.getTitle();
        r.description = c.getDescription();
        r.status = c.getStatus();
        r.coverImage = c.getCoverImage();
        if (c.getCategory() != null) {
            r.categoryId = c.getCategory().getId();
            r.categoryName = c.getCategory().getName();
        }
        r.teacherId = c.getTeacher().getId();
        r.teacherName = c.getTeacher().getName();
        r.createdAt = c.getCreatedAt();
        r.sections = sections;
        return r;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public CourseStatus getStatus() { return status; }
    public String getCoverImage() { return coverImage; }
    public Long getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public Long getTeacherId() { return teacherId; }
    public String getTeacherName() { return teacherName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<SectionResponse> getSections() { return sections; }
}
