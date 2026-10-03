package com.ecomflow.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ecomflow.enums.OrderStatus;

public class Order {
    private static int orderCounter = 7000;

    private int orderId;
    private Customer customer;
    private List<OrderItem> items;
    private double total;
    private OrderStatus status;
    private LocalDateTime placedAt;

    public Order(Customer customer, List<OrderItem> items) {
        this.orderId = ++orderCounter;
        this.customer = customer;
        this.items = (items != null) ? new ArrayList<>(items) : new ArrayList<>();
        this.total = calculateTotal();
        this.status = OrderStatus.PLACED;
        this.placedAt = LocalDateTime.now();
    }

    public Order(int orderId, Customer customer, List<OrderItem> items, double total, OrderStatus status, LocalDateTime placedAt) {
        this.orderId = orderId;
        this.customer = customer;
        this.items = (items != null) ? new ArrayList<>(items) : new ArrayList<>();
        this.total = total;
        this.status = status;
        this.placedAt = placedAt;
    }

    /**
     * Calculates the sum of all snapshot items upon order creation.
     * This total is permanently stored and frozen.
     */
    public double calculateTotal() {
        double sum = 0.0;
        for (OrderItem item : items) {
            sum += item.getSubtotal();
        }
        return sum;
    }

    /**
     * Cancels the order unless it has already been delivered.
     *
     * @throws IllegalStateException if the order is already in DELIVERED state
     */
    public void cancelOrder() {
        if (this.status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel order #" + orderId + " because it has already been DELIVERED.");
        }
        if (this.status == OrderStatus.CANCELLED) {
            System.out.println("Order #" + orderId + " is already CANCELLED.");
            return;
        }
        this.status = OrderStatus.CANCELLED;
        System.out.println("Order #" + orderId + " has been successfully CANCELLED.");
    }

    public int getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public static int getOrderCounter() {
        return orderCounter;
    }
}
