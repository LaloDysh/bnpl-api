package com.bnpl.application.service.loan;

import com.bnpl.domain.model.customer.CreditLine;
import com.bnpl.domain.model.customer.Customer;
import com.bnpl.domain.model.loan.PaymentScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentSchemeAssignerTest {

    private PaymentSchemeAssigner paymentSchemeAssigner;

    @BeforeEach
    void setUp() {
        paymentSchemeAssigner = new PaymentSchemeAssigner();
    }

    @Test
    void shouldAssignScheme1WhenFirstNameStartsWithC() {
        // Given
        Customer customer = createCustomer("Carlos");

        // When
        PaymentScheme result = paymentSchemeAssigner.assignPaymentScheme(customer);

        // Then
        assertEquals(PaymentScheme.SCHEME_1, result);
    }

    @Test
    void shouldAssignScheme1WhenFirstNameStartsWithL() {
        // Given
        Customer customer = createCustomer("Luis");

        // When
        PaymentScheme result = paymentSchemeAssigner.assignPaymentScheme(customer);

        // Then
        assertEquals(PaymentScheme.SCHEME_1, result);
    }

    @Test
    void shouldAssignScheme1WhenFirstNameStartsWithH() {
        // Given
        Customer customer = createCustomer("Hugo");

        // When
        PaymentScheme result = paymentSchemeAssigner.assignPaymentScheme(customer);

        // Then
        assertEquals(PaymentScheme.SCHEME_1, result);
    }

    @Test
    void shouldAssignScheme2WhenIdValueIsGreaterThan25() {
        // Given - using a fixed UUID that will hash to a value > 25
        UUID id = UUID.fromString("f7c1bd87-4dda-4b14-b883-a1088a3b2c1a");
        Customer customer = createCustomerWithId("Alex", id);

        // When
        PaymentScheme result = paymentSchemeAssigner.assignPaymentScheme(customer);

        // Then
        assertEquals(PaymentScheme.SCHEME_2, result);
    }

    @Test
    void shouldAssignScheme2ByDefault() {
        // Given
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000000");
        Customer customer = createCustomerWithId("Miguel", id);

        // When
        PaymentScheme result = paymentSchemeAssigner.assignPaymentScheme(customer);

        // Then
        assertEquals(PaymentScheme.SCHEME_2, result);
    }
    
    private Customer createCustomer(String firstName) {
        return Customer.builder()
                .id(UUID.randomUUID())
                .firstName(firstName)
                .lastName("Doe")
                .secondLastName("Smith")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .creditLine(CreditLine.of(new BigDecimal("5000.00")))
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    private Customer createCustomerWithId(String firstName, UUID id) {
        return Customer.builder()
                .id(id)
                .firstName(firstName)
                .lastName("Doe")
                .secondLastName("Smith")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .creditLine(CreditLine.of(new BigDecimal("5000.00")))
                .createdAt(LocalDateTime.now())
                .build();
    }
}