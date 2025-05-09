package com.bnpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class BnplIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14-alpine")
            .withDatabaseName("bnpl_test")
            .withUsername("test")
            .withPassword("test");
    
    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
    
    @Autowired
    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    
    @Test
    public void registerClientAndMakePurchase_shouldWorkEndToEnd() throws Exception {
        // Step 1: Register a client
        String birthDate = LocalDate.now().minusYears(30).format(DateTimeFormatter.ISO_DATE);
        String clientRequestBody = String.format("{\"name\":\"John Doe\",\"birthDate\":\"%s\"}", birthDate);
        
        MvcResult clientResult = mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(clientRequestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCliente").exists())
                .andExpect(jsonPath("$.assignedCreditAmount").exists())
                .andReturn();
        
        String clientResponse = clientResult.getResponse().getContentAsString();
        JsonNode clientJsonNode = objectMapper.readTree(clientResponse);
        Long clientId = clientJsonNode.get("idCliente").asLong();
        BigDecimal assignedCredit = new BigDecimal(clientJsonNode.get("assignedCreditAmount").asText());
        
        assertEquals(new BigDecimal("8000"), assignedCredit); // Since client is 30 years old
        
        // Step 2: Make a purchase
        BigDecimal purchaseAmount = new BigDecimal("1000");
        String purchaseRequestBody = String.format("{\"idCliente\":%d,\"montoCompra\":%s}", clientId, purchaseAmount);
        
        MvcResult purchaseResult = mockMvc.perform(post("/api/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(purchaseRequestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCompra").exists())
                .andExpect(jsonPath("$.totalAmount").exists())
                .andExpect(jsonPath("$.installmentAmount").exists())
                .andExpect(jsonPath("$.paymentDates").isArray())
                .andReturn();
        
        String purchaseResponse = purchaseResult.getResponse().getContentAsString();
        JsonNode purchaseJsonNode = objectMapper.readTree(purchaseResponse);
        Long purchaseId = purchaseJsonNode.get("idCompra").asLong();
        BigDecimal totalAmount = new BigDecimal(purchaseJsonNode.get("totalAmount").asText());
        BigDecimal installmentAmount = new BigDecimal(purchaseJsonNode.get("installmentAmount").asText());
        
        assertTrue(purchaseId > 0);
        // We can only assert that totalAmount > purchaseAmount due to interest rates
        assertTrue(totalAmount.compareTo(purchaseAmount) > 0); 
        assertTrue(totalAmount.compareTo(installmentAmount.multiply(new BigDecimal("5"))) == 0); // 5 payments
        
        // Step 3: Try to make a purchase that exceeds credit limit
        BigDecimal exceededAmount = new BigDecimal("9000");
        String exceededRequestBody = String.format("{\"idCliente\":%d,\"montoCompra\":%s}", clientId, exceededAmount);
        
        mockMvc.perform(post("/api/purchases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(exceededRequestBody))
                .andExpect(status().isBadRequest()) // Should fail due to insufficient credit
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.error").value("Insufficient Credit"));
    }
    
    @Test
    public void registerClient_withInvalidAge_shouldReturnError() throws Exception {
        // Client too young
        String youngBirthDate = LocalDate.now().minusYears(16).format(DateTimeFormatter.ISO_DATE);
        String youngClientRequestBody = String.format("{\"name\":\"Young Client\",\"birthDate\":\"%s\"}", youngBirthDate);
        
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(youngClientRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Client Not Eligible"));
        
        // Client too old
        String oldBirthDate = LocalDate.now().minusYears(70).format(DateTimeFormatter.ISO_DATE);
        String oldClientRequestBody = String.format("{\"name\":\"Old Client\",\"birthDate\":\"%s\"}", oldBirthDate);
        
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(oldClientRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Client Not Eligible"));
    }
}
