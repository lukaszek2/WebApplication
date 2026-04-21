package com.edututor.dto.section;

import com.edututor.dto.resource.ResourceResponse;
import com.edututor.entity.Section;

import java.util.List;

public class SectionResponse {

    private Long id;
    private String title;
    private Integer orderIndex;
    private List<ResourceResponse> resources;

    public static SectionResponse from(Section s, List<ResourceResponse> resources) {
        SectionResponse r = new SectionResponse();
        r.id = s.getId();
        r.title = s.getTitle();
        r.orderIndex = s.getOrderIndex();
        r.resources = resources;
        return r;
    }

    public static SectionResponse from(Section s) {
        return from(s, null);
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Integer getOrderIndex() { return orderIndex; }
    public List<ResourceResponse> getResources() { return resources; }
}
