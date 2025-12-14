package com.fitplanhub.model;

import java.sql.Timestamp;

public class FitnessPlan {
    private int planId;
    private int trainerId;
    private String title;
    private String description;
    private double price;
    private int durationDays;
    private Timestamp createdAt;

    private String trainerName;

    public FitnessPlan() {
    }

    public FitnessPlan(int trainerId, String title, String description,
            double price, int durationDays) {
        this.trainerId = trainerId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.durationDays = durationDays;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }
}