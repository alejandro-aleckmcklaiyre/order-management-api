package com.example.product_api.controller;

import com.example.product_api.dto.OrderRequestDTO;
import com.example.product_api.dto.OrderResponseDTO;
import com.example.product_api.model.Order;
import com.example.product_api.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // User Story 6: Create Order
    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO orderDTO) {
        OrderResponseDTO response = orderService.createOrder(orderDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // User Story 7: Get Order by ID
    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        OrderResponseDTO response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    // User Story 8: Get Customer Orders
    @GetMapping("/customers/{id}/orders")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByCustomerId(@PathVariable("id") Long customerId) {
        List<OrderResponseDTO> responses = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(responses);
    }

    // User Story 9: Cancel Order
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Order cancelled successfully and product stock has been restored.");
    }
}