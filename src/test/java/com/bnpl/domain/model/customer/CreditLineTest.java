package com.bnpl.domain.model.customer;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class CreditLineTest {

    @Test
    void testOf() {
        // Given
        BigDecimal amount = new BigDecimal("5000.00");
        
        // When
        CreditLine creditLine = CreditLine.of(amount);
        
        // Then
        assertEquals(amount, creditLine.getTotalAmount());
        assertEquals(amount, creditLine.getAvailableAmount());
    }

    @Test
    void testDeduct() {
        // Given
        BigDecimal total = new BigDecimal("5000.00");
        BigDecimal deduct = new BigDecimal("1000.00");
        CreditLine creditLine = CreditLine.of(total);
        
        // When
        creditLine.deduct(deduct);
        
        // Then
        assertEquals(total, creditLine.getTotalAmount());
        assertEquals(new BigDecimal("4000.00"), creditLine.getAvailableAmount());
    }

    @Test
    void testDeductThrowsException() {
        // Given
        CreditLine creditLine = CreditLine.of(new BigDecimal("1000.00"));
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            creditLine.deduct(new BigDecimal("2000.00"))
        );
    }

    @Test
    void testRestore() {
        // Given
        BigDecimal total = new BigDecimal("5000.00");
        BigDecimal deduct = new BigDecimal("1000.00");
        CreditLine creditLine = CreditLine.of(total);
        creditLine.deduct(deduct);
        
        // When
        creditLine.restore(deduct);
        
        // Then
        assertEquals(total, creditLine.getAvailableAmount());
    }

    @Test
    void testRestoreDoesNotExceedTotal() {
        // Given
        BigDecimal total = new BigDecimal("5000.00");
        BigDecimal deduct = new BigDecimal("1000.00");
        CreditLine creditLine = CreditLine.of(total);
        creditLine.deduct(deduct);
        
        // When
        creditLine.restore(new BigDecimal("2000.00"));
        
        // Then
        assertEquals(total, creditLine.getAvailableAmount());
    }
}