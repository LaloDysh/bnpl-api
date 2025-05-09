package com.bnpl.infrastructure.adapter.in.web.customer;

import com.bnpl.application.port.in.customer.CreateCustomerUseCase;
import com.bnpl.application.port.in.customer.GetCustomerUseCase;
import com.bnpl.application.service.security.JwtService;
import com.bnpl.domain.model.customer.CreditLine;
import com.bnpl.domain.model.customer.Customer;
import com.bnpl.infrastructure.adapter.in.web.customer.request.CustomerRequest;
import com.bnpl.infrastructure.adapter.in.web.customer.response.CustomerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateCustomerUseCase createCustomerUseCase;

    @MockBean
    private GetCustomerUseCase getCustomerUseCase;

    @MockBean
    private CustomerMapper customerMapper;

    @MockBean
    private JwtService jwtService;

    @Test
    void shouldCreateCustomerSuccessfully() throws Exception {
        // Given
        CustomerRequest request = new CustomerRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setSecondLastName("Smith");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));

        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(customerId)
                .firstName("John")
                .lastName("Doe")
                .secondLastName("Smith")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .creditLine(CreditLine.of(new BigDecimal("5000.00")))
                .createdAt(LocalDateTime.now())
                .build();

        CustomerResponse response = new CustomerResponse();
        response.setId(customerId);
        response.setCreditLineAmount(new BigDecimal("5000.00"));
        response.setAvailableCreditLineAmount(new BigDecimal("5000.00"));
        response.setCreatedAt(customer.getCreatedAt());

        when(createCustomerUseCase.createCustomer(
                request.getFirstName(),
                request.getLastName(),
                request.getSecondLastName(),
                request.getDateOfBirth()
        )).thenReturn(customer);
        
        when(customerMapper.toResponse(customer)).thenReturn(response);
        when(jwtService.generateToken(customerId)).thenReturn("dummy-token");

        // When & Then
        mockMvc.perform(post("/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("X-Auth-Token", "dummy-token"))
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.creditLineAmount").value("5000.00"))
                .andExpect(jsonPath("$.availableCreditLineAmount").value("5000.00"));
    }

    @Test
    void shouldGetCustomerByIdSuccessfully() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(customerId)
                .firstName("John")
                .lastName("Doe")
                .secondLastName("Smith")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .creditLine(CreditLine.of(new BigDecimal("5000.00")))
                .createdAt(LocalDateTime.now())
                .build();

        CustomerResponse response = new CustomerResponse();
        response.setId(customerId);
        response.setCreditLineAmount(new BigDecimal("5000.00"));
        response.setAvailableCreditLineAmount(new BigDecimal("4000.00"));
        response.setCreatedAt(customer.getCreatedAt());

        when(getCustomerUseCase.getCustomerById(customerId)).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/v1/customers/{customerId}", customerId)
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.creditLineAmount").value("5000.00"))
                .andExpect(jsonPath("$.availableCreditLineAmount").value("4000.00"));
    }
}