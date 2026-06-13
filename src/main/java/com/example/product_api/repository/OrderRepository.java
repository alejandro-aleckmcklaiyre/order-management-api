package com.example.product_api.repository;

import com.example.product_api.model.Order;
import com.example.product_api.dto.TopProductResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Para sa User Story 8: Makuha lahat ng order ng isang customer
    List<Order> findByCustomerId(Long customerId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status <> 'CANCELLED'")
    Long countNonCancelledOrders();

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0.0) FROM Order o WHERE o.status <> 'CANCELLED'")
    Double sumNonCancelledRevenue();

    @Query("SELECT new com.example.product_api.dto.TopProductResponseDTO(oi.productId, p.name, SUM(oi.quantity), SUM(oi.quantity * oi.price)) " +
           "FROM Order o JOIN o.items oi JOIN Product p ON oi.productId = p.id " +
           "WHERE o.status <> 'CANCELLED' " +
           "GROUP BY oi.productId, p.name " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<TopProductResponseDTO> findTopSellingProducts();
}