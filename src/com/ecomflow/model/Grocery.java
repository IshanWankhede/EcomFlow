package com.ecomflow.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Grocery extends Product {
    private LocalDate expiryDate;

    public Grocery(String name, double price, int stock, Category category, LocalDate expiryDate) {
        super(name, price, stock, category);
        setExpiryDate(expiryDate);
    }

    public Grocery(String name, double price, int stock, Category category, LocalDate expiryDate, String imageUrl) {
        super(name, price, stock, category, imageUrl);
        setExpiryDate(expiryDate);
    }

    public Grocery(int productId, String name, double price, int stock, Category category, LocalDate expiryDate) {
        super(productId, name, price, stock, category);
        setExpiryDate(expiryDate);
    }

    public Grocery(int productId, String name, double price, int stock, Category category, LocalDate expiryDate, String imageUrl) {
        super(productId, name, price, stock, category, imageUrl);
        setExpiryDate(expiryDate);
    }

    /**
     * Grocery Discount Strategy:
     * Perishable items receive dynamic discounts as their expiration date nears:
     * - Within 7 days of expiry: 30% discount (flash clearance for perishable goods)
     * - Within 30 days of expiry: 15% discount
     * - More than 30 days or expired: standard base pricing (0% discount)
     */
    @Override
    public double calculateDiscount() {
        if (expiryDate == null) {
            return 0.0;
        }

        LocalDate today = LocalDate.now();
        long daysUntilExpiry = ChronoUnit.DAYS.between(today, expiryDate);

        if (daysUntilExpiry >= 0 && daysUntilExpiry <= 7) {
            return getPrice() * 0.30;
        } else if (daysUntilExpiry > 7 && daysUntilExpiry <= 30) {
            return getPrice() * 0.15;
        }
        return 0.0;
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Expiry Date  : " + (expiryDate != null ? expiryDate.toString() : "N/A"));
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        if (expiryDate == null) {
            throw new IllegalArgumentException("Expiry date cannot be null.");
        }
        this.expiryDate = expiryDate;
    }
}
