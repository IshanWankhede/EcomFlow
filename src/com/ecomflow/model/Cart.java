package com.ecomflow.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private Customer owner;
    private List<CartItem> items;

    public Cart(Customer owner) {
        this.owner = owner;
        this.items = new ArrayList<>();
    }

    public void addItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Cannot add null product to cart.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        for (CartItem item : items) {
            if (item.getProduct().getProductId() == product.getProductId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(product, quantity));
    }

    public boolean removeItem(int productId) {
        return items.removeIf(item -> item.getProduct().getProductId() == productId);
    }

    public void updateQuantity(int productId, int newQty) {
        if (newQty <= 0) {
            removeItem(productId);
            return;
        }
        for (CartItem item : items) {
            if (item.getProduct().getProductId() == productId) {
                item.setQuantity(newQty);
                return;
            }
        }
    }

    /**
     * Calculates the total dynamically by summing all item subtotals.
     * Reflects live price changes of referenced products.
     */
    public double calculateTotal() {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void clear() {
        items.clear();
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
