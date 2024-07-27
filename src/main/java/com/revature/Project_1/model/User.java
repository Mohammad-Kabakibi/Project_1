package com.revature.Project_1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Size(min = 3, message = "first name should contain at least 3 characters")
    @NotNull(message = "first name is required")
    private String firstName;

    @Size(min = 3, message = "last name should contain at least 3 characters")
    @NotNull(message = "last name is required")
    private String lastName;

    @Column(unique = true)
    @Size(min = 3, message = "username should contain at least 3 characters")
    @NotNull(message = "username is required")
    private String username;

    @Column(nullable = false)
    @Size(min = 5, message = "password should contain at least 5 characters")
    @NotNull(message = "password is required")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roleId")
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Reimbursement> reimbursements;


    //boilerplate code


    public User() {
    }

    public User(int id, String firstName, String lastName, String username, String password, Role role) {
        this.userId = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public List<Reimbursement> getReimbursements() {
        return reimbursements;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int id) {
        this.userId = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
