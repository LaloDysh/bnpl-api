package com.bnpl.domain.port.out;

import com.bnpl.domain.model.Loan;

public interface LoanRepository {
    Loan save(Loan loan);
}
