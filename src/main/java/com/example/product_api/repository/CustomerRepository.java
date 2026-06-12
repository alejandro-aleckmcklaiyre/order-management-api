package com.example.product_api.repository;

import com.example.product_api.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Find a customer by their email address
    Optional<Customer> findByEmail(String email);
}