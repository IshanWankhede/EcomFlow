package com.ecomflow.model;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product in CartItem cannot be null.");
        }
        this.product = product;
        setQuantity(quantity);
    }

    /**
     * Calculates the subtotal dynamically based on the live product price.
     * Any price updates on the Product object are reflected immediately.
     */
    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product in CartItem cannot be null.");
        }
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("CartItem quantity must be greater than 0. Received: " + quantity);
        }
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("%s x %d = $%.2f", product.getName(), quantity, getSubtotal());
    }
}
