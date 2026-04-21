package com.edututor.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "students")
public class Student extends User {
    public Student() {
        setRole(Role.STUDENT);
    }
}
