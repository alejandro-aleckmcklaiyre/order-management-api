package com.example.product_api.dto;

import java.util.List;

public class OrderRequestDTO {
    private Long customerId;
    private List<OrderItemRequestDTO> items;

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public List<OrderItemRequestDTO> getItems() { return items; }
    public void setItems(List<OrderItemRequestDTO> items) { this.items = items; }
}