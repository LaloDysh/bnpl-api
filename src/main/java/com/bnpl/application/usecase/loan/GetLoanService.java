package com.bnpl.application.usecase.loan;

import com.bnpl.application.port.in.loan.GetLoanUseCase;
import com.bnpl.application.port.out.loan.LoanRepository;
import com.bnpl.domain.model.error.BnplException;
import com.bnpl.domain.model.loan.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetLoanService implements GetLoanUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetLoanService.class);

    private final LoanRepository loanRepository;

    public GetLoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Loan getLoanById(UUID loanId) {
        log.info("Getting loan with ID: {}", loanId);
        
        return loanRepository.findById(loanId)
                .orElseThrow(() -> {
                    log.error("Loan not found with ID: {}", loanId);
                    return new BnplException.LoanNotFoundException("/v1/loans/" + loanId);
                });
    }
}
