package com.ecomflow.discount;

import com.ecomflow.interfaces.Discountable;

public class PercentageDiscount implements Discountable {
    private double percentage;

    public PercentageDiscount(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100. Received: " + percentage);
        }
        this.percentage = percentage;
    }

    @Override
    public double calculateDiscount(double amount) {
        if (amount <= 0) return 0.0;
        return amount * (percentage / 100.0);
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100.");
        }
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return String.format("%.1f%% Off", percentage);
    }
}
