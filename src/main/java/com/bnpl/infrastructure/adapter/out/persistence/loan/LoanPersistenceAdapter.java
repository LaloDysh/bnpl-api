package com.bnpl.infrastructure.adapter.out.persistence.loan;

import com.bnpl.application.port.out.loan.LoanRepository;
import com.bnpl.domain.model.loan.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class LoanPersistenceAdapter implements LoanRepository {

    private static final Logger log = LoggerFactory.getLogger(LoanPersistenceAdapter.class);

    private final LoanJpaRepository loanJpaRepository;
    private final LoanPersistenceMapper loanPersistenceMapper;

    public LoanPersistenceAdapter(
            LoanJpaRepository loanJpaRepository,
            LoanPersistenceMapper loanPersistenceMapper) {
        this.loanJpaRepository = loanJpaRepository;
        this.loanPersistenceMapper = loanPersistenceMapper;
    }

    @Override
    public Loan save(Loan loan) {
        log.debug("Saving loan with ID: {}", loan.getId());
        
        LoanEntity entity = loanPersistenceMapper.toEntity(loan);
        LoanEntity savedEntity = loanJpaRepository.save(entity);
        
        return loanPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Loan> findById(UUID loanId) {
        log.debug("Finding loan with ID: {}", loanId);
        
        return loanJpaRepository.findById(loanId)
                .map(loanPersistenceMapper::toDomain);
    }
}
