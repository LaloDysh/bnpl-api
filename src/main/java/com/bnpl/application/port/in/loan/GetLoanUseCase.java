package com.bnpl.application.port.in.loan;

import com.bnpl.domain.model.loan.Loan;

import java.util.UUID;

public interface GetLoanUseCase {
    Loan getLoanById(UUID loanId);
}