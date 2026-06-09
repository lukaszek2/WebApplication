package com.edututor.service;

import com.edututor.dto.resource.ResourceRequest;
import com.edututor.dto.resource.ResourceResponse;
import com.edututor.entity.Resource;
import com.edututor.entity.Section;
import com.edututor.exception.ResourceNotFoundException;
import com.edututor.repository.ResourceRepository;
import com.edututor.repository.SectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final SectionRepository sectionRepository;

    public ResourceService(ResourceRepository resourceRepository, SectionRepository sectionRepository) {
        this.resourceRepository = resourceRepository;
        this.sectionRepository = sectionRepository;
    }

    public List<ResourceResponse> getResources(Long sectionId) {
        return resourceRepository.findBySectionIdOrderByOrderIndex(sectionId).stream()
                .map(ResourceResponse::from)
                .collect(Collectors.toList());
    }

    public ResourceResponse create(Long sectionId, ResourceRequest request) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));
        Resource resource = buildResource(request, section);
        return ResourceResponse.from(resourceRepository.save(resource));
    }

    public ResourceResponse update(Long resourceId, ResourceRequest request) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        resource.setTitle(request.getTitle());
        resource.setType(request.getType());
        resource.setUrl(request.getUrl());
        resource.setFilePath(request.getFilePath());
        resource.setContent(request.getContent());
        resource.setOrderIndex(request.getOrderIndex());
        return ResourceResponse.from(resourceRepository.save(resource));
    }

    public void delete(Long resourceId) {
        if (!resourceRepository.existsById(resourceId)) {
            throw new ResourceNotFoundException("Resource not found");
        }
        resourceRepository.deleteById(resourceId);
    }

    private Resource buildResource(ResourceRequest request, Section section) {
        Resource resource = new Resource();
        resource.setSection(section);
        resource.setTitle(request.getTitle());
        resource.setType(request.getType());
        resource.setUrl(request.getUrl());
        resource.setFilePath(request.getFilePath());
        resource.setContent(request.getContent());
        resource.setOrderIndex(request.getOrderIndex());
        return resource;
    }
}
