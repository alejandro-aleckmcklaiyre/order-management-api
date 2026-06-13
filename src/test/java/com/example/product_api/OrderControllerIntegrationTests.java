package com.example.product_api;

import com.jayway.jsonpath.JsonPath;
import com.example.product_api.repository.CustomerRepository;
import com.example.product_api.repository.OrderRepository;
import com.example.product_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Epic 3: Order API
 *
 * US6 - POST   /api/orders              : Create an order
 * US7 - GET    /api/orders/{id}         : Get order by ID
 * US8 - GET    /api/customers/{id}/orders : Get all orders for a customer
 * US9 - DELETE /api/orders/{id}         : Cancel an order (PENDING only)
 */
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setup() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();
    }

    // ─── Shared helpers ──────────────────────────────────────────────────────

    private Long createCustomer(String name, String email, String phone) throws Exception {
        String body = String.format(
                "{\"name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\"}", name, email, phone);
        String response = mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    private Long createProduct(String name, double price, int stock) throws Exception {
        String body = String.format(
                "{\"name\":\"%s\",\"description\":\"desc\",\"price\":%.1f,\"stock\":%d}", name, price, stock);
        String response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    private Long createOrder(Long customerId, String itemsJson) throws Exception {
        String body = String.format("{\"customerId\":%d,\"items\":%s}", customerId, itemsJson);
        String response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    // ─── US6: Create Order ───────────────────────────────────────────────────

    /**
     * US6 - Happy path: create an order, verify 201 Created, total price is
     * calculated automatically, status starts as PENDING, and stock is reduced.
     */
    @Test
    public void us6_createOrder_success_priceCalculatedAndStockReduced() throws Exception {
        Long customerId = createCustomer("Ana Reyes", "ana@example.com", "09161234567");
        Long productId  = createProduct("Headphones", 1500.0, 10);

        // Place order: 3 units × 1500.0 = 4500.0
        String orderBody = String.format(
                "{\"customerId\":%d,\"items\":[{\"productId\":%d,\"quantity\":3}]}",
                customerId, productId);
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.customerId", is(customerId.intValue())))
                .andExpect(jsonPath("$.totalPrice", is(4500.0)))
                .andExpect(jsonPath("$.status", is("PENDING")));

        // Verify stock was reduced: 10 - 3 = 7
        mockMvc.perform(get("/api/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock", is(7)));
    }

    /**
     * US6 - Order with insufficient stock must be rejected.
     */
    @Test
    public void us6_createOrder_insufficientStock_returnsError() throws Exception {
        Long customerId = createCustomer("Ben Cruz", "ben@example.com", "09171234567");
        Long productId  = createProduct("Chair", 3000.0, 2); // only 2 in stock

        String orderBody = String.format(
                "{\"customerId\":%d,\"items\":[{\"productId\":%d,\"quantity\":5}]}",
                customerId, productId);
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderBody))
                .andExpect(status().is5xxServerError());
    }

    /**
     * US6 - Order referencing a non-existent product must be rejected.
     */
    @Test
    public void us6_createOrder_productNotFound_returnsError() throws Exception {
        Long customerId = createCustomer("Carl Sy", "carl@example.com", "09181234567");

        String orderBody = String.format(
                "{\"customerId\":%d,\"items\":[{\"productId\":99999,\"quantity\":1}]}",
                customerId);
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderBody))
                .andExpect(status().is5xxServerError());
    }

    // ─── US7: Get Order by ID ────────────────────────────────────────────────

    /**
     * US7 - Happy path: retrieve order details by ID including items and total price.
     */
    @Test
    public void us7_getOrderById_success() throws Exception {
        Long customerId = createCustomer("Diana Lee", "diana@example.com", "09191234567");
        Long productId  = createProduct("Tablet", 8000.0, 5);

        Long orderId = createOrder(customerId,
                String.format("[{\"productId\":%d,\"quantity\":1}]", productId));

        mockMvc.perform(get("/api/orders/" + orderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(orderId.intValue())))
                .andExpect(jsonPath("$.customerId", is(customerId.intValue())))
                .andExpect(jsonPath("$.totalPrice", is(8000.0)))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId", is(productId.intValue())))
                .andExpect(jsonPath("$.items[0].quantity", is(1)));
    }

    /**
     * US7 - Non-existent order ID returns error.
     */
    @Test
    public void us7_getOrderById_notFound_returnsError() throws Exception {
        mockMvc.perform(get("/api/orders/99999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    // ─── US8: Get Customer Orders ────────────────────────────────────────────

    /**
     * US8 - A customer's order history lists all their orders.
     */
    @Test
    public void us8_getCustomerOrders_returnsAllOrders() throws Exception {
        Long customerId = createCustomer("Elena Tan", "elena@example.com", "09201234567");
        Long productId  = createProduct("Speaker", 2000.0, 20);

        // Place 2 orders for the same customer
        createOrder(customerId, String.format("[{\"productId\":%d,\"quantity\":1}]", productId));
        createOrder(customerId, String.format("[{\"productId\":%d,\"quantity\":2}]", productId));

        mockMvc.perform(get("/api/customers/" + customerId + "/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    /**
     * US8 - Customer with no orders returns an empty list.
     */
    @Test
    public void us8_getCustomerOrders_noOrders_returnsEmptyList() throws Exception {
        Long customerId = createCustomer("Fred Go", "fred@example.com", "09211234567");

        mockMvc.perform(get("/api/customers/" + customerId + "/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ─── US9: Cancel Order ───────────────────────────────────────────────────

    /**
     * US9 - Cancel a PENDING order: returns 200 OK and stock is restored.
     */
    @Test
    public void us9_cancelOrder_success_stockRestored() throws Exception {
        Long customerId = createCustomer("Grace Uy", "grace@example.com", "09221234567");
        Long productId  = createProduct("Webcam", 1200.0, 10);

        Long orderId = createOrder(customerId,
                String.format("[{\"productId\":%d,\"quantity\":4}]", productId));

        // Stock after order: 10 - 4 = 6
        mockMvc.perform(get("/api/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stock", is(6)));

        // Cancel the order
        mockMvc.perform(delete("/api/orders/" + orderId))
                .andExpect(status().isOk());

        // Stock should be restored: 6 + 4 = 10
        mockMvc.perform(get("/api/products/" + productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stock", is(10)));
    }

    /**
     * US9 - Attempting to cancel an already-cancelled order returns an error.
     */
    @Test
    public void us9_cancelOrder_alreadyCancelled_returnsError() throws Exception {
        Long customerId = createCustomer("Harry Lim", "harry@example.com", "09231234567");
        Long productId  = createProduct("USB Hub", 800.0, 10);

        Long orderId = createOrder(customerId,
                String.format("[{\"productId\":%d,\"quantity\":1}]", productId));

        // First cancel succeeds
        mockMvc.perform(delete("/api/orders/" + orderId))
                .andExpect(status().isOk());

        // Second cancel must fail (status is no longer PENDING)
        mockMvc.perform(delete("/api/orders/" + orderId))
                .andExpect(status().is5xxServerError());
    }
}
