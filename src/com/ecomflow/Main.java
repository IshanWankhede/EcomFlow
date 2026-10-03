package com.ecomflow;

import com.ecomflow.model.Address;
import com.ecomflow.model.Category;
import com.ecomflow.model.Customer;
import com.ecomflow.model.Electronics;
import com.ecomflow.model.Product;
import com.ecomflow.model.User;
import com.ecomflow.repository.DataStore;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  EcomFlow — Smart E-Commerce Management System  ");
        System.out.println("=================================================");
        System.out.println();

        // Phase 6 Test: In-Memory DataStore CRUD Operations
        System.out.println("--- Phase 6 Test: Repository Layer (DataStore) ---");
        DataStore dataStore = new DataStore();

        // 1. Add Category & Product
        Category gadgets = new Category(101, "Smart Gadgets");
        dataStore.addCategory(gadgets);

        Product smartwatch = new Electronics("Ultra Smartwatch Pro", 249.99, 15, gadgets, "TechTime", 18);
        dataStore.addProduct(smartwatch);

        // 2. Add Customer
        Address address = new Address("742 Evergreen Terrace", "Springfield", "OR", "97477", "USA");
        Customer customer = new Customer("Homer Simpson", "homer@simpson.org", "donut123", "555-7334", address);
        dataStore.addUser(customer);

        // 3. Retrieve User by Email key (O(1) lookup)
        User retrievedUser = dataStore.getUserByEmail("homer@simpson.org");
        System.out.println("[DataStore Lookup] Retrieved User by email 'homer@simpson.org':");
        if (retrievedUser != null) {
            retrievedUser.displayProfile();
        } else {
            System.out.println("User not found!");
        }
        System.out.println();

        // 4. Retrieve Product by ID key (O(1) lookup)
        Product retrievedProduct = dataStore.getProductById(smartwatch.getProductId());
        System.out.println("[DataStore Lookup] Retrieved Product by ID #" + smartwatch.getProductId() + ":");
        if (retrievedProduct != null) {
            retrievedProduct.displayDetails();
        } else {
            System.out.println("Product not found!");
        }

        // 5. Check Category uniqueness check
        System.out.println();
        System.out.println("Is 'Smart Gadgets' category registered? " + dataStore.isCategoryNameTaken("smart gadgets"));
        System.out.println("Is 'Furniture' category registered? " + dataStore.isCategoryNameTaken("furniture"));
    }
}
