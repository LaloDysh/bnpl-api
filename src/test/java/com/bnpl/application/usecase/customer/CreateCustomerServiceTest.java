package com.bnpl.application.usecase.customer;

import com.bnpl.application.port.out.customer.CustomerRepository;
import com.bnpl.application.service.customer.CreditLineCalculator;
import com.bnpl.domain.model.customer.Customer;
import com.bnpl.domain.model.error.BnplException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CreditLineCalculator creditLineCalculator;

    private CreateCustomerService createCustomerService;

    @BeforeEach
    void setUp() {
        createCustomerService = new CreateCustomerService(customerRepository, creditLineCalculator);
    }

    @Test
    void shouldCreateCustomerSuccessfully() {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        String secondLastName = "Smith";
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        BigDecimal creditLineAmount = new BigDecimal("5000.00");

        when(creditLineCalculator.calculateCreditLine(eq(dateOfBirth), any(LocalDate.class)))
                .thenReturn(creditLineAmount);
        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Customer result = createCustomerService.createCustomer(firstName, lastName, secondLastName, dateOfBirth);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(secondLastName, result.getSecondLastName());
        assertEquals(dateOfBirth, result.getDateOfBirth());
        assertEquals(creditLineAmount, result.getCreditLine().getTotalAmount());
        assertEquals(creditLineAmount, result.getCreditLine().getAvailableAmount());
        assertNotNull(result.getCreatedAt());

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        Customer savedCustomer = customerCaptor.getValue();
        assertEquals(firstName, savedCustomer.getFirstName());
        assertEquals(lastName, savedCustomer.getLastName());
        assertEquals(secondLastName, savedCustomer.getSecondLastName());
        assertEquals(dateOfBirth, savedCustomer.getDateOfBirth());
    }

    @Test
    void shouldThrowExceptionWhenAgeIsInvalid() {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        String secondLastName = "Smith";
        LocalDate dateOfBirth = LocalDate.of(2010, 1, 1); // Too young

        when(creditLineCalculator.calculateCreditLine(eq(dateOfBirth), any(LocalDate.class)))
                .thenThrow(new IllegalArgumentException("Age must be between 18 and 65 years"));

        // When & Then
        BnplException exception = assertThrows(
                BnplException.InvalidAgeException.class,
                () -> createCustomerService.createCustomer(firstName, lastName, secondLastName, dateOfBirth)
        );
        assertEquals("APZ000002", exception.getCode());
        assertEquals("INVALID_CUSTOMER_REQUEST", exception.getError());
        assertEquals("/v1/customers", exception.getPath());

        verify(customerRepository, never()).save(any());
    }
}