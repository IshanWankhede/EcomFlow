package com.ecomflow.model;

public class Admin extends User {

    public Admin(String name, String email, String password, String phone) {
        super(name, email, password, phone);
    }

    @Override
    public void displayProfile() {
        System.out.println("================= [ADMIN PROFILE] ==================");
        System.out.println("User ID  : " + getUserId());
        System.out.println("Role     : ADMINISTRATOR");
        System.out.println("Name     : " + getName());
        System.out.println("Email    : " + getEmail());
        System.out.println("Phone    : " + getPhone());
        System.out.println("Access   : Full System & Catalog Management");
        System.out.println("====================================================");
    }
}
