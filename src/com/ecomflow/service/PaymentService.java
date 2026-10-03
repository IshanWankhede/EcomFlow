package com.ecomflow.service;

import com.ecomflow.exceptions.InvalidPaymentException;
import com.ecomflow.interfaces.Payment;
import com.ecomflow.payment.CardPayment;
import com.ecomflow.payment.CashOnDelivery;
import com.ecomflow.payment.UPIPayment;

public class PaymentService {

    /**
     * Resolves a payment method string to a concrete Payment strategy.
     *
     * @param methodName method identifier ("UPI", "CARD", "COD", "CASH_ON_DELIVERY")
     * @return concrete Payment implementation
     * @throws InvalidPaymentException if method name is unrecognized or unsupported
     */
    public Payment resolvePayment(String methodName) throws InvalidPaymentException {
        if (methodName == null || methodName.trim().isEmpty()) {
            throw new InvalidPaymentException("Payment method cannot be empty.");
        }

        String normalized = methodName.trim().toUpperCase();
        switch (normalized) {
            case "UPI":
                return new UPIPayment("customer@ecomflow");
            case "CARD":
            case "CREDIT_CARD":
            case "DEBIT_CARD":
                return new CardPayment("4111222233334444", "Valued Customer", "CREDIT", "12/29");
            case "COD":
            case "CASH_ON_DELIVERY":
                return new CashOnDelivery();
            default:
                throw new InvalidPaymentException("Unsupported payment method: '" + methodName +
                        "'. Supported methods are: UPI, CARD, COD.");
        }
    }

    public Payment createUPIPayment(String upiId) {
        return new UPIPayment(upiId);
    }

    public Payment createCardPayment(String cardNumber, String cardHolder, String cardType, String expiry) {
        return new CardPayment(cardNumber, cardHolder, cardType, expiry);
    }

    public Payment createCODPayment() {
        return new CashOnDelivery();
    }

    public void processPayment(Payment strategy, double amount) {
        if (strategy == null) {
            throw new IllegalArgumentException("Payment strategy cannot be null.");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative.");
        }
        strategy.pay(amount);
    }

    public void processRefund(Payment strategy, double amount) {
        if (strategy == null) {
            throw new IllegalArgumentException("Payment strategy cannot be null.");
        }
        strategy.refund(amount);
    }
}
