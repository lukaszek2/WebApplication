package com.edututor.service;

import com.edututor.entity.*;
import com.edututor.exception.ResourceNotFoundException;
import com.edututor.repository.*;
import org.springframework.stereotype.Service;

@Service
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final StudentRepository studentRepository;
    private final ResourceRepository resourceRepository;

    public ProgressService(ProgressRepository progressRepository,
                           StudentRepository studentRepository,
                           ResourceRepository resourceRepository) {
        this.progressRepository = progressRepository;
        this.studentRepository = studentRepository;
        this.resourceRepository = resourceRepository;
    }

    public void markComplete(Long studentId, Long resourceId) {
        if (progressRepository.existsByStudentIdAndResourceId(studentId, resourceId)) {
            return;
        }
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        Progress progress = new Progress();
        progress.setStudent(student);
        progress.setResource(resource);
        progressRepository.save(progress);
    }
}
