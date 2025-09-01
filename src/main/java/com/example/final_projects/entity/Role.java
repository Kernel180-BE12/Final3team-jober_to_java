package com.example.final_projects.entity;

import jakarta.persistence.*;


@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long role_id;

    @Column(name="role_name",nullable = false, unique=true, length = 50)
    private String name;

    public Role(Long role_id, String name) {
        this.role_id = role_id;
        this.name = name;
    }

    public Role() {

    }

    public Long getId() {
        return role_id;
    }

    public void setId(Long role_id) {
        this.role_id = role_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
