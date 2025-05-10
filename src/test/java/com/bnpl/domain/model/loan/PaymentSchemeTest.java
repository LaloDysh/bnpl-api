package com.bnpl.domain.model.loan;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PaymentSchemeTest {

    @Test
    void testScheme1Values() {
        // Given
        PaymentScheme scheme = PaymentScheme.SCHEME_1;
        
        // Then
        assertEquals(5, scheme.getNumberOfPayments());
        assertEquals(14, scheme.getFrequencyInDays());
        assertEquals(new BigDecimal("0.13"), scheme.getInterestRate());
    }

    @Test
    void testScheme2Values() {
        // Given
        PaymentScheme scheme = PaymentScheme.SCHEME_2;
        
        // Then
        assertEquals(5, scheme.getNumberOfPayments());
        assertEquals(14, scheme.getFrequencyInDays());
        assertEquals(new BigDecimal("0.16"), scheme.getInterestRate());
    }
}