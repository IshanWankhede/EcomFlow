package com.ecomflow.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ecomflow.model.Category;
import com.ecomflow.model.Order;
import com.ecomflow.model.Product;
import com.ecomflow.model.User;

/**
 * In-memory repository centralizing all application data structures using Java Collections.
 * Pure data storage layer with no business logic or custom exceptions.
 */
public class DataStore {
    private final Map<String, User> usersByEmail;
    private final Map<Integer, Product> productsById;
    private final List<Order> orderHistory;
    private final Map<Integer, Category> categoriesById;
    private final Set<String> usedCategoryNames;
    private final Set<String> usedEmails;

    public DataStore() {
        this.usersByEmail = new HashMap<>();
        this.productsById = new HashMap<>();
        this.orderHistory = new ArrayList<>();
        this.categoriesById = new HashMap<>();
        this.usedCategoryNames = new HashSet<>();
        this.usedEmails = new HashSet<>();
    }

    // ==================== User Operations ====================

    public boolean addUser(User user) {
        if (user == null || user.getEmail() == null) {
            return false;
        }
        String key = user.getEmail().toLowerCase().trim();
        usersByEmail.put(key, user);
        usedEmails.add(key);
        return true;
    }

    public User getUserByEmail(String email) {
        if (email == null) return null;
        return usersByEmail.get(email.toLowerCase().trim());
    }

    public boolean isEmailTaken(String email) {
        if (email == null) return false;
        return usedEmails.contains(email.toLowerCase().trim());
    }

    public boolean removeUser(String email) {
        if (email == null) return false;
        String key = email.toLowerCase().trim();
        usedEmails.remove(key);
        return usersByEmail.remove(key) != null;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(usersByEmail.values());
    }

    // ==================== Product Operations ====================

    public boolean addProduct(Product product) {
        if (product == null) return false;
        productsById.put(product.getProductId(), product);
        return true;
    }

    public Product getProductById(int productId) {
        return productsById.get(productId);
    }

    public boolean removeProduct(int productId) {
        return productsById.remove(productId) != null;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(productsById.values());
    }

    // ==================== Category Operations ====================

    public boolean addCategory(Category category) {
        if (category == null || category.getName() == null) return false;
        String key = category.getName().toLowerCase().trim();
        categoriesById.put(category.getCategoryId(), category);
        usedCategoryNames.add(key);
        return true;
    }

    public Category getCategoryById(int categoryId) {
        return categoriesById.get(categoryId);
    }

    public boolean isCategoryNameTaken(String name) {
        if (name == null) return false;
        return usedCategoryNames.contains(name.toLowerCase().trim());
    }

    public List<Category> getAllCategories() {
        return new ArrayList<>(categoriesById.values());
    }

    public boolean removeCategory(int categoryId) {
        Category cat = categoriesById.remove(categoryId);
        if (cat != null && cat.getName() != null) {
            usedCategoryNames.remove(cat.getName().toLowerCase().trim());
            return true;
        }
        return false;
    }

    // ==================== Order Operations ====================

    public boolean addOrder(Order order) {
        if (order == null) return false;
        return orderHistory.add(order);
    }

    public Order getOrderById(int orderId) {
        for (Order order : orderHistory) {
            if (order.getOrderId() == orderId) {
                return order;
            }
        }
        return null;
    }

    public List<Order> getAllOrders() {
        return Collections.unmodifiableList(orderHistory);
    }

    public List<Order> getOrdersByCustomer(int customerId) {
        List<Order> result = new ArrayList<>();
        for (Order order : orderHistory) {
            if (order.getCustomer() != null && order.getCustomer().getUserId() == customerId) {
                result.add(order);
            }
        }
        return result;
    }

    // ==================== Utility ====================

    public void clearAll() {
        usersByEmail.clear();
        productsById.clear();
        orderHistory.clear();
        categoriesById.clear();
        usedCategoryNames.clear();
        usedEmails.clear();
    }
}
