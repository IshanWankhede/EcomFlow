package com.ecomflow.service;

import com.ecomflow.exceptions.InsufficientStockException;
import com.ecomflow.exceptions.ProductNotFoundException;
import com.ecomflow.model.Product;
import com.ecomflow.repository.DataStore;

public class InventoryService {
    private final DataStore dataStore;

    public InventoryService(DataStore dataStore) {
        if (dataStore == null) {
            throw new IllegalArgumentException("DataStore cannot be null.");
        }
        this.dataStore = dataStore;
    }

    public void addStock(int productId, int quantity) throws ProductNotFoundException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Stock to add must be positive. Received: " + quantity);
        }
        Product product = getProduct(productId);
        product.updateStock(quantity);
    }

    public void removeStock(int productId, int quantity) throws ProductNotFoundException, InsufficientStockException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Stock to remove must be positive. Received: " + quantity);
        }
        Product product = getProduct(productId);
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(String.format(
                    "Insufficient stock for '%s' (ID #%d). Requested: %d, Available: %d",
                    product.getName(), productId, quantity, product.getStock()));
        }
        product.updateStock(-quantity);
    }

    public int getStockLevel(int productId) throws ProductNotFoundException {
        return getProduct(productId).getStock();
    }

    public boolean hasSufficientStock(int productId, int requestedQty) throws ProductNotFoundException {
        return getProduct(productId).getStock() >= requestedQty;
    }

    private Product getProduct(int productId) throws ProductNotFoundException {
        Product product = dataStore.getProductById(productId);
        if (product == null) {
            throw new ProductNotFoundException("Cannot adjust inventory: Product ID #" + productId + " does not exist.");
        }
        return product;
    }
}
