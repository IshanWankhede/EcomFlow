package com.ecomflow.model;

public class Electronics extends Product {
    private String brand;
    private int warrantyMonths;

    public Electronics(String name, double price, int stock, Category category, String brand, int warrantyMonths) {
        super(name, price, stock, category);
        setBrand(brand);
        setWarrantyMonths(warrantyMonths);
    }

    public Electronics(String name, double price, int stock, Category category, String brand, int warrantyMonths, String imageUrl) {
        super(name, price, stock, category, imageUrl);
        setBrand(brand);
        setWarrantyMonths(warrantyMonths);
    }

    public Electronics(int productId, String name, double price, int stock, Category category, String brand, int warrantyMonths) {
        super(productId, name, price, stock, category);
        setBrand(brand);
        setWarrantyMonths(warrantyMonths);
    }

    public Electronics(int productId, String name, double price, int stock, Category category, String brand, int warrantyMonths, String imageUrl) {
        super(productId, name, price, stock, category, imageUrl);
        setBrand(brand);
        setWarrantyMonths(warrantyMonths);
    }

    /**
     * Electronics Discount Strategy:
     * High-warranty electronics (> 12 months) receive a 5% promotional discount
     * to incentivize customers to purchase premium covered devices.
     */
    @Override
    public double calculateDiscount() {
        if (warrantyMonths > 12) {
            return getPrice() * 0.05;
        }
        return 0.0;
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Brand        : " + brand);
        System.out.println("Warranty     : " + warrantyMonths + " months");
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = (brand != null) ? brand.trim() : "";
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(int warrantyMonths) {
        if (warrantyMonths < 0) {
            throw new IllegalArgumentException("Warranty months cannot be negative.");
        }
        this.warrantyMonths = warrantyMonths;
    }
}
