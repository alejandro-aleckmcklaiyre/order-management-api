package com.example.product_api;

import com.jayway.jsonpath.JsonPath;
import com.example.product_api.repository.CustomerRepository;
import com.example.product_api.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Epic 1: Customer API
 *
 * US1 - POST /api/customers  : Register a new customer
 * US2 - GET  /api/customers/{id} : Get customer details by ID
 */
@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setup() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
    }

    // ─── US1: Register Customer ──────────────────────────────────────────────

    /**
     * US1 - Happy path: register a new customer and get 201 Created with
     * id, name, email, and phone in the response body.
     */
    @Test
    public void us1_registerCustomer_success() throws Exception {
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Maria Santos\",\"email\":\"maria@example.com\",\"phone\":\"09181234567\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Maria Santos")))
                .andExpect(jsonPath("$.email", is("maria@example.com")))
                .andExpect(jsonPath("$.phone", is("09181234567")));
    }

    /**
     * US1 - Duplicate email must be rejected with 400 Bad Request.
     */
    @Test
    public void us1_registerCustomer_duplicateEmail_returns400() throws Exception {
        String body = "{\"name\":\"Maria Santos\",\"email\":\"dupe@example.com\",\"phone\":\"09181234567\"}";

        // First registration succeeds
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

        // Second registration with the same email must fail
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    // ─── US2: Get Customer Details ───────────────────────────────────────────

    /**
     * US2 - Happy path: retrieve a customer by ID and verify all fields.
     */
    @Test
    public void us2_getCustomerById_success() throws Exception {
        // Create a customer first
        String response = mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Jose Rizal\",\"email\":\"jose@example.com\",\"phone\":\"09991234567\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long customerId = ((Number) JsonPath.read(response, "$.id")).longValue();

        // Retrieve the customer by ID
        mockMvc.perform(get("/api/customers/" + customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(customerId.intValue())))
                .andExpect(jsonPath("$.name", is("Jose Rizal")))
                .andExpect(jsonPath("$.email", is("jose@example.com")))
                .andExpect(jsonPath("$.phone", is("09991234567")));
    }

    /**
     * US2 - Non-existent customer ID must return 404 Not Found.
     */
    @Test
    public void us2_getCustomerById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/customers/99999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
