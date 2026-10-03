package com.ecomflow.service;

import com.ecomflow.exceptions.InvalidLoginException;
import com.ecomflow.model.Address;
import com.ecomflow.model.Admin;
import com.ecomflow.model.Customer;
import com.ecomflow.model.User;
import com.ecomflow.repository.DataStore;

public class AuthenticationService {
    private final DataStore dataStore;
    private User currentUser;

    public AuthenticationService(DataStore dataStore) {
        if (dataStore == null) {
            throw new IllegalArgumentException("DataStore cannot be null.");
        }
        this.dataStore = dataStore;
    }

    public Customer registerCustomer(String name, String email, String password, String phone, Address address) {
        validateCredentials(name, email, password);
        Customer customer = new Customer(name, email, password, phone, address);
        dataStore.addUser(customer);
        return customer;
    }

    public Admin registerAdmin(String name, String email, String password, String phone) {
        validateCredentials(name, email, password);
        Admin admin = new Admin(name, email, password, phone);
        dataStore.addUser(admin);
        return admin;
    }

    public User login(String email, String password) throws InvalidLoginException {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new InvalidLoginException("Login failed: Email and password must not be empty.");
        }

        User user = dataStore.getUserByEmail(email.trim());
        if (user == null || !user.getPassword().equals(password)) {
            throw new InvalidLoginException("Login failed: Invalid email or password for " + email);
        }

        this.currentUser = user;
        return user;
    }

    public void logout() {
        if (currentUser != null) {
            currentUser.logout();
            currentUser = null;
        }
    }

    public void logout(User user) {
        if (user != null) {
            user.logout();
            if (currentUser != null && currentUser.getUserId() == user.getUserId()) {
                currentUser = null;
            }
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private void validateCredentials(String name, String email, String password) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration failed: Name cannot be empty.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration failed: Email cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration failed: Password cannot be empty.");
        }
        if (dataStore.isEmailTaken(email.trim())) {
            throw new IllegalArgumentException("Registration failed: Email '" + email + "' is already registered.");
        }
    }
}
