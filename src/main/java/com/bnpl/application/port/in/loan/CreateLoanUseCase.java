package com.bnpl.application.port.in.loan;

import com.bnpl.domain.model.loan.Loan;

import java.math.BigDecimal;
import java.util.UUID;

public interface CreateLoanUseCase {
    Loan createLoan(UUID customerId, BigDecimal amount);
}