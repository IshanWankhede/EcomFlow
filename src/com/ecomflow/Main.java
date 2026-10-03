package com.ecomflow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ecomflow.model.Address;
import com.ecomflow.model.Admin;
import com.ecomflow.model.Category;
import com.ecomflow.model.Clothing;
import com.ecomflow.model.Customer;
import com.ecomflow.model.Electronics;
import com.ecomflow.model.Grocery;
import com.ecomflow.model.Product;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  EcomFlow — Smart E-Commerce Management System  ");
        System.out.println("=================================================");
        System.out.println();

        // Phase 2 User Hierarchy Demonstration
        System.out.println("--- Phase 2 Test: User Hierarchy Demonstration ---");
        Address address = new Address("42 Tech Boulevard", "Bengaluru", "Karnataka", "560001", "India");
        Customer customer = new Customer("Alice Johnson", "alice@example.com", "pass123", "+91 9876543210", address);
        Admin admin = new Admin("Bob Smith", "admin@ecomflow.com", "adminSecret", "+91 9123456780");

        customer.displayProfile();
        System.out.println();
        admin.displayProfile();
        System.out.println();

        // Phase 3 Product Hierarchy & Polymorphism Demonstration
        System.out.println("--- Phase 3 Test: Product Hierarchy & Polymorphic Discounts ---");
        Category electronicsCategory = new Category(1, "Electronics");
        Category clothingCategory = new Category(2, "Apparel");
        Category groceryCategory = new Category(3, "Groceries");

        List<Product> catalog = new ArrayList<>();
        catalog.add(new Electronics("Noise-Cancelling Headphones", 199.99, 20, electronicsCategory, "SoundMax", 24));
        catalog.add(new Clothing("Winter Fleece Jacket", 59.99, 45, clothingCategory, "L", "Polyester"));
        catalog.add(new Grocery("Organic Almond Milk", 4.99, 15, groceryCategory, LocalDate.now().plusDays(5)));

        for (Product product : catalog) {
            // Polymorphic invocations through Product base reference
            product.displayDetails();
            System.out.printf("Polymorphic discount calculation -> $%.2f%n", product.calculateDiscount());
            System.out.println();
        }
    }
}
