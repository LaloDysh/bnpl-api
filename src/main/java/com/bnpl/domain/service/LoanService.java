package com.bnpl.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bnpl.domain.model.Loan;
import com.bnpl.domain.port.out.LoanRepository;

public class LoanService {
     private static final Logger log = LoggerFactory.getLogger(LoanService.class);
    
    private final LoanRepository loanRepository;
    
    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }
    
    public Loan createLoan(Long purchaseId, BigDecimal totalAmount, BigDecimal installmentAmount, 
                         List<LocalDate> paymentDates, BigDecimal commission) {
        log.info("Creating loan for purchase {}: total amount {}, installment amount {}", 
                 purchaseId, totalAmount, installmentAmount);
        
        Loan loan = new Loan(null, purchaseId, totalAmount, installmentAmount, paymentDates, commission);
        return loanRepository.save(loan);
    }
}
