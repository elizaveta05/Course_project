package com.example.course_project;

public class OrderCart {
    private String userId;
    private String bouquetId;

    public OrderCart() {
        // Default constructor required for Firestore
    }

    public OrderCart(String userId, String bouquetId) {
        this.userId = userId;
        this.bouquetId = bouquetId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBouquetId() {
        return bouquetId;
    }

    public void setBouquetId(String bouquetId) {
        this.bouquetId = bouquetId;
    }
}
