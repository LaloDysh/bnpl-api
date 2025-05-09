package com.bnpl.application.service.loan;

import com.bnpl.domain.model.loan.Installment;
import com.bnpl.domain.model.loan.InstallmentStatus;
import com.bnpl.domain.model.loan.PaymentScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;

class InstallmentCalculatorTest {

    private InstallmentCalculator installmentCalculator;

    @BeforeEach
    void setUp() {
        installmentCalculator = new InstallmentCalculator();
    }

    @Test
    void shouldCalculateInstallments() {
        // Given
        UUID loanId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("1000.00");
        PaymentScheme paymentScheme = PaymentScheme.SCHEME_1;
        LocalDate startDate = LocalDate.now();

        // When
        List<Installment> installments = installmentCalculator.calculateInstallments(loanId, amount, paymentScheme, startDate);

        // Then
        assertEquals(5, installments.size());
        
        // First installment should be NEXT, others PENDING
        assertEquals(InstallmentStatus.NEXT, installments.get(0).getStatus());
        for (int i = 1; i < installments.size(); i++) {
            assertEquals(InstallmentStatus.PENDING, installments.get(i).getStatus());
        }
        
        // Check installment amounts (should be equal)
        BigDecimal expectedAmount = new BigDecimal("200.00"); // 1000 / 5
        for (Installment installment : installments) {
            assertEquals(expectedAmount, installment.getAmount());
        }
        
        // Check payment dates
        for (int i = 0; i < installments.size(); i++) {
            assertEquals(startDate.plusDays((long) i * paymentScheme.getFrequencyInDays()), 
                    installments.get(i).getScheduledPaymentDate());
        }
    }

    @Test
    void shouldCalculateCommission() {
        // Given
        BigDecimal amount = new BigDecimal("1000.00");
        
        // When
        BigDecimal commission1 = installmentCalculator.calculateCommission(amount, PaymentScheme.SCHEME_1);
        BigDecimal commission2 = installmentCalculator.calculateCommission(amount, PaymentScheme.SCHEME_2);
        
        // Then
        assertEquals(new BigDecimal("130.00"), commission1);
        assertEquals(new BigDecimal("160.00"), commission2);
    }
}