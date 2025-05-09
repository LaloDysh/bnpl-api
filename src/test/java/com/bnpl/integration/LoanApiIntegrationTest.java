package com.bnpl.integration;
import com.bnpl.infrastructure.adapter.in.web.customer.request.CustomerRequest;
import com.bnpl.infrastructure.adapter.in.web.customer.response.CustomerResponse;
import com.bnpl.infrastructure.adapter.in.web.loan.request.LoanRequest;
import com.bnpl.infrastructure.adapter.in.web.loan.response.LoanResponse;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class LoanApiIntegrationTest {

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
    void testCreateAndGetLoan() throws Exception {
        // First create a customer
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setFirstName("Carlos"); // Should get Scheme 1
        customerRequest.setLastName("Rodriguez");
        customerRequest.setSecondLastName("Lopez");
        customerRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));

        ResponseEntity<CustomerResponse> createCustomerResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/customers",
                customerRequest,
                CustomerResponse.class);

        assertEquals(HttpStatus.CREATED, createCustomerResponse.getStatusCode());
        assertNotNull(createCustomerResponse.getBody());
        UUID customerId = createCustomerResponse.getBody().getId();
        String token = createCustomerResponse.getHeaders().getFirst("X-Auth-Token");

        // Create a loan
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setCustomerId(customerId);
        loanRequest.setAmount(new BigDecimal("1000.00"));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<LoanRequest> requestEntity = new HttpEntity<>(loanRequest, headers);

        ResponseEntity<LoanResponse> createLoanResponse = restTemplate.exchange(
                "http://localhost:" + port + "/v1/loans",
                HttpMethod.POST,
                requestEntity,
                LoanResponse.class);

        assertEquals(HttpStatus.CREATED, createLoanResponse.getStatusCode());
        assertNotNull(createLoanResponse.getBody());
        assertNotNull(createLoanResponse.getBody().getId());
        assertEquals(customerId, createLoanResponse.getBody().getCustomerId());
        assertEquals(new BigDecimal("1000.00"), createLoanResponse.getBody().getAmount());
        assertNotNull(createLoanResponse.getBody().getPaymentPlan());
        assertEquals(new BigDecimal("130.00"), createLoanResponse.getBody().getPaymentPlan().getCommissionAmount());
        assertEquals(5, createLoanResponse.getBody().getPaymentPlan().getInstallments().size());

        // Get loan details
        UUID loanId = createLoanResponse.getBody().getId();
        ResponseEntity<LoanResponse> getLoanResponse = restTemplate.exchange(
                "http://localhost:" + port + "/v1/loans/" + loanId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                LoanResponse.class);

        assertEquals(HttpStatus.OK, getLoanResponse.getStatusCode());
        assertNotNull(getLoanResponse.getBody());
        assertEquals(loanId, getLoanResponse.getBody().getId());

        // Verify that the credit line was reduced
        ResponseEntity<CustomerResponse> getCustomerResponse = restTemplate.exchange(
                "http://localhost:" + port + "/v1/customers/" + customerId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                CustomerResponse.class);

        assertEquals(HttpStatus.OK, getCustomerResponse.getStatusCode());
        BigDecimal availableCreditLine = getCustomerResponse.getBody().getAvailableCreditLineAmount();
        BigDecimal totalCreditLine = getCustomerResponse.getBody().getCreditLineAmount();
        assertEquals(totalCreditLine.subtract(new BigDecimal("1000.00")), availableCreditLine);
    }

    @Test
    void testCreateLoanWithInsufficientCreditLine() throws Exception {
        // First create a customer
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setFirstName("Maria");
        customerRequest.setLastName("Gomez");
        customerRequest.setSecondLastName("Perez");
        customerRequest.setDateOfBirth(LocalDate.of(1995, 1, 1));

        ResponseEntity<CustomerResponse> createCustomerResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/v1/customers",
                customerRequest,
                CustomerResponse.class);

        assertEquals(HttpStatus.CREATED, createCustomerResponse.getStatusCode());
        UUID customerId = createCustomerResponse.getBody().getId();
        BigDecimal creditLineAmount = createCustomerResponse.getBody().getCreditLineAmount();
        String token = createCustomerResponse.getHeaders().getFirst("X-Auth-Token");

        // Try to create a loan exceeding credit line
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setCustomerId(customerId);
        loanRequest.setAmount(creditLineAmount.add(new BigDecimal("1000.00")));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<LoanRequest> requestEntity = new HttpEntity<>(loanRequest, headers);

        ResponseEntity<Object> createLoanResponse = restTemplate.exchange(
                "http://localhost:" + port + "/v1/loans",
                HttpMethod.POST,
                requestEntity,
                Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, createLoanResponse.getStatusCode());
    }
}
