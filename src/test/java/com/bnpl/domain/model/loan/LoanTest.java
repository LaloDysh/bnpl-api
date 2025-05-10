package com.bnpl.domain.model.loan;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class LoanTest {

    @Test
    void testBuilder() {
        // Given
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("1000.00");
        PaymentScheme paymentScheme = PaymentScheme.SCHEME_1;
        BigDecimal commissionAmount = new BigDecimal("130.00");
        List<Installment> installments = new ArrayList<>();
        LoanStatus status = LoanStatus.ACTIVE;
        LocalDateTime createdAt = LocalDateTime.now();
        
        // When
        Loan loan = Loan.builder()
                .id(id)
                .customerId(customerId)
                .amount(amount)
                .paymentScheme(paymentScheme)
                .commissionAmount(commissionAmount)
                .installments(installments)
                .status(status)
                .createdAt(createdAt)
                .build();
        
        // Then
        assertEquals(id, loan.getId());
        assertEquals(customerId, loan.getCustomerId());
        assertEquals(amount, loan.getAmount());
        assertEquals(paymentScheme, loan.getPaymentScheme());
        assertEquals(commissionAmount, loan.getCommissionAmount());
        assertEquals(amount.add(commissionAmount), loan.getTotalAmount());
        assertEquals(installments, loan.getInstallments());
        assertEquals(status, loan.getStatus());
        assertEquals(createdAt, loan.getCreatedAt());
    }

    @Test
    void testUpdateStatusToLate() {
        // Given
        UUID loanId = UUID.randomUUID();
        List<Installment> installments = new ArrayList<>();
        
        Installment errorInstallment = Installment.builder()
                .id(UUID.randomUUID())
                .loanId(loanId)
                .installmentNumber(1)
                .amount(new BigDecimal("200.00"))
                .scheduledPaymentDate(LocalDate.now())
                .status(InstallmentStatus.ERROR)
                .build();
        installments.add(errorInstallment);
        
        Loan loan = Loan.builder()
                .id(loanId)
                .customerId(UUID.randomUUID())
                .amount(new BigDecimal("1000.00"))
                .commissionAmount(new BigDecimal("130.00"))
                .installments(installments)
                .status(LoanStatus.ACTIVE)
                .build();
        
        // When
        loan.updateStatus();
        
        // Then
        assertEquals(LoanStatus.LATE, loan.getStatus());
    }

    @Test
    void testUpdateStatusToActive() {
        // Given
        UUID loanId = UUID.randomUUID();
        List<Installment> installments = new ArrayList<>();
        
        Installment pendingInstallment = Installment.builder()
                .id(UUID.randomUUID())
                .loanId(loanId)
                .installmentNumber(1)
                .amount(new BigDecimal("200.00"))
                .scheduledPaymentDate(LocalDate.now())
                .status(InstallmentStatus.PENDING)
                .build();
        installments.add(pendingInstallment);
        
        Loan loan = Loan.builder()
                .id(loanId)
                .customerId(UUID.randomUUID())
                .amount(new BigDecimal("1000.00"))
                .commissionAmount(new BigDecimal("130.00"))
                .installments(installments)
                .status(LoanStatus.COMPLETED) // Comenzamos con COMPLETED pero debería cambiar a ACTIVE
                .build();
        
        // When
        loan.updateStatus();
        
        // Then
        assertEquals(LoanStatus.ACTIVE, loan.getStatus());
    }

    @Test
    void testUpdateStatusToCompleted() {
        // Given
        UUID loanId = UUID.randomUUID();
        List<Installment> installments = new ArrayList<>();
        
        Installment paidInstallment = Installment.builder()
                .id(UUID.randomUUID())
                .loanId(loanId)
                .installmentNumber(1)
                .amount(new BigDecimal("200.00"))
                .scheduledPaymentDate(LocalDate.now())
                .status(InstallmentStatus.PAID)
                .build();
        installments.add(paidInstallment);
        
        Loan loan = Loan.builder()
                .id(loanId)
                .customerId(UUID.randomUUID())
                .amount(new BigDecimal("1000.00"))
                .commissionAmount(new BigDecimal("130.00"))
                .installments(installments)
                .status(LoanStatus.ACTIVE)
                .build();
        
        // When
        loan.updateStatus();
        
        // Then
        assertEquals(LoanStatus.COMPLETED, loan.getStatus());
    }

    @Test
    void testGetInstallmentsReturnsUnmodifiableList() {
        // Given
        UUID loanId = UUID.randomUUID();
        List<Installment> installments = new ArrayList<>();
        
        Installment installment = Installment.builder()
                .id(UUID.randomUUID())
                .loanId(loanId)
                .installmentNumber(1)
                .amount(new BigDecimal("200.00"))
                .scheduledPaymentDate(LocalDate.now())
                .status(InstallmentStatus.PENDING)
                .build();
        installments.add(installment);
        
        Loan loan = Loan.builder()
                .id(loanId)
                .customerId(UUID.randomUUID())
                .amount(new BigDecimal("1000.00"))
                .commissionAmount(new BigDecimal("130.00"))
                .installments(installments)
                .status(LoanStatus.ACTIVE)
                .build();
        
        // When & Then
        assertThrows(UnsupportedOperationException.class, () -> 
            loan.getInstallments().add(installment)
        );
    }
}