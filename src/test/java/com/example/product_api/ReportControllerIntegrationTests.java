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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReportControllerIntegrationTests {

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

    // ─── Helper: create a customer via POST /api/customers and return its ID ───

    private Long createCustomer(String name, String email, String phone) throws Exception {
        String body = String.format(
                "{\"name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\"}",
                name, email, phone);
        String response = mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    // ─── Helper: create a product via POST /api/products and return its ID ───

    private Long createProduct(String name, String description, double price, int stock) throws Exception {
        String body = String.format(
                "{\"name\":\"%s\",\"description\":\"%s\",\"price\":%.1f,\"stock\":%d}",
                name, description, price, stock);
        String response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    // ─── US10 / US11: empty-database baseline ───────────────────────────────

    @Test
    public void testEmptyReport() throws Exception {
        mockMvc.perform(get("/api/reports/sales")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders", is(0)))
                .andExpect(jsonPath("$.totalRevenue", is(0.0)));

        mockMvc.perform(get("/api/reports/top-products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ─── US10 / US11: full reporting flow ────────────────────────────────────

    @Test
    public void testSalesReportAndTopProducts() throws Exception {

        // 1. Register a customer via POST /api/customers (US1)
        Long customerId = createCustomer("Juan dela Cruz", "juan@example.com", "09171234567");

        // 2. Create Product A via POST /api/products (US3)
        Long productAId = createProduct("Product A", "Desc A", 100.0, 10);

        // 3. Create Product B via POST /api/products (US3)
        Long productBId = createProduct("Product B", "Desc B", 50.0, 20);

        // 4. Create Order 1: Product A x2, Product B x1  → total = 250.0 (US6)
        String order1Json = String.format(
                "{\"customerId\":%d,\"items\":[{\"productId\":%d,\"quantity\":2},{\"productId\":%d,\"quantity\":1}]}",
                customerId, productAId, productBId);
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(order1Json))
                .andExpect(status().isCreated());

        // 5. Create Order 2: Product A x1  → total = 100.0 (US6)
        String order2Json = String.format(
                "{\"customerId\":%d,\"items\":[{\"productId\":%d,\"quantity\":1}]}",
                customerId, productAId);
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(order2Json))
                .andExpect(status().isCreated());

        // 6. Create Order 3: Product B x5  → total = 250.0, then cancel it (US6, US9)
        String order3Json = String.format(
                "{\"customerId\":%d,\"items\":[{\"productId\":%d,\"quantity\":5}]}",
                customerId, productBId);
        String responseOrder3 = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(order3Json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long order3Id = ((Number) JsonPath.read(responseOrder3, "$.id")).longValue();

        // Cancel Order 3 via DELETE /api/orders/{id} (US9)
        mockMvc.perform(delete("/api/orders/" + order3Id))
                .andExpect(status().isOk());

        // 7. Verify GET /api/reports/sales (US10)
        // Expected: totalOrders = 2 (Order 1 + Order 2), totalRevenue = 350.0
        mockMvc.perform(get("/api/reports/sales")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders", is(2)))
                .andExpect(jsonPath("$.totalRevenue", is(350.0)));

        // 8. Verify GET /api/reports/top-products (US11)
        // Expected: Product A (qty 3, rev 300.0) first, Product B (qty 1, rev 50.0) second
        // (Order 3 was CANCELLED so Product B x5 is excluded)
        mockMvc.perform(get("/api/reports/top-products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].productId", is(productAId.intValue())))
                .andExpect(jsonPath("$[0].productName", is("Product A")))
                .andExpect(jsonPath("$[0].totalQuantitySold", is(3)))
                .andExpect(jsonPath("$[0].totalRevenue", is(300.0)))
                .andExpect(jsonPath("$[1].productId", is(productBId.intValue())))
                .andExpect(jsonPath("$[1].productName", is("Product B")))
                .andExpect(jsonPath("$[1].totalQuantitySold", is(1)))
                .andExpect(jsonPath("$[1].totalRevenue", is(50.0)));
    }
}
