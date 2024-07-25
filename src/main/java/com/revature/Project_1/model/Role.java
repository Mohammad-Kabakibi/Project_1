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


    //boilerplate code


    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Role() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
