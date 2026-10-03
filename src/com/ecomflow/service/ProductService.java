package com.ecomflow.service;

import java.util.ArrayList;
import java.util.List;

import com.ecomflow.exceptions.ProductNotFoundException;
import com.ecomflow.model.Product;
import com.ecomflow.repository.DataStore;

public class ProductService {
    private final DataStore dataStore;

    public ProductService(DataStore dataStore) {
        if (dataStore == null) {
            throw new IllegalArgumentException("DataStore cannot be null.");
        }
        this.dataStore = dataStore;
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Cannot add null product.");
        }
        dataStore.addProduct(product);
    }

    public Product getProductById(int productId) throws ProductNotFoundException {
        Product product = dataStore.getProductById(productId);
        if (product == null) {
            throw new ProductNotFoundException("Product with ID #" + productId + " was not found in catalog.");
        }
        return product;
    }

    public void updateProduct(Product product) throws ProductNotFoundException {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (dataStore.getProductById(product.getProductId()) == null) {
            throw new ProductNotFoundException("Cannot update: Product with ID #" + product.getProductId() + " does not exist.");
        }
        dataStore.addProduct(product);
    }

    public void deleteProduct(int productId) throws ProductNotFoundException {
        boolean removed = dataStore.removeProduct(productId);
        if (!removed) {
            throw new ProductNotFoundException("Cannot delete: Product with ID #" + productId + " was not found.");
        }
    }

    public List<Product> getAllProducts() {
        return dataStore.getAllProducts();
    }

    public List<Product> searchByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        String term = keyword.toLowerCase().trim();
        List<Product> matches = new ArrayList<>();
        for (Product product : dataStore.getAllProducts()) {
            if (product.getName().toLowerCase().contains(term)) {
                matches.add(product);
            }
        }
        return matches;
    }

    public List<Product> filterByCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return getAllProducts();
        }
        String term = categoryName.toLowerCase().trim();
        List<Product> matches = new ArrayList<>();
        for (Product product : dataStore.getAllProducts()) {
            if (product.getCategory() != null && product.getCategory().getName().toLowerCase().contains(term)) {
                matches.add(product);
            }
        }
        return matches;
    }
}
