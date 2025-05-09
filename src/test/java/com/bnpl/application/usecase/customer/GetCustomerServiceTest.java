package com.bnpl.application.usecase.customer;

import com.bnpl.application.port.out.customer.CustomerRepository;
import com.bnpl.domain.model.customer.CreditLine;
import com.bnpl.domain.model.customer.Customer;
import com.bnpl.domain.model.error.BnplException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private GetCustomerService getCustomerService;

    @BeforeEach
    void setUp() {
        getCustomerService = new GetCustomerService(customerRepository);
    }

    @Test
    void shouldGetCustomerByIdSuccessfully() {
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

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When
        Customer result = getCustomerService.getCustomerById(customerId);

        // Then
        assertNotNull(result);
        assertEquals(customerId, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("Smith", result.getSecondLastName());
        assertEquals(LocalDate.of(1990, 1, 1), result.getDateOfBirth());
        assertEquals(new BigDecimal("5000.00"), result.getCreditLine().getTotalAmount());
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When & Then
        BnplException exception = assertThrows(
                BnplException.CustomerNotFoundException.class,
                () -> getCustomerService.getCustomerById(customerId)
        );
        assertEquals("APZ000005", exception.getCode());
        assertEquals("CUSTOMER_NOT_FOUND", exception.getError());
        assertEquals("/v1/customers/" + customerId, exception.getPath());
    }
}