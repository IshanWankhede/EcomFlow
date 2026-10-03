package com.ecomflow.service;

import java.util.List;

import com.ecomflow.model.Address;
import com.ecomflow.model.Cart;
import com.ecomflow.model.Customer;
import com.ecomflow.model.Order;
import com.ecomflow.repository.DataStore;

public class CustomerService {
    private final DataStore dataStore;

    public CustomerService(DataStore dataStore) {
        if (dataStore == null) {
            throw new IllegalArgumentException("DataStore cannot be null.");
        }
        this.dataStore = dataStore;
    }

    public List<Order> getOrderHistory(Customer customer) {
        if (customer == null) {
            return List.of();
        }
        return dataStore.getOrdersByCustomer(customer.getUserId());
    }

    public Cart getCart(Customer customer) {
        if (customer == null) {
            return null;
        }
        return customer.getCart();
    }

    public void updateAddress(Customer customer, Address newAddress) {
        if (customer != null && newAddress != null) {
            customer.setAddress(newAddress);
        }
    }
}
