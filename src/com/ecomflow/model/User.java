package com.ecomflow.model;

public abstract class User {
    private static int userCounter = 1000;

    private int userId;
    private String name;
    private String email;
    private String password;
    private String phone;

    public User(String name, String email, String password, String phone) {
        this.userId = ++userCounter;
        setName(name);
        setEmail(email);
        setPassword(password);
        setPhone(phone);
    }

    public boolean login(String enteredEmail, String enteredPassword) {
        if (enteredEmail != null && enteredPassword != null
                && this.email.equalsIgnoreCase(enteredEmail.trim())
                && this.password.equals(enteredPassword)) {
            System.out.println("User " + name + " (" + email + ") logged in successfully.");
            return true;
        }
        System.out.println("Login failed for " + enteredEmail);
        return false;
    }

    public void logout() {
        System.out.println("User " + name + " (" + email + ") logged out.");
    }

    public void displayProfile() {
        System.out.println("----------------------------------------");
        System.out.println("User ID  : " + userId);
        System.out.println("Name     : " + name);
        System.out.println("Email    : " + email);
        System.out.println("Phone    : " + phone);
        System.out.println("----------------------------------------");
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        this.email = email.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = (phone != null) ? phone.trim() : "";
    }

    public static int getUserCounter() {
        return userCounter;
    }
}
