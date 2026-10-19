package com.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.dto.request.ProductRequest;
import com.inventory.dto.request.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest regRequest = new RegisterRequest();
        regRequest.setUsername("testuser");
        regRequest.setEmail("test@example.com");
        regRequest.setPassword("password123");
        regRequest.setFullName("Test User");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andReturn();

        token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    void createProduct_shouldReturn201() throws Exception {
        ProductRequest product = new ProductRequest();
        product.setName("Test Widget");
        product.setDescription("A test product");
        product.setCategory("Electronics");
        product.setSku("TST-001");
        product.setQuantity(50);
        product.setPrice(new BigDecimal("29.99"));

        MockMultipartFile productPart = new MockMultipartFile(
                "product", "", "application/json",
                objectMapper.writeValueAsBytes(product));

        mockMvc.perform(multipart("/api/products")
                        .file(productPart)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Widget"))
                .andExpect(jsonPath("$.sku").value("TST-001"))
                .andExpect(jsonPath("$.status").value("IN_STOCK"));
    }

    @Test
    void createProduct_lowQuantity_shouldBeLowStock() throws Exception {
        ProductRequest product = new ProductRequest();
        product.setName("Low Stock Item");
        product.setCategory("Electronics");
        product.setSku("LOW-001");
        product.setQuantity(5);
        product.setPrice(new BigDecimal("10.00"));

        MockMultipartFile productPart = new MockMultipartFile(
                "product", "", "application/json",
                objectMapper.writeValueAsBytes(product));

        mockMvc.perform(multipart("/api/products")
                        .file(productPart)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("LOW_STOCK"));
    }

    @Test
    void createProduct_zeroQuantity_shouldBeOutOfStock() throws Exception {
        ProductRequest product = new ProductRequest();
        product.setName("Out of Stock Item");
        product.setCategory("Electronics");
        product.setSku("OOS-001");
        product.setQuantity(0);
        product.setPrice(new BigDecimal("15.00"));

        MockMultipartFile productPart = new MockMultipartFile(
                "product", "", "application/json",
                objectMapper.writeValueAsBytes(product));

        mockMvc.perform(multipart("/api/products")
                        .file(productPart)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OUT_OF_STOCK"));
    }

    @Test
    void getProducts_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getProducts_withAuth_shouldReturnPagedResults() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber());
    }

    @Test
    void getDashboardStats_shouldReturnStats() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").isNumber())
                .andExpect(jsonPath("$.inStockCount").isNumber())
                .andExpect(jsonPath("$.totalInventoryValue").isNumber());
    }
}
