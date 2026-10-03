package com.ecomflow;

import com.ecomflow.model.Address;
import com.ecomflow.model.Admin;
import com.ecomflow.model.Customer;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  EcomFlow — Smart E-Commerce Management System  ");
        System.out.println("=================================================");
        System.out.println();

        // Phase 2 Scratch Test: Testing User Hierarchy & Polymorphic Display
        System.out.println("--- Phase 2 Test: User Hierarchy Demonstration ---");
        
        Address address = new Address("42 Tech Boulevard", "Bengaluru", "Karnataka", "560001", "India");
        Customer customer = new Customer("Alice Johnson", "alice@example.com", "pass123", "+91 9876543210", address);
        Admin admin = new Admin("Bob Smith", "admin@ecomflow.com", "adminSecret", "+91 9123456780");

        customer.displayProfile();
        System.out.println();
        admin.displayProfile();
    }
}
