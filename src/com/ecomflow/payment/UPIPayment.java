package com.ecomflow.payment;

import com.ecomflow.interfaces.Payment;

public class UPIPayment implements Payment {
    private String upiId;

    public UPIPayment(String upiId) {
        if (upiId == null || !upiId.contains("@")) {
            throw new IllegalArgumentException("Invalid UPI ID format (must contain '@'): " + upiId);
        }
        this.upiId = upiId.trim();
    }

    @Override
    public void pay(double amount) {
        System.out.printf("[UPI Payment] Processing ₹%.2f via Virtual Payment Address (VPA): %s -> Transaction SUCCESS.%n",
                amount, upiId);
    }

    @Override
    public void refund(double amount) {
        System.out.printf("[UPI Refund] Initiating refund of ₹%.2f back to VPA: %s -> Refund CREDITED.%n",
                amount, upiId);
    }

    public String getUpiId() {
        return upiId;
    }
}
