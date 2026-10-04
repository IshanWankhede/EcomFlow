package com.ecomflow.model;

public class Clothing extends Product {
    private String size;
    private String material;

    public Clothing(String name, double price, int stock, Category category, String size, String material) {
        super(name, price, stock, category);
        setSize(size);
        setMaterial(material);
    }

    public Clothing(String name, double price, int stock, Category category, String size, String material, String imageUrl) {
        super(name, price, stock, category, imageUrl);
        setSize(size);
        setMaterial(material);
    }

    public Clothing(int productId, String name, double price, int stock, Category category, String size, String material) {
        super(productId, name, price, stock, category);
        setSize(size);
        setMaterial(material);
    }

    public Clothing(int productId, String name, double price, int stock, Category category, String size, String material, String imageUrl) {
        super(productId, name, price, stock, category, imageUrl);
        setSize(size);
        setMaterial(material);
    }

    /**
     * Clothing Discount Strategy:
     * High-volume inventory clearance: apparel items with stock levels over 30 units
     * automatically receive a 10% clearance discount to rotate seasonal inventory.
     */
    @Override
    public double calculateDiscount() {
        if (getStock() > 30) {
            return getPrice() * 0.10;
        }
        return 0.0;
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Size         : " + size);
        System.out.println("Material     : " + material);
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = (size != null) ? size.trim() : "";
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = (material != null) ? material.trim() : "";
    }
}
