package com.ecomflow.model;

public abstract class Product {
    private static int productCounter = 5000;

    private int productId;
    private String name;
    private double price;
    private int stock;
    private Category category;
    private String imageUrl;

    public Product(String name, double price, int stock, Category category) {
        this.productId = ++productCounter;
        setName(name);
        setPrice(price);
        setStock(stock);
        setCategory(category);
    }

    public Product(String name, double price, int stock, Category category, String imageUrl) {
        this(name, price, stock, category);
        setImageUrl(imageUrl);
    }

    public Product(int productId, String name, double price, int stock, Category category) {
        this.productId = productId;
        setName(name);
        setPrice(price);
        setStock(stock);
        setCategory(category);
    }

    public Product(int productId, String name, double price, int stock, Category category, String imageUrl) {
        this(productId, name, price, stock, category);
        setImageUrl(imageUrl);
    }

    /**
     * Calculates the category/product-specific discount amount.
     * Concrete subclasses provide specialized business logic for this calculation.
     *
     * @return the discount amount in currency units
     */
    public abstract double calculateDiscount();

    public void updateStock(int delta) {
        if (this.stock + delta < 0) {
            throw new IllegalArgumentException("Cannot reduce stock below 0. Current stock: "
                    + this.stock + ", requested reduction: " + Math.abs(delta));
        }
        this.stock += delta;
    }

    public void displayDetails() {
        System.out.println("---------------- [PRODUCT DETAILS] ----------------");
        System.out.println("Product ID   : " + productId);
        System.out.println("Name         : " + name);
        System.out.println("Category     : " + (category != null ? category.getName() : "Uncategorized"));
        System.out.printf("Base Price   : $%.2f%n", price);
        System.out.println("Stock Level  : " + stock + " units");
        System.out.printf("Discount Amt : $%.2f (Final: $%.2f)%n", calculateDiscount(), getEffectivePrice());
    }

    public double getEffectivePrice() {
        double discount = calculateDiscount();
        return Math.max(0.0, price - discount);
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        this.name = name.trim();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0. Received: " + price);
        }
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative. Received: " + stock);
        }
        this.stock = stock;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = (imageUrl != null) ? imageUrl.trim() : null;
    }

    public static int getProductCounter() {
        return productCounter;
    }
}
