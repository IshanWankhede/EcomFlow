package com.ecomflow.payment;

import com.ecomflow.interfaces.Payment;

public class CardPayment implements Payment {
    private String cardNumber;
    private String cardHolderName;
    private String cardType; // "CREDIT" or "DEBIT"
    private String expiryDate;

    public CardPayment(String cardNumber, String cardHolderName, String cardType, String expiryDate) {
        if (cardNumber == null || cardNumber.trim().length() < 12) {
            throw new IllegalArgumentException("Card number must have at least 12 digits.");
        }
        this.cardNumber = cardNumber.trim();
        this.cardHolderName = (cardHolderName != null) ? cardHolderName.trim() : "";
        this.cardType = (cardType != null) ? cardType.toUpperCase().trim() : "DEBIT";
        this.expiryDate = (expiryDate != null) ? expiryDate.trim() : "";
    }

    @Override
    public void pay(double amount) {
        System.out.printf("[Card Payment] Charging ₹%.2f to %s Card ending in %s -> Transaction SUCCESS.%n",
                amount, cardType, getMaskedCardNumber());
    }

    @Override
    public void refund(double amount) {
        System.out.printf("[Card Refund] Processing reversal of ₹%.2f to %s Card ending in %s -> Refund SETTLED.%n",
                amount, cardType, getMaskedCardNumber());
    }

    /**
     * Specialized method specific to CardPayment (not present on the Payment interface).
     * Demonstrates downcasting and subtype-specific behavior inspection.
     */
    public void showCardSpecificDetails() {
        System.out.println("  >>> [Card Details: Downcasted]") ;
        System.out.println("      Card Type   : " + cardType);
        System.out.println("      Card Holder : " + cardHolderName);
        System.out.println("      Masked No   : " + getMaskedCardNumber());
        System.out.println("      Expiry      : " + expiryDate);
    }

    public String getMaskedCardNumber() {
        if (cardNumber.length() <= 4) {
            return "**** " + cardNumber;
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getCardType() {
        return cardType;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
}
