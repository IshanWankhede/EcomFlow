package com.ecomflow.model;

public class Category {
    private static int categoryCounter = 100;

    private int categoryId;
    private String name;

    public Category(String name) {
        this.categoryId = ++categoryCounter;
        setName(name);
    }

    public Category(int categoryId, String name) {
        this.categoryId = categoryId;
        setName(name);
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }
        this.name = name.trim();
    }

    @Override
    public String toString() {
        return name + " (ID: " + categoryId + ")";
    }
}
