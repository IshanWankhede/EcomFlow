package com.ecomflow.model;

public class OrderItem {
    private int productId;
    private String productName;
    private double priceAtOrderTime;
    private int quantity;

    public OrderItem(int productId, String productName, double priceAtOrderTime, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.priceAtOrderTime = priceAtOrderTime;
        setQuantity(quantity);
    }

    /**
     * Snapshot constructor: extracts and copies state from a live Product and quantity.
     * Guarantees that future changes to the live product don't affect this historical item.
     */
    public OrderItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null when creating OrderItem.");
        }
        this.productId = product.getProductId();
        this.productName = product.getName();
        this.priceAtOrderTime = product.getPrice();
        setQuantity(quantity);
    }

    public double getSubtotal() {
        return priceAtOrderTime * quantity;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPriceAtOrderTime() {
        return priceAtOrderTime;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("OrderItem quantity must be greater than 0. Received: " + quantity);
        }
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("%s (ID: %d) x %d @ $%.2f = $%.2f",
                productName, productId, quantity, priceAtOrderTime, getSubtotal());
    }
}
