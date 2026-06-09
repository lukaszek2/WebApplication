package com.edututor.dto.profile;

import javax.validation.constraints.NotBlank;

public class ProfileRequest {

    @NotBlank
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
