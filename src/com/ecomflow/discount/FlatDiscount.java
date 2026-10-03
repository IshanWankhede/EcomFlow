package com.ecomflow.discount;

import com.ecomflow.interfaces.Discountable;

public class FlatDiscount implements Discountable {
    private double flatAmount;

    public FlatDiscount(double flatAmount) {
        if (flatAmount < 0) {
            throw new IllegalArgumentException("Flat discount amount cannot be negative. Received: " + flatAmount);
        }
        this.flatAmount = flatAmount;
    }

    @Override
    public double calculateDiscount(double amount) {
        if (amount <= 0) return 0.0;
        return Math.min(amount, flatAmount);
    }

    public double getFlatAmount() {
        return flatAmount;
    }

    public void setFlatAmount(double flatAmount) {
        if (flatAmount < 0) {
            throw new IllegalArgumentException("Flat discount amount cannot be negative.");
        }
        this.flatAmount = flatAmount;
    }

    @Override
    public String toString() {
        return String.format("Flat $%.2f Off", flatAmount);
    }
}
