package com.ecomflow.service;

import java.util.ArrayList;
import java.util.List;

import com.ecomflow.enums.OrderStatus;
import com.ecomflow.exceptions.EmptyCartException;
import com.ecomflow.exceptions.InsufficientStockException;
import com.ecomflow.exceptions.InvalidPaymentException;
import com.ecomflow.exceptions.ProductNotFoundException;
import com.ecomflow.interfaces.Discountable;
import com.ecomflow.interfaces.Payment;
import com.ecomflow.model.Address;
import com.ecomflow.model.Cart;
import com.ecomflow.model.CartItem;
import com.ecomflow.model.Customer;
import com.ecomflow.model.Invoice;
import com.ecomflow.model.Order;
import com.ecomflow.model.OrderItem;
import com.ecomflow.repository.DataStore;

public class OrderService {
    private final DataStore dataStore;
    private final CartService cartService;
    private final DiscountService discountService;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;

    public OrderService(DataStore dataStore,
                        CartService cartService,
                        DiscountService discountService,
                        PaymentService paymentService,
                        InventoryService inventoryService) {
        if (dataStore == null || cartService == null || discountService == null ||
            paymentService == null || inventoryService == null) {
            throw new IllegalArgumentException("Dependencies for OrderService cannot be null.");
        }
        this.dataStore = dataStore;
        this.cartService = cartService;
        this.discountService = discountService;
        this.paymentService = paymentService;
        this.inventoryService = inventoryService;
    }

    /**
     * Orchestrates the complete checkout sequence with atomic inventory deduction and immutable snapshots.
     */
    public Invoice placeOrder(Cart cart, String couponCode, String paymentMethod, Address shippingAddress)
            throws EmptyCartException, InsufficientStockException, InvalidPaymentException, ProductNotFoundException {

        // a. Validate cart isn't empty
        cartService.validateNotEmpty(cart);

        if (shippingAddress == null || !shippingAddress.isComplete()) {
            throw new IllegalArgumentException(
                    "A complete shipping address with a valid pincode is required to place an order.");
        }

        Customer customer = cart.getOwner();
        if (customer != null) {
            customer.setAddress(shippingAddress);
        }

        // e. Pre-validate stock for all items BEFORE deductions (atomic check)
        for (CartItem item : cart.getItems()) {
            int productId = item.getProduct().getProductId();
            int requestedQty = item.getQuantity();
            if (!inventoryService.hasSufficientStock(productId, requestedQty)) {
                int available = inventoryService.getStockLevel(productId);
                throw new InsufficientStockException(String.format(
                        "Cannot place order: Insufficient stock for '%s' (ID #%d). Requested: %d, Available: %d",
                        item.getProduct().getName(), productId, requestedQty, available));
            }
        }

        // b. Resolve and apply discount
        double subtotal = cartService.getTotal(cart);
        Discountable discountStrategy = discountService.resolveDiscount(couponCode);
        double discountAmount = discountStrategy.calculateDiscount(subtotal);
        double finalPayableTotal = Math.max(0.0, subtotal - discountAmount);

        // c. Resolve and process payment
        Payment payment = paymentService.resolvePayment(paymentMethod);
        paymentService.processPayment(payment, finalPayableTotal);

        // e. Deduct stock for each item
        for (CartItem item : cart.getItems()) {
            inventoryService.removeStock(item.getProduct().getProductId(), item.getQuantity());
        }

        // d. Convert CartItems to immutable snapshot OrderItems
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            orderItems.add(new OrderItem(item.getProduct(), item.getQuantity()));
        }

        // Create Order and freeze final payable total
        Order order = new Order(customer, orderItems);
        order.setTotal(finalPayableTotal);

        // f. Save order to DataStore and customer history
        dataStore.addOrder(order);
        if (customer != null) {
            customer.addOrderToHistory(order);
        }

        // g. Clear cart
        cartService.clearCart(cart);

        // h. Generate and return Invoice
        return new Invoice(order);
    }

    public Invoice placeOrder(Cart cart, String couponCode, String paymentMethod)
            throws EmptyCartException, InsufficientStockException, InvalidPaymentException, ProductNotFoundException {
        Address defaultAddress = (cart != null && cart.getOwner() != null) ? cart.getOwner().getAddress() : null;
        return placeOrder(cart, couponCode, paymentMethod, defaultAddress);
    }

    /**
     * Cancels an order and restores deducted stock levels unless the order was already DELIVERED.
     */
    public void cancelOrder(int orderId) throws ProductNotFoundException {
        Order order = dataStore.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Cannot cancel: Order #" + orderId + " not found.");
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel Order #" + orderId + " because it has already been DELIVERED.");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            System.out.println("Order #" + orderId + " is already CANCELLED.");
            return;
        }

        // Cancel order state
        order.cancelOrder();

        // Restore stock levels for all snapshot items in the order
        for (OrderItem item : order.getItems()) {
            inventoryService.addStock(item.getProductId(), item.getQuantity());
        }
        System.out.println("Successfully restored inventory for Order #" + orderId + " items.");
    }

    public void updateOrderStatus(int orderId, OrderStatus newStatus) {
        Order order = dataStore.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Cannot update status: Order #" + orderId + " not found.");
        }
        if (order.getStatus() == OrderStatus.DELIVERED && newStatus != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot transition order #" + orderId + " out of terminal DELIVERED status.");
        }
        order.setStatus(newStatus);
        System.out.println("Order #" + orderId + " status updated to " + newStatus);
    }

    public Order getOrderById(int orderId) {
        return dataStore.getOrderById(orderId);
    }

    public List<Order> getAllOrders() {
        return dataStore.getAllOrders();
    }
}
