package com.ecomflow.service;

import java.util.HashMap;
import java.util.Map;

import com.ecomflow.discount.FlatDiscount;
import com.ecomflow.discount.NoDiscount;
import com.ecomflow.discount.PercentageDiscount;
import com.ecomflow.interfaces.Discountable;

public class DiscountService {
    private final Map<String, Discountable> promoCodes;

    public DiscountService() {
        this.promoCodes = new HashMap<>();
        initializeDefaultCoupons();
    }

    private void initializeDefaultCoupons() {
        promoCodes.put("SAVE10", new PercentageDiscount(10.0));
        promoCodes.put("SAVE20", new PercentageDiscount(20.0));
        promoCodes.put("FLAT50", new FlatDiscount(50.0));
        promoCodes.put("FLAT100", new FlatDiscount(100.0));
    }

    public void registerDiscount(String couponCode, Discountable discount) {
        if (couponCode != null && !couponCode.trim().isEmpty() && discount != null) {
            promoCodes.put(couponCode.toUpperCase().trim(), discount);
        }
    }

    /**
     * Resolves a coupon code to its corresponding Discountable strategy.
     * Returns NoDiscount if the code is blank, null, or unrecognized.
     */
    public Discountable resolveDiscount(String couponCode) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return new NoDiscount();
        }
        Discountable discount = promoCodes.get(couponCode.toUpperCase().trim());
        return (discount != null) ? discount : new NoDiscount();
    }

    public double calculateDiscountAmount(String couponCode, double subtotal) {
        Discountable strategy = resolveDiscount(couponCode);
        return strategy.calculateDiscount(subtotal);
    }
}
