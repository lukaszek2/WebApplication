package com.edututor.dto.category;

import com.edututor.entity.Category;

public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private String icon;

    public static CategoryResponse from(Category c) {
        CategoryResponse r = new CategoryResponse();
        r.id = c.getId();
        r.name = c.getName();
        r.description = c.getDescription();
        r.icon = c.getIcon();
        return r;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
}
