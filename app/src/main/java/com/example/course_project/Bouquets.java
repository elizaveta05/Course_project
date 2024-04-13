package com.example.course_project;

import com.google.firebase.firestore.PropertyName;

public class Bouquets {
    @PropertyName("Name")
    private String name;

    @PropertyName("Description")
    private String description;

    @PropertyName("Image")
    private String image;

    @PropertyName("Cost")
    private String cost;

    @PropertyName("Category")
    private String category;

    @PropertyName("Composition")
    private String composition;

    public Bouquets() {
        // Пустой конструктор (необходим для Firebase)
    }

    public Bouquets(String name, String description, String image, String cost, String category, String composition) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.cost = cost;
        this.category = category;
        this.composition = composition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }
}