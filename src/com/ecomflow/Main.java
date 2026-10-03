package com.ecomflow;

import com.ecomflow.exceptions.EmptyCartException;
import com.ecomflow.exceptions.InvalidPaymentException;
import com.ecomflow.interfaces.Discountable;
import com.ecomflow.interfaces.Payment;
import com.ecomflow.model.Address;
import com.ecomflow.model.Cart;
import com.ecomflow.model.Category;
import com.ecomflow.model.Clothing;
import com.ecomflow.model.Customer;
import com.ecomflow.model.Electronics;
import com.ecomflow.model.Product;
import com.ecomflow.service.CartService;
import com.ecomflow.service.DiscountService;
import com.ecomflow.service.PaymentService;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  EcomFlow — Smart E-Commerce Management System  ");
        System.out.println("=================================================");
        System.out.println();

        System.out.println("--- Phase 8 Test: Cart, Discount, and Payment Services ---");
        CartService cartService = new CartService();
        DiscountService discountService = new DiscountService();
        PaymentService paymentService = new PaymentService();

        // 1. Setup Customer & Catalog
        Address address = new Address("221B Baker Street", "London", "Greater London", "NW1 6XE", "UK");
        Customer customer = new Customer("John Watson", "watson@bakerstreet.com", "sherlock221", "+44 20 7224 3688", address);
        Cart cart = customer.getCart();

        Category electronics = new Category(101, "Electronics");
        Category clothing = new Category(102, "Clothing");

        Product mechanicalKeyboard = new Electronics("RGB Mechanical Keyboard", 129.99, 10, electronics, "Keychron", 12);
        Product hoodie = new Clothing("Cozy Fleece Hoodie", 69.99, 25, clothing, "XL", "Cotton/Poly");

        // 2. Add items to cart via CartService
        System.out.println("[Step 1: Adding Items to Cart via CartService]");
        cartService.addItem(cart, mechanicalKeyboard, 1);
        cartService.addItem(cart, hoodie, 2);

        double cartSubtotal = cartService.getTotal(cart);
        System.out.printf("Cart Subtotal (%d unique items): $%.2f%n", cart.getItems().size(), cartSubtotal);
        System.out.println();

        // 3. Apply Discount via DiscountService
        System.out.println("[Step 2: Resolving & Applying Discount Strategy]");
        String promoCode = "SAVE10";
        Discountable discountStrategy = discountService.resolveDiscount(promoCode);
        double discountAmount = discountStrategy.calculateDiscount(cartSubtotal);
        double payableAmount = cartSubtotal - discountAmount;

        System.out.println("Applied Promo Code  : " + promoCode + " (" + discountStrategy + ")");
        System.out.printf("Discount Deducted   : -$%.2f%n", discountAmount);
        System.out.printf("Final Payable Total : $%.2f%n", payableAmount);
        System.out.println();

        // 4. Resolve & Process Payment via PaymentService
        System.out.println("[Step 3: Resolving & Processing Valid Payment Method]");
        try {
            Payment payment = paymentService.resolvePayment("UPI");
            System.out.println("Resolved Payment Strategy: " + payment.getClass().getSimpleName());
            paymentService.processPayment(payment, payableAmount);
        } catch (InvalidPaymentException e) {
            System.err.println("Payment error: " + e.getMessage());
        }
        System.out.println();

        // 5. Test Invalid Payment Exception Handling
        System.out.println("[Step 4: Testing Invalid Payment Exception Handling]");
        try {
            System.out.println("Attempting payment with unsupported method 'BITCOIN'...");
            paymentService.resolvePayment("BITCOIN");
            System.out.println("This line should never execute!");
        } catch (InvalidPaymentException e) {
            System.out.println(">>> Gracefully caught expected InvalidPaymentException: " + e.getMessage());
        }
        System.out.println();

        // 6. Test Empty Cart Validation
        System.out.println("[Step 5: Testing Empty Cart Validation]");
        Cart emptyCart = new Cart(customer);
        try {
            System.out.println("Validating empty cart for checkout...");
            cartService.validateNotEmpty(emptyCart);
        } catch (EmptyCartException e) {
            System.out.println(">>> Gracefully caught expected EmptyCartException: " + e.getMessage());
        }
    }
}
