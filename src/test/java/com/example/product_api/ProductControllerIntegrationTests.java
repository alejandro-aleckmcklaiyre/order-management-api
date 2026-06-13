package com.example.product_api;

import com.jayway.jsonpath.JsonPath;
import com.example.product_api.repository.OrderRepository;
import com.example.product_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Epic 2: Product API
 *
 * US3 - POST /api/products        : Create a product
 * US4 - GET  /api/products        : List all products
 * US5 - GET  /api/products/{id}   : Get product by ID
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void setup() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    // ─── US3: Create Product ─────────────────────────────────────────────────

    /**
     * US3 - Happy path: create a product and verify the response fields.
     */
    @Test
    public void us3_createProduct_success() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Laptop\",\"description\":\"Gaming laptop\",\"price\":49999.0,\"stock\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.description", is("Gaming laptop")))
                .andExpect(jsonPath("$.price", is(49999.0)))
                .andExpect(jsonPath("$.stock", is(5)));
    }

    /**
     * US3 - Price must be greater than 0; price = 0 should return 400 Bad Request.
     */
    @Test
    public void us3_createProduct_invalidPrice_returns400() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"BadProduct\",\"description\":\"Bad\",\"price\":0,\"stock\":5}"))
                .andExpect(status().isBadRequest());
    }

    /**
     * US3 - Stock must be >= 0; negative stock should return 400 Bad Request.
     */
    @Test
    public void us3_createProduct_negativeStock_returns400() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"BadStock\",\"description\":\"Bad\",\"price\":100.0,\"stock\":-1}"))
                .andExpect(status().isBadRequest());
    }

    // ─── US4: List Products ──────────────────────────────────────────────────

    /**
     * US4 - GET all products returns 200 OK and a list; with 2 inserted should return 2.
     */
    @Test
    public void us4_getAllProducts_success() throws Exception {
        // Insert two products via API
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Mouse\",\"description\":\"Wireless mouse\",\"price\":599.0,\"stock\":50}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Keyboard\",\"description\":\"Mechanical keyboard\",\"price\":1299.0,\"stock\":30}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    /**
     * US4 - Empty catalog returns 200 OK with an empty array.
     */
    @Test
    public void us4_getAllProducts_empty() throws Exception {
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ─── US5: Get Product by ID ──────────────────────────────────────────────

    /**
     * US5 - Happy path: retrieve a product by its ID and verify all fields.
     */
    @Test
    public void us5_getProductById_success() throws Exception {
        // Create via API
        String response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Monitor\",\"description\":\"4K Monitor\",\"price\":15000.0,\"stock\":8}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long productId = ((Number) JsonPath.read(response, "$.id")).longValue();

        // Retrieve by ID
        mockMvc.perform(get("/api/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(productId.intValue())))
                .andExpect(jsonPath("$.name", is("Monitor")))
                .andExpect(jsonPath("$.description", is("4K Monitor")))
                .andExpect(jsonPath("$.price", is(15000.0)))
                .andExpect(jsonPath("$.stock", is(8)));
    }

    /**
     * US5 - Non-existent product ID returns 404 Not Found.
     */
    @Test
    public void us5_getProductById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/products/99999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
