package com.edututor.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "teachers")
public class Teacher extends User {
    public Teacher() {
        setRole(Role.TEACHER);
    }
}
