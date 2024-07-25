package com.revature.Project_1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Role {
    @Id
    private int id;

    @Column(unique = true, nullable = false)
    private String name;


}
