package com.edututor.dto.admin;

import com.edututor.entity.Role;

import javax.validation.constraints.NotNull;

public class RoleUpdateRequest {

    @NotNull
    private Role role;

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
