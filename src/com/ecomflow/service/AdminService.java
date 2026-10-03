package com.ecomflow.service;

import java.util.ArrayList;
import java.util.List;

import com.ecomflow.enums.OrderStatus;
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
}
