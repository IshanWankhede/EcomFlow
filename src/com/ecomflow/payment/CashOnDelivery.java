package com.ecomflow.payment;

import com.ecomflow.interfaces.Payment;

public class CashOnDelivery implements Payment {
    private boolean cashCollected;

    public CashOnDelivery() {
        this.cashCollected = false;
    }

    @Override
    public void pay(double amount) {
        this.cashCollected = true;
        System.out.printf("[Cash On Delivery] Order placed with COD. ₹%.2f to be collected upon doorstep delivery.%n",
                amount);
    }

    @Override
    public void refund(double amount) {
        if (cashCollected) {
            System.out.printf("[Cash On Delivery Refund] Cash refund of ₹%.2f initiated via delivery agent or bank transfer.%n",
                    amount);
        } else {
            System.out.println("[Cash On Delivery] Order cancelled prior to delivery. No physical cash was collected.");
        }
    }

    public boolean isCashCollected() {
        return cashCollected;
    }
}
