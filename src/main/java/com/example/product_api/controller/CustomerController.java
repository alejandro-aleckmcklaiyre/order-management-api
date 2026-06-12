package com.example.product_api.controller;

import com.example.product_api.model.Customer;
import com.example.product_api.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // User Story 1: Register Customer (POST /api/customers)
    @PostMapping
    public ResponseEntity<?> register(@RequestBody Customer customer) {
        try {
            Customer created = customerService.registerCustomer(customer);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Requirement: Return 400 Bad Request if email is already registered
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // User Story 2: Get Customer Details (GET /api/customers/{id})
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(customer -> new ResponseEntity<>(customer, HttpStatus.OK))
                // Requirement: Return 404 if not found
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}