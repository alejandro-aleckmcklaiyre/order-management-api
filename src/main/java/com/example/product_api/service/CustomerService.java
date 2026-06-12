package com.example.product_api.service;

import com.example.product_api.model.Customer;
import java.util.Optional;

public interface CustomerService {
    Customer registerCustomer(Customer customer);
    Optional<Customer> getCustomerById(Long id);
}