package com.edututor.dto.resource;

import com.edututor.entity.Resource;
import com.edututor.entity.ResourceType;

public class ResourceResponse {

    private Long id;
    private String title;
    private ResourceType type;
    private String url;
    private String filePath;
    private String content;
    private Integer orderIndex;

    public static ResourceResponse from(Resource r) {
        ResourceResponse dto = new ResourceResponse();
        dto.id = r.getId();
        dto.title = r.getTitle();
        dto.type = r.getType();
        dto.url = r.getUrl();
        dto.filePath = r.getFilePath();
        dto.content = r.getContent();
        dto.orderIndex = r.getOrderIndex();
        return dto;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public ResourceType getType() { return type; }
    public String getUrl() { return url; }
    public String getFilePath() { return filePath; }
    public String getContent() { return content; }
    public Integer getOrderIndex() { return orderIndex; }
}
