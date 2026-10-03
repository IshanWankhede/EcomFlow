package com.ecomflow;

import java.util.ArrayList;
import java.util.List;

import com.ecomflow.discount.FlatDiscount;
import com.ecomflow.discount.NoDiscount;
import com.ecomflow.discount.PercentageDiscount;
import com.ecomflow.interfaces.Discountable;
import com.ecomflow.interfaces.Payment;
import com.ecomflow.payment.CardPayment;
import com.ecomflow.payment.CashOnDelivery;
import com.ecomflow.payment.UPIPayment;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  EcomFlow — Smart E-Commerce Management System  ");
        System.out.println("=================================================");
        System.out.println();

        // Phase 5 Test: Payment Strategy Polymorphism & Downcasting
        System.out.println("--- Phase 5 Test: Payment Strategy Polymorphism & Downcasting ---");
        List<Payment> paymentMethods = new ArrayList<>();
        paymentMethods.add(new UPIPayment("alice@okaxis"));
        paymentMethods.add(new CardPayment("4532789012345678", "Alice Johnson", "CREDIT", "12/28"));
        paymentMethods.add(new CashOnDelivery());

        double sampleAmount = 1499.50;

        for (Payment payment : paymentMethods) {
            // Polymorphic dispatch via Payment interface
            payment.pay(sampleAmount);

            // Downcasting pattern matching: accessing subtype-specific method on CardPayment
            if (payment instanceof CardPayment cp) {
                cp.showCardSpecificDetails();
            }
            System.out.println();
        }

        // Phase 5 Test: Discount Strategy Polymorphism
        System.out.println("--- Phase 5 Test: Discount Strategy Polymorphism ---");
        List<Discountable> discountStrategies = new ArrayList<>();
        discountStrategies.add(new PercentageDiscount(15.0)); // 15% off
        discountStrategies.add(new FlatDiscount(250.0));       // ₹250 flat off
        discountStrategies.add(new NoDiscount());             // 0 off

        double orderSubtotal = 2000.00;
        System.out.printf("Base Order Amount: ₹%.2f%n", orderSubtotal);
        System.out.println("-------------------------------------------------");

        for (Discountable discount : discountStrategies) {
            double discountAmt = discount.calculateDiscount(orderSubtotal);
            double finalPrice = orderSubtotal - discountAmt;
            System.out.printf("Strategy: %-20s | Discount: -₹%-7.2f | Final: ₹%.2f%n",
                    discount.toString(), discountAmt, finalPrice);
        }
    }
}
