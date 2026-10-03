package com.ecomflow.model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private Address address;
    
    // TODO: In Phase 4, replace Object with com.ecomflow.model.Cart
    private Object cart;
    
    // TODO: In Phase 4, replace Object with com.ecomflow.model.Order
    private List<Object> orderHistory;

    public Customer(String name, String email, String password, String phone, Address address) {
        super(name, email, password, phone);
        this.address = address;
        this.orderHistory = new ArrayList<>();
    }

    @Override
    public void displayProfile() {
        System.out.println("================ [CUSTOMER PROFILE] ================");
        System.out.println("User ID      : " + getUserId());
        System.out.println("Role         : CUSTOMER");
        System.out.println("Name         : " + getName());
        System.out.println("Email        : " + getEmail());
        System.out.println("Phone        : " + getPhone());
        System.out.println("Address      : " + (address != null ? address.toString() : "Not provided"));
        System.out.println("Total Orders : " + (orderHistory != null ? orderHistory.size() : 0));
        System.out.println("====================================================");
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Object getCart() {
        return cart;
    }

    public void setCart(Object cart) {
        this.cart = cart;
    }

    public List<Object> getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(List<Object> orderHistory) {
        this.orderHistory = orderHistory;
    }
}
