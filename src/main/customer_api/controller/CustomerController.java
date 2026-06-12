package com.pup.taguig.app.controller;

import com.pup.taguig.app.model.Customer;
import com.pup.taguig.app.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired 
    private CustomerService customerService;

    //POST /api/customers
    @PostMapping
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody Customer customer) {
        try {
            Customer createdCustomer = customerService.registerCustomer(customer);
            return ResponseEntity.ok(createdCustomer); 
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //GET /api/customers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); 
    }
}