package com.example.course_project;

public class FavoriteItem {
    private String userId;
    private String bouquetId;

    public FavoriteItem() {
        // Default constructor required for Firestore
    }

    public FavoriteItem(String userId, String bouquetId) {
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