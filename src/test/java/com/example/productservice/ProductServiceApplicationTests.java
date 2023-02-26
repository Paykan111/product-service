package com.example.productservice;

import com.example.productservice.dto.ProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest = getProductRequest();
        String productRequestString = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        Assertions.assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        List<ProductResponse> productResponseList = getProductResponseList();
        String productResponseString = objectMapper.writeValueAsString(productResponseList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productResponseString))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private List<ProductResponse> getProductResponseList() {
        List<ProductResponse> productResponses = new ArrayList<>();

        productResponses.add(ProductResponse.builder()
                .id("1488")
                .name("iPhone 13")
                .description("iPhone 13")
                .price(BigDecimal.valueOf(1200))
                .build());
        productResponses.add(ProductResponse.builder()
                .id("1337")
                .name("iPhone 14")
                .description("iPhone 14")
                .price(BigDecimal.valueOf(1500))
                .build());
        return productResponses;
    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("iPhone 13")
                .description("iPhone 13")
                .price(BigDecimal.valueOf(1200))
                .build();
    }

}
