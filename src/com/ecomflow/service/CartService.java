package com.ecomflow.service;

import com.ecomflow.exceptions.EmptyCartException;
import com.ecomflow.model.Cart;
import com.ecomflow.model.Product;

public class CartService {

    public void addItem(Cart cart, Product product, int quantity) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null.");
        }
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (product.getStock() <= 0) {
            throw new IllegalArgumentException("Cannot add '" + product.getName() + "' to cart: product is out of stock.");
        }
        cart.addItem(product, quantity);
    }

    public boolean removeItem(Cart cart, int productId) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null.");
        }
        return cart.removeItem(productId);
    }

    public void updateQuantity(Cart cart, int productId, int newQuantity) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null.");
        }
        cart.updateQuantity(productId, newQuantity);
    }

    public double getTotal(Cart cart) {
        if (cart == null) {
            return 0.0;
        }
        return cart.calculateTotal();
    }

    public void clearCart(Cart cart) {
        if (cart != null) {
            cart.clear();
        }
    }

    /**
     * Validates that the cart is ready for checkout.
     *
     * @param cart the cart to validate
     * @throws EmptyCartException if the cart is null or contains no items
     */
    public void validateNotEmpty(Cart cart) throws EmptyCartException {
        if (cart == null || cart.isEmpty()) {
            throw new EmptyCartException("Checkout cannot proceed: Shopping cart is empty.");
        }
    }
}
