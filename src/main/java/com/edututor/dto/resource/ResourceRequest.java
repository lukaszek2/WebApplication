package com.edututor.dto.resource;

import com.edututor.entity.ResourceType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ResourceRequest {

    @NotBlank
    private String title;

    @NotNull
    private ResourceType type;

    private String url;
    private String filePath;
    private String content;
    private Integer orderIndex = 0;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public ResourceType getType() { return type; }
    public void setType(ResourceType type) { this.type = type; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
}
