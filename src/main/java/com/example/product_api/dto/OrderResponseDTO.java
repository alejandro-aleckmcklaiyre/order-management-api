package com.example.product_api.dto;

import java.util.List;

public class OrderResponseDTO {
    private Long id;
    private Long customerId;
    private Double totalPrice;
    private String status;
    private List<OrderItemRequestDTO> items; // Ibalik lang kung ano ang binili nila

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<OrderItemRequestDTO> getItems() { return items; }
    public void setItems(List<OrderItemRequestDTO> items) { this.items = items; }
}