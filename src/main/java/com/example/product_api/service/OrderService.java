package com.example.product_api.service;

import com.example.product_api.dto.OrderRequestDTO;
import com.example.product_api.dto.OrderResponseDTO;
import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO orderDTO);
    OrderResponseDTO getOrderById(Long id);
    List<OrderResponseDTO> getOrdersByCustomerId(Long customerId);
    void cancelOrder(Long id);
}