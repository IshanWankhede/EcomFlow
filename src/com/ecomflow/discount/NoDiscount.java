package com.ecomflow.discount;

import com.ecomflow.interfaces.Discountable;

public class NoDiscount implements Discountable {

    @Override
    public double calculateDiscount(double amount) {
        return 0.0;
    }

    @Override
    public String toString() {
        return "No Discount (0.00)";
    }
}
