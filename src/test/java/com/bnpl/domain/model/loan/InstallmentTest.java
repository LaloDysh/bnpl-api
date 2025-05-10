package com.bnpl.domain.model.loan;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class InstallmentTest {

    @Test
    void testBuilder() {
        // Given
        UUID id = UUID.randomUUID();
        UUID loanId = UUID.randomUUID();
        int installmentNumber = 1;
        BigDecimal amount = new BigDecimal("200.00");
        LocalDate scheduledPaymentDate = LocalDate.now();
        InstallmentStatus status = InstallmentStatus.PENDING;
        
        // When
        Installment installment = Installment.builder()
                .id(id)
                .loanId(loanId)
                .installmentNumber(installmentNumber)
                .amount(amount)
                .scheduledPaymentDate(scheduledPaymentDate)
                .status(status)
                .build();
        
        // Then
        assertEquals(id, installment.getId());
        assertEquals(loanId, installment.getLoanId());
        assertEquals(installmentNumber, installment.getInstallmentNumber());
        assertEquals(amount, installment.getAmount());
        assertEquals(scheduledPaymentDate, installment.getScheduledPaymentDate());
        assertEquals(status, installment.getStatus());
    }

    @Test
    void testSetStatus() {
        // Given
        Installment installment = Installment.builder()
                .id(UUID.randomUUID())
                .loanId(UUID.randomUUID())
                .installmentNumber(1)
                .amount(new BigDecimal("200.00"))
                .scheduledPaymentDate(LocalDate.now())
                .status(InstallmentStatus.PENDING)
                .build();
        
        // When
        installment.setStatus(InstallmentStatus.PAID);
        
        // Then
        assertEquals(InstallmentStatus.PAID, installment.getStatus());
    }
}