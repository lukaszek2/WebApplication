package com.edututor.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin extends User {
    public Admin() {
        setRole(Role.ADMIN);
    }
}
