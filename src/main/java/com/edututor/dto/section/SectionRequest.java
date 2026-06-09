package com.edututor.dto.section;

import javax.validation.constraints.NotBlank;

public class SectionRequest {

    @NotBlank
    private String title;

    private Integer orderIndex = 0;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
}
