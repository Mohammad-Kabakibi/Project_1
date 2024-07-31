package com.revature.Project_1.model.DTO;


//NOTE: This class is temporarily, will be removed when we implement sessions
public class IncomingReimbDTO {
    private String description;
    private float amount;

    public IncomingReimbDTO() {
    }

    public IncomingReimbDTO(String descriptioon, float amount) {
        this.description = descriptioon;
        this.amount = amount;
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
}
