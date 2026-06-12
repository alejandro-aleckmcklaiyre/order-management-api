package com.example.product_api.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long customerId;
    private Double totalPrice;
    private String status; // PENDING, CANCELLED

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    // --- GETTERS AND SETTERS ---
    public Long getId() { 
        return id; 
    }
    public void setId(Long id) {
    this.id = id;
    }

    public Long getCustomerId() { 
        return customerId; 
    }
    public void setCustomerId(Long customerId) { 
        this.customerId = customerId; 
    }

    public Double getTotalPrice() { 
        return totalPrice; 
    }
    public void setTotalPrice(Double totalPrice) { 
        this.totalPrice = totalPrice; 
    }

    public String getStatus() { 
        return status; 
    }
    public void setStatus(String status) { 
        this.status = status; 
    }

    public List<OrderItem> getItems() { 
        return items; 
    }
    public void setItems(List<OrderItem> items) { 
        this.items = items; 
    }
}