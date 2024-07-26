package com.revature.Project_1.model.DTO;


//NOTE: This class is temporarily, will be removed when we implement sessions
public class IncomingReimbDTO {
    private String description;
    private float amount;
    private int userId;

    public IncomingReimbDTO() {
    }

    public IncomingReimbDTO(String descriptioon, float amount, int userId) {
        this.description = descriptioon;
        this.amount = amount;
        this.userId = userId;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
