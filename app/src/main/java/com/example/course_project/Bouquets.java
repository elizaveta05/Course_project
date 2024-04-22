package com.example.course_project;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.PropertyName;

public class Bouquets {
    @PropertyName("Document ID")
    private String id;

    @PropertyName("Name")
    private String name;

    @PropertyName("Description")
    private String description;

    @PropertyName("Image")
    private String image;

    @PropertyName("Cost")
    private String cost;

    @PropertyName("Category")
    private DocumentReference category;

    @PropertyName("Composition")
    private DocumentReference composition;

    private boolean isFavorite;
    private String categoryName;

    public Bouquets() {
        // Empty constructor (required for Firebase)
    }

    public Bouquets(String id, String name, String description, String image, String cost, DocumentReference category, DocumentReference composition) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.cost = cost;
        this.category = category;
        this.composition = composition;
    }


    public boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public DocumentReference getCategory() {
        return category;
    }

    public void setCategory(DocumentReference category) {
        this.category = category;
    }

    public DocumentReference getComposition() {
        return composition;
    }

    public void setComposition(DocumentReference composition) {
        this.composition = composition;
    }
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}