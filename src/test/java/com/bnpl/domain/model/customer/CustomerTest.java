package com.bnpl.domain.model.customer;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void testBuilder() {
        // Given
        UUID id = UUID.randomUUID();
        String firstName = "John";
        String lastName = "Doe";
        String secondLastName = "Smith";
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        CreditLine creditLine = CreditLine.of(new BigDecimal("5000.00"));
        LocalDateTime createdAt = LocalDateTime.now();
        
        // When
        Customer customer = Customer.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .secondLastName(secondLastName)
                .dateOfBirth(dateOfBirth)
                .creditLine(creditLine)
                .createdAt(createdAt)
                .build();
        
        // Then
        assertEquals(id, customer.getId());
        assertEquals(firstName, customer.getFirstName());
        assertEquals(lastName, customer.getLastName());
        assertEquals(secondLastName, customer.getSecondLastName());
        assertEquals(dateOfBirth, customer.getDateOfBirth());
        assertEquals(creditLine, customer.getCreditLine());
        assertEquals(createdAt, customer.getCreatedAt());
    }
}