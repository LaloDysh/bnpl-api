package com.bnpl.application.usecase.loan;

import com.bnpl.application.port.out.loan.LoanRepository;
import com.bnpl.domain.model.error.BnplException;
import com.bnpl.domain.model.loan.Loan;
import com.bnpl.domain.model.loan.LoanStatus;
import com.bnpl.domain.model.loan.PaymentScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetLoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    private GetLoanService getLoanService;

    @BeforeEach
    void setUp() {
        getLoanService = new GetLoanService(loanRepository);
    }

    @Test
    void shouldGetLoanByIdSuccessfully() {
        // Given
        UUID loanId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        
        Loan loan = Loan.builder()
                .id(loanId)
                .customerId(customerId)
                .amount(new BigDecimal("1000.00"))
                .commissionAmount(new BigDecimal("130.00"))
                .paymentScheme(PaymentScheme.SCHEME_1)
                .installments(new ArrayList<>())
                .status(LoanStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        // When
        Loan result = getLoanService.getLoanById(loanId);

        // Then
        assertNotNull(result);
        assertEquals(loanId, result.getId());
        assertEquals(customerId, result.getCustomerId());
        assertEquals(new BigDecimal("1000.00"), result.getAmount());
        assertEquals(new BigDecimal("130.00"), result.getCommissionAmount());
        assertEquals(PaymentScheme.SCHEME_1, result.getPaymentScheme());
        assertEquals(LoanStatus.ACTIVE, result.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenLoanNotFound() {
        // Given
        UUID loanId = UUID.randomUUID();
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        // When & Then
        BnplException exception = assertThrows(
                BnplException.LoanNotFoundException.class,
                () -> getLoanService.getLoanById(loanId)
        );
        assertEquals("APZ000008", exception.getCode());
        assertEquals("LOAN_NOT_FOUND", exception.getError());
        assertEquals("/v1/loans/" + loanId, exception.getPath());
    }
}