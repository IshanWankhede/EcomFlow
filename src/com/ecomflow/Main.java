package com.ecomflow;

import com.ecomflow.exceptions.InsufficientStockException;
import com.ecomflow.exceptions.InvalidLoginException;
import com.ecomflow.exceptions.ProductNotFoundException;
import com.ecomflow.model.Address;
import com.ecomflow.model.Category;
import com.ecomflow.model.Customer;
import com.ecomflow.model.Electronics;
import com.ecomflow.model.Product;
import com.ecomflow.model.User;
import com.ecomflow.repository.DataStore;
import com.ecomflow.service.AuthenticationService;
import com.ecomflow.service.InventoryService;
import com.ecomflow.service.ProductService;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  EcomFlow — Smart E-Commerce Management System  ");
        System.out.println("=================================================");
        System.out.println();

        System.out.println("--- Phase 7 Test: Service Layer (Identity, Catalog, Inventory) ---");
        DataStore dataStore = new DataStore();
        AuthenticationService authService = new AuthenticationService(dataStore);
        ProductService productService = new ProductService(dataStore);
        InventoryService inventoryService = new InventoryService(dataStore);

        // 1. Register Users via AuthenticationService
        System.out.println("[Step 1: User Registration]");
        Address address = new Address("10 Downing Street", "London", "Greater London", "SW1A 2AA", "UK");
        Customer customer = authService.registerCustomer("Diana Prince", "diana@amazon.com", "lasso123", "+44 20 7946 0991", address);
        System.out.println("Registered Customer: " + customer.getName() + " (" + customer.getEmail() + ")");

        User admin = authService.registerAdmin("Bruce Wayne", "admin@waynecorp.com", "batmanPass", "+1 555 0199");
        System.out.println("Registered Admin: " + admin.getName() + " (" + admin.getEmail() + ")");
        System.out.println();

        // 2. Login as Customer & Admin
        System.out.println("[Step 2: Authentication & Login Verification]");
        try {
            User loggedCustomer = authService.login("diana@amazon.com", "lasso123");
            System.out.println("Customer login successful! Logged in as: " + loggedCustomer.getName());

            User loggedAdmin = authService.login("admin@waynecorp.com", "batmanPass");
            System.out.println("Admin login successful! Logged in as: " + loggedAdmin.getName());
        } catch (InvalidLoginException e) {
            System.err.println("Caught Login Error: " + e.getMessage());
        }

        // Test Invalid Login
        try {
            System.out.println("Attempting invalid login with wrong password...");
            authService.login("diana@amazon.com", "wrongPassword");
        } catch (InvalidLoginException e) {
            System.out.println(">>> Gracefully caught expected InvalidLoginException: " + e.getMessage());
        }
        System.out.println();

        // 3. Add Product via ProductService
        System.out.println("[Step 3: Catalog Management via ProductService]");
        Category electronics = new Category(201, "Electronics");
        dataStore.addCategory(electronics);

        Product gamingLaptop = new Electronics("Pro Gaming Laptop 16\"", 1499.00, 5, electronics, "ApexTech", 24);
        productService.addProduct(gamingLaptop);
        System.out.printf("Added '%s' with initial stock of %d units.%n", gamingLaptop.getName(), gamingLaptop.getStock());
        System.out.println();

        // 4. Test Inventory Management and InsufficientStockException
        System.out.println("[Step 4: Inventory Deduction & InsufficientStockException Check]");
        try {
            System.out.println("Attempting legitimate deduction of 2 units...");
            inventoryService.removeStock(gamingLaptop.getProductId(), 2);
            System.out.println("Deduction successful. Remaining stock: " + gamingLaptop.getStock() + " units.");

            System.out.println("Attempting illegal deduction of 10 units (exceeding available 3 units)...");
            inventoryService.removeStock(gamingLaptop.getProductId(), 10);
            System.out.println("This line should never execute!");
        } catch (ProductNotFoundException e) {
            System.err.println("Product lookup error: " + e.getMessage());
        } catch (InsufficientStockException e) {
            System.out.println(">>> Gracefully caught expected InsufficientStockException: " + e.getMessage());
            System.out.println(">>> Confirmed: Stock remains safely unchanged at: " + gamingLaptop.getStock() + " units.");
        }
    }
}
