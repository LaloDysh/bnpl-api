package com.bnpl.integration;

import com.bnpl.infrastructure.adapter.in.web.customer.request.CustomerRequest;
import com.bnpl.infrastructure.adapter.in.web.customer.response.CustomerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class CustomerApiIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("bnpl_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerPostgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAndGetCustomer() throws Exception {
        // Create customer
        CustomerRequest request = new CustomerRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setSecondLastName("Smith");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1)); // 35 years old should get 8000 credit line

        ResponseEntity<CustomerResponse> createResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/customers",
                request,
                CustomerResponse.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());
        assertEquals(createResponse.getBody().getCreditLineAmount(), createResponse.getBody().getAvailableCreditLineAmount());

        // Get JWT token from headers
        String token = createResponse.getHeaders().getFirst("X-Auth-Token");
        assertNotNull(token);

        // Get customer details
        UUID customerId = createResponse.getBody().getId();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CustomerResponse> getResponse = restTemplate.exchange(
                "http://localhost:" + port + "/v1/customers/" + customerId,
                HttpMethod.GET,
                requestEntity,
                CustomerResponse.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(customerId, getResponse.getBody().getId());
    }

    @Test
    void testCreateCustomerWithInvalidAge() {
        // Create customer with age < 18
        CustomerRequest request = new CustomerRequest();
        request.setFirstName("Young");
        request.setLastName("Person");
        request.setSecondLastName("Test");
        request.setDateOfBirth(LocalDate.now().minusYears(17));

        ResponseEntity<Object> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/customers",
                request,
                Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
