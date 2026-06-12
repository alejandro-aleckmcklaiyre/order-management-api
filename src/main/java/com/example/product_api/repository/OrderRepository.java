package com.example.product_api.repository;

import com.example.product_api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Para sa User Story 8: Makuha lahat ng order ng isang customer
    List<Order> findByCustomerId(Long customerId);
}