package com.revature.Project_1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
public class Reimbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reimbId;

    @Size(min = 3, message = "description should contain at least 3 characters")
    @NotNull(message = "description is required")
    private String description;

    @NotNull(message = "amount is required")
    @Positive(message = "amount should be a positive number")
    private float amount;

    @NotEmpty(message = "status cannot be empty")
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
//    @JsonManagedReference
    private User user;


    //boilerplate code


    public Reimbursement() {
    }

    public Reimbursement(int reimbId, String description, float amount, String status, User user) {
        this.reimbId = reimbId;
        this.description = description;
        this.amount = amount;
        this.status = status;
        this.user = user;
    }

    public int getReimbId() {
        return reimbId;
    }

    public void setReimbId(int reimbId) {
        this.reimbId = reimbId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
