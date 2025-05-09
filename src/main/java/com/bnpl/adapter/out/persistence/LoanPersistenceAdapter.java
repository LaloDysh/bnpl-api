package com.bnpl.adapter.out.persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bnpl.adapter.out.persistence.entity.LoanEntity;
import com.bnpl.adapter.out.persistence.entity.PaymentEntity;
import com.bnpl.adapter.out.persistence.mapper.LoanEntityMapper;
import com.bnpl.adapter.out.persistence.repository.LoanJpaRepository;
import com.bnpl.adapter.out.persistence.repository.PaymentJpaRepository;
import com.bnpl.domain.model.Loan;
import com.bnpl.domain.port.out.LoanRepository;

@Component
public class LoanPersistenceAdapter implements LoanRepository {
    
    private final LoanJpaRepository loanJpaRepository;
    private final PaymentJpaRepository paymentJpaRepository;
    private final LoanEntityMapper loanEntityMapper;
    
    public LoanPersistenceAdapter(
            LoanJpaRepository loanJpaRepository,
            PaymentJpaRepository paymentJpaRepository,
            LoanEntityMapper loanEntityMapper) {
        this.loanJpaRepository = loanJpaRepository;
        this.paymentJpaRepository = paymentJpaRepository;
        this.loanEntityMapper = loanEntityMapper;
    }
    
    @Override
    @Transactional
    public Loan save(Loan loan) {
        // Create and save loan entity
        LoanEntity loanEntity = new LoanEntity(
                loan.getId(),
                loan.getPurchaseId(),
                loan.getTotalAmount(),
                loan.getInstallmentAmount(),
                loan.getCommission()
        );
        
        LoanEntity savedLoanEntity = loanJpaRepository.save(loanEntity);
        
        // Create and save payment entities
        List<PaymentEntity> paymentEntities = new ArrayList<>();
        for (LocalDate dueDate : loan.getPaymentDates()) {
            PaymentEntity paymentEntity = new PaymentEntity(
                    null,
                    savedLoanEntity.getId(),
                    dueDate,
                    loan.getInstallmentAmount()
            );
            paymentEntities.add(paymentJpaRepository.save(paymentEntity));
        }
        
        return loanEntityMapper.toDomain(savedLoanEntity, paymentEntities);
    }
}
