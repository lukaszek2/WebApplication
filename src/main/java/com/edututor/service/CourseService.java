package com.edututor.service;

import com.edututor.dto.course.CourseDetailResponse;
import com.edututor.dto.course.CourseRequest;
import com.edututor.dto.course.CourseResponse;
import com.edututor.dto.resource.ResourceResponse;
import com.edututor.dto.section.SectionResponse;
import com.edututor.entity.*;
import com.edututor.exception.ResourceNotFoundException;
import com.edututor.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final TeacherRepository teacherRepository;
    private final SectionRepository sectionRepository;
    private final ResourceRepository resourceRepository;

    public CourseService(CourseRepository courseRepository, CategoryRepository categoryRepository,
                         TeacherRepository teacherRepository, SectionRepository sectionRepository,
                         ResourceRepository resourceRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.teacherRepository = teacherRepository;
        this.sectionRepository = sectionRepository;
        this.resourceRepository = resourceRepository;
    }

    public List<CourseResponse> getPublishedCourses() {
        return courseRepository.findByStatus(CourseStatus.PUBLISHED).stream()
                .map(CourseResponse::from)
                .collect(Collectors.toList());
    }

    public CourseDetailResponse getCourseDetail(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        List<SectionResponse> sections = sectionRepository
                .findByCourseIdOrderByOrderIndex(courseId).stream()
                .map(s -> {
                    List<ResourceResponse> resources = resourceRepository
                            .findBySectionIdOrderByOrderIndex(s.getId()).stream()
                            .map(ResourceResponse::from)
                            .collect(Collectors.toList());
                    return SectionResponse.from(s, resources);
                })
                .collect(Collectors.toList());

        return CourseDetailResponse.from(course, sections);
    }

    public ResourceResponse getResource(Long courseId, Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        if (!resource.getSection().getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Resource not found in this course");
        }
        return ResourceResponse.from(resource);
    }

    public CourseResponse createCourse(CourseRequest request, Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCoverImage(request.getCoverImage());
        course.setTeacher(teacher);
        if (request.getCategoryId() != null) {
            categoryRepository.findById(request.getCategoryId()).ifPresent(course::setCategory);
        }
        return CourseResponse.from(courseRepository.save(course));
    }

    public CourseResponse updateCourse(Long courseId, CourseRequest request, Long teacherId) {
        Course course = getTeacherCourse(courseId, teacherId);
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCoverImage(request.getCoverImage());
        if (request.getCategoryId() != null) {
            categoryRepository.findById(request.getCategoryId()).ifPresent(course::setCategory);
        }
        return CourseResponse.from(courseRepository.save(course));
    }

    public void deleteCourse(Long courseId, Long teacherId) {
        Course course = getTeacherCourse(courseId, teacherId);
        courseRepository.delete(course);
    }

    public CourseResponse publishCourse(Long courseId, Long teacherId) {
        Course course = getTeacherCourse(courseId, teacherId);
        course.setStatus(CourseStatus.PUBLISHED);
        return CourseResponse.from(courseRepository.save(course));
    }

    public List<CourseResponse> getTeacherCourses(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream()
                .map(CourseResponse::from)
                .collect(Collectors.toList());
    }

    private Course getTeacherCourse(Long courseId, Long teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new IllegalArgumentException("You don't own this course");
        }
        return course;
    }
}
