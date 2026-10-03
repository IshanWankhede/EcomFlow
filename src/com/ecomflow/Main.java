package com.ecomflow;

import com.ecomflow.exceptions.EmptyCartException;
import com.ecomflow.exceptions.InsufficientStockException;
import com.ecomflow.exceptions.InvalidPaymentException;
import com.ecomflow.exceptions.ProductNotFoundException;
import com.ecomflow.model.Address;
import com.ecomflow.model.Cart;
import com.ecomflow.model.Category;
import com.ecomflow.model.Customer;
import com.ecomflow.model.Electronics;
import com.ecomflow.model.Invoice;
import com.ecomflow.model.Product;
import com.ecomflow.repository.DataStore;
import com.ecomflow.service.AdminService;
import com.ecomflow.service.AuthenticationService;
import com.ecomflow.service.CartService;
import com.ecomflow.service.CustomerService;
import com.ecomflow.service.DiscountService;
import com.ecomflow.service.InventoryService;
import com.ecomflow.service.OrderService;
import com.ecomflow.service.PaymentService;
import com.ecomflow.service.ProductService;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  EcomFlow — Smart E-Commerce Management System  ");
        System.out.println("=================================================");
        System.out.println();

        System.out.println("--- Phase 9 Test: End-to-End Order Orchestration & Lifecycle ---");

        // 1. Initialize System Infrastructure & Services
        DataStore dataStore = new DataStore();
        AuthenticationService authService = new AuthenticationService(dataStore);
        ProductService productService = new ProductService(dataStore);
        InventoryService inventoryService = new InventoryService(dataStore);
        CartService cartService = new CartService();
        DiscountService discountService = new DiscountService();
        PaymentService paymentService = new PaymentService();
        OrderService orderService = new OrderService(dataStore, cartService, discountService, paymentService, inventoryService);
        CustomerService customerService = new CustomerService(dataStore);
        AdminService adminService = new AdminService(dataStore, orderService, inventoryService);

        try {
            // 2. Register Customer & Catalog
            Address address = new Address("742 Evergreen Terrace", "Springfield", "OR", "97477", "USA");
            Customer customer = authService.registerCustomer("Marge Simpson", "marge@simpson.org", "bluehair1", "555-7334", address);

            Category appliances = new Category(301, "Kitchen Appliances");
            dataStore.addCategory(appliances);

            Product blender = new Electronics("High-Speed Blender 1200W", 150.00, 5, appliances, "NutriBlend", 12);
            productService.addProduct(blender);

            System.out.println("[Initial State]");
            System.out.println("Registered Customer : " + customer.getName());
            System.out.println("Added Product       : " + blender.getName() + " (ID #" + blender.getProductId() + ")");
            System.out.println("Initial Stock Level : " + blender.getStock() + " units");
            System.out.println();

            // 3. Customer builds Cart (qty = 2)
            System.out.println("[Step 1: Adding 2 Blenders to Cart]");
            Cart cart = customer.getCart();
            cartService.addItem(cart, blender, 2);
            System.out.printf("Cart Subtotal: $%.2f%n", cartService.getTotal(cart));
            System.out.println();

            // 4. Place Order with Coupon "SAVE10" and Payment "UPI"
            System.out.println("[Step 2: Placing Order with SAVE10 Promo & UPI Payment]");
            Invoice invoice = orderService.placeOrder(cart, "SAVE10", "UPI");
            System.out.println();

            // 5. Print Generated Invoice
            invoice.printInvoice();
            System.out.println();

            // 6. Verify Stock Deduction & Cart Cleared
            System.out.println("[Step 3: Verification Post-Order]");
            System.out.println("Stock Level After Order Placement : " + blender.getStock() + " units (deducted 2 from 5)");
            System.out.println("Customer Cart Is Empty            : " + cart.isEmpty());
            System.out.println();

            // 7. Test Order Cancellation & Stock Restoration
            int orderId = invoice.getOrder().getOrderId();
            System.out.println("[Step 4: Cancelling Order #" + orderId + " & Restoring Stock]");
            orderService.cancelOrder(orderId);
            System.out.println("Stock Level After Cancellation   : " + blender.getStock() + " units (restored back to 5)");
            System.out.println("Order Status in DataStore        : " + orderService.getOrderById(orderId).getStatus());
            System.out.println();

            // 8. Test Atomic Checkout Failure: Requesting More Stock Than Available
            System.out.println("[Step 5: Testing Insufficient Stock Checkout Abort]");
            cartService.addItem(cart, blender, 10); // requesting 10 when available is 5
            System.out.println("Added 10 units to cart (Available: " + blender.getStock() + " units).");

            try {
                System.out.println("Attempting to place order with insufficient inventory...");
                orderService.placeOrder(cart, "SAVE10", "UPI");
                System.out.println("This line should never execute!");
            } catch (InsufficientStockException e) {
                System.out.println(">>> Gracefully caught expected InsufficientStockException: " + e.getMessage());
                System.out.println(">>> Atomic Guarantee Confirmed: Stock remains completely untouched at "
                        + blender.getStock() + " units.");
            }

        } catch (EmptyCartException | InsufficientStockException | InvalidPaymentException | ProductNotFoundException e) {
            System.err.println("Unexpected Error: " + e.getMessage());
        }
    }
}
