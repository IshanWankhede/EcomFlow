package com.ecomflow.model;

import java.time.format.DateTimeFormatter;

public class Invoice {
    private Order order;

    public Invoice(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null for Invoice.");
        }
        this.order = order;
    }

    public void printInvoice() {
        System.out.println(generateInvoiceText());
    }

    public String generateInvoiceText() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        sb.append("========================================================\n");
        sb.append("                   ECOMFLOW INVOICE                     \n");
        sb.append("========================================================\n");
        sb.append(String.format("Invoice Date : %s%n", order.getPlacedAt() != null ? order.getPlacedAt().format(dtf) : "N/A"));
        sb.append(String.format("Order ID     : #%d%n", order.getOrderId()));
        sb.append(String.format("Order Status : %s%n", order.getStatus()));
        sb.append("--------------------------------------------------------\n");
        
        if (order.getCustomer() != null) {
            sb.append(String.format("Customer     : %s (%s)%n", order.getCustomer().getName(), order.getCustomer().getEmail()));
            if (order.getCustomer().getAddress() != null) {
                sb.append(String.format("Ship To      : %s%n", order.getCustomer().getAddress().toString()));
            }
        }
        sb.append("--------------------------------------------------------\n");
        sb.append(String.format("%-28s %-6s %-10s %-8s%n", "Item", "Qty", "Unit Price", "Subtotal"));
        sb.append("--------------------------------------------------------\n");

        for (OrderItem item : order.getItems()) {
            sb.append(String.format("%-28s %-6d $%-9.2f $%-8.2f%n",
                    truncate(item.getProductName(), 28),
                    item.getQuantity(),
                    item.getPriceAtOrderTime(),
                    item.getSubtotal()));
        }

        sb.append("--------------------------------------------------------\n");
        sb.append(String.format("TOTAL AMOUNT : $%.2f%n", order.getTotal()));
        sb.append("========================================================\n");
        return sb.toString();
    }

    private String truncate(String text, int length) {
        if (text == null) return "";
        return text.length() <= length ? text : text.substring(0, length - 3) + "...";
    }

    public Order getOrder() {
        return order;
    }
}
