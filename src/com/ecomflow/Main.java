package com.ecomflow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ecomflow.model.Address;
import com.ecomflow.model.Cart;
import com.ecomflow.model.Category;
import com.ecomflow.model.Clothing;
import com.ecomflow.model.Customer;
import com.ecomflow.model.Electronics;
import com.ecomflow.model.Grocery;
import com.ecomflow.model.Invoice;
import com.ecomflow.model.Order;
import com.ecomflow.model.OrderItem;
import com.ecomflow.model.Product;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  EcomFlow — Smart E-Commerce Management System  ");
        System.out.println("=================================================");
        System.out.println();

        // Setup Categories and Customer
        Category electronicsCategory = new Category(1, "Electronics");
        Category clothingCategory = new Category(2, "Apparel");
        Category groceryCategory = new Category(3, "Groceries");

        Address address = new Address("42 Tech Boulevard", "Bengaluru", "Karnataka", "560001", "India");
        Customer customer = new Customer("Alice Johnson", "alice@example.com", "pass123", "+91 9876543210", address);

        // Create Real Products
        Product phone = new Electronics("Smartphone X", 799.99, 10, electronicsCategory, "TechCorp", 24);
        Product shirt = new Clothing("Cotton Oxford Shirt", 49.99, 40, clothingCategory, "M", "100% Cotton");
        Product milk = new Grocery("Organic Whole Milk", 3.50, 25, groceryCategory, LocalDate.now().plusDays(10));

        // 1. Build Cart with live product references
        Cart cart = customer.getCart();
        cart.addItem(phone, 1);
        cart.addItem(shirt, 2);
        cart.addItem(milk, 4);

        System.out.println("--- Phase 4 Test: Live Cart vs Frozen Order Snapshot ---");
        System.out.printf("Initial Cart Total (Live): $%.2f%n", cart.calculateTotal());

        // 2. Simulate Checkout: Create Order with snapshot OrderItems
        List<OrderItem> orderItems = new ArrayList<>();
        for (var cartItem : cart.getItems()) {
            orderItems.add(new OrderItem(cartItem.getProduct(), cartItem.getQuantity()));
        }
        Order placedOrder = new Order(customer, orderItems);
        customer.addOrderToHistory(placedOrder);

        System.out.printf("Placed Order Total (Snapshot): $%.2f%n", placedOrder.getTotal());
        System.out.println();

        // 3. Price change occurs on live products in catalog
        System.out.println(">>> Changing live product prices in the catalog...");
        phone.setPrice(999.99); // Price increased from $799.99 to $999.99
        shirt.setPrice(29.99);  // Price discounted from $49.99 to $29.99
        System.out.println(">>> New Smartphone X Price: $999.99 (was $799.99)");
        System.out.println(">>> New Cotton Oxford Shirt Price: $29.99 (was $49.99)");
        System.out.println();

        // 4. Verify live cart reflects new prices, while historical order remains frozen
        System.out.printf("Re-calculated Cart Total (Live Reference) : $%.2f%n", cart.calculateTotal());
        System.out.printf("Historical Order Total (Frozen Snapshot)  : $%.2f%n", placedOrder.getTotal());
        System.out.println();

        // 5. Generate and Print Invoice
        Invoice invoice = new Invoice(placedOrder);
        invoice.printInvoice();
    }
}
