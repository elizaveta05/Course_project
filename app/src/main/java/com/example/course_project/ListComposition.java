package com.example.course_project;

public class ListComposition {
    private String id;
    private String name;

    public ListComposition() {
        // Пустой конструктор (необходим для Firebase)
    }

    public ListComposition(String id, String name) {
        this.id = id;
        this.name = name;
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
}