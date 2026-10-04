package com.ecomflow.service;

import java.util.ArrayList;
import java.util.List;

import com.ecomflow.enums.OrderStatus;
import com.ecomflow.exceptions.CustomerNotFoundException;
import com.ecomflow.exceptions.ProductNotFoundException;
import com.ecomflow.model.Customer;
import com.ecomflow.model.Order;
import com.ecomflow.model.Product;
import com.ecomflow.model.User;
import com.ecomflow.repository.DataStore;

public class AdminService {
    private final DataStore dataStore;
    private final OrderService orderService;
    private final InventoryService inventoryService;

    public AdminService(DataStore dataStore, OrderService orderService, InventoryService inventoryService) {
        if (dataStore == null || orderService == null || inventoryService == null) {
            throw new IllegalArgumentException("Dependencies for AdminService cannot be null.");
        }
        this.dataStore = dataStore;
        this.orderService = orderService;
        this.inventoryService = inventoryService;
    }

    public List<Order> listAllOrders() {
        return dataStore.getAllOrders();
    }

    public List<Customer> listAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        for (User user : dataStore.getAllUsers()) {
            if (user instanceof Customer customer) {
                customers.add(customer);
            }
        }
        return customers;
    }

    public List<Product> listAllProducts() {
        return dataStore.getAllProducts();
    }

    public void updateOrderStatus(int orderId, OrderStatus status) {
        orderService.updateOrderStatus(orderId, status);
    }

    public void restockProduct(int productId, int quantity) throws ProductNotFoundException {
        inventoryService.addStock(productId, quantity);
    }

    /**
     * Removes a customer account from the system.
     *
     * <p>Order history is intentionally PRESERVED: all orders placed by this customer
     * remain in DataStore so the admin's order records stay complete. Only the
     * Customer/User account entry itself is deleted (removed from usersByEmail and
     * the usedEmails set in DataStore).</p>
     *
     * @param email the email address of the customer to delete
     * @throws CustomerNotFoundException if no customer with that email exists,
     *                                   or if the email belongs to a non-Customer user (e.g. Admin)
     */
    public void deleteCustomer(String email) throws CustomerNotFoundException {
        if (email == null || email.trim().isEmpty()) {
            throw new CustomerNotFoundException("Cannot delete customer: email must not be blank.");
        }
        User user = dataStore.getUserByEmail(email.trim());
        if (user == null) {
            throw new CustomerNotFoundException(
                    "Cannot delete account: no user found with email '" + email + "'.");
        }
        if (!(user instanceof Customer)) {
            throw new CustomerNotFoundException(
                    "Cannot delete account with email '" + email + "': target is not a Customer account.");
        }
        dataStore.removeUser(email.trim());
    }
}
