package com.bnpl.application.port.out.loan;

import com.bnpl.domain.model.loan.Loan;

import java.util.Optional;
import java.util.UUID;

public interface LoanRepository {
    Loan save(Loan loan);
    Optional<Loan> findById(UUID loanId);
}