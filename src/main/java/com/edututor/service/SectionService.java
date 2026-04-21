package com.edututor.service;

import com.edututor.dto.section.SectionRequest;
import com.edututor.dto.section.SectionResponse;
import com.edututor.entity.Course;
import com.edututor.entity.Section;
import com.edututor.exception.ResourceNotFoundException;
import com.edututor.repository.CourseRepository;
import com.edututor.repository.SectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;

    public SectionService(SectionRepository sectionRepository, CourseRepository courseRepository) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
    }

    public List<SectionResponse> getSections(Long courseId) {
        return sectionRepository.findByCourseIdOrderByOrderIndex(courseId).stream()
                .map(SectionResponse::from)
                .collect(Collectors.toList());
    }

    public SectionResponse create(Long courseId, SectionRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        Section section = new Section();
        section.setCourse(course);
        section.setTitle(request.getTitle());
        section.setOrderIndex(request.getOrderIndex());
        return SectionResponse.from(sectionRepository.save(section));
    }

    public SectionResponse update(Long courseId, Long sectionId, SectionRequest request) {
        Section section = getSection(courseId, sectionId);
        section.setTitle(request.getTitle());
        section.setOrderIndex(request.getOrderIndex());
        return SectionResponse.from(sectionRepository.save(section));
    }

    public void delete(Long courseId, Long sectionId) {
        Section section = getSection(courseId, sectionId);
        sectionRepository.delete(section);
    }

    private Section getSection(Long courseId, Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));
        if (!section.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Section not found in this course");
        }
        return section;
    }
}
