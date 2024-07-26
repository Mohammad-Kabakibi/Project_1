package com.revature.Project_1.model.DTO;


//NOTE: This class is temporarily, will be removed when we implement sessions
public class IncomingReimbDTO {
    private String descriptioon;
    private float amount;
    private int userId;

    public IncomingReimbDTO() {
    }

    public IncomingReimbDTO(String descriptioon, float amount, int userId) {
        this.descriptioon = descriptioon;
        this.amount = amount;
        this.userId = userId;
    }

    public String getDescriptioon() {
        return descriptioon;
    }

    public void setDescriptioon(String descriptioon) {
        this.descriptioon = descriptioon;
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
