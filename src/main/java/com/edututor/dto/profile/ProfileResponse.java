package com.edututor.dto.profile;

import com.edututor.entity.User;

public class ProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private Boolean isActive;

    public static ProfileResponse from(User u) {
        ProfileResponse r = new ProfileResponse();
        r.id = u.getId();
        r.name = u.getName();
        r.email = u.getEmail();
        r.role = u.getRole().name();
        r.isActive = u.getIsActive();
        return r;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public Boolean getIsActive() { return isActive; }
}
