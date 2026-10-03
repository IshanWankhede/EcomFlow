package com.ecomflow.model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private Address address;
    private Cart cart;
    private List<Order> orderHistory;

    public Customer(String name, String email, String password, String phone, Address address) {
        super(name, email, password, phone);
        this.address = address;
        this.cart = new Cart(this);
        this.orderHistory = new ArrayList<>();
    }

    public void addOrderToHistory(Order order) {
        if (order != null) {
            orderHistory.add(order);
        }
    }

    @Override
    public void displayProfile() {
        System.out.println("================ [CUSTOMER PROFILE] ================");
        System.out.println("Summary      : " + getProfileSummary()); // protected method from User
        System.out.println("Role         : CUSTOMER");
        System.out.println("Phone        : " + getPhone());
        System.out.println("Address      : " + (address != null ? address.toString() : "Not provided"));
        System.out.println("Cart Items   : " + (cart != null ? cart.getItems().size() : 0));
        System.out.println("Total Orders : " + (orderHistory != null ? orderHistory.size() : 0));
        System.out.println("====================================================");
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public List<Order> getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(List<Order> orderHistory) {
        this.orderHistory = orderHistory;
    }
}
