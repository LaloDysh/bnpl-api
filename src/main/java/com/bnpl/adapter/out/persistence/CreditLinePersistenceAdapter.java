package com.bnpl.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.bnpl.adapter.out.persistence.entity.CreditLineEntity;
import com.bnpl.adapter.out.persistence.mapper.CreditLineEntityMapper;
import com.bnpl.adapter.out.persistence.repository.CreditLineJpaRepository;
import com.bnpl.domain.model.CreditLine;
import com.bnpl.domain.port.out.CreditLineRepository;

@Component
public class CreditLinePersistenceAdapter implements CreditLineRepository {
    
    private final CreditLineJpaRepository creditLineJpaRepository;
    private final CreditLineEntityMapper creditLineEntityMapper;
    
    public CreditLinePersistenceAdapter(
            CreditLineJpaRepository creditLineJpaRepository,
            CreditLineEntityMapper creditLineEntityMapper) {
        this.creditLineJpaRepository = creditLineJpaRepository;
        this.creditLineEntityMapper = creditLineEntityMapper;
    }
    
    @Override
    public CreditLine save(CreditLine creditLine) {
        CreditLineEntity entity = creditLineEntityMapper.toEntity(creditLine);
        CreditLineEntity savedEntity = creditLineJpaRepository.save(entity);
        return creditLineEntityMapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<CreditLine> findByClientId(Long clientId) {
        return creditLineJpaRepository.findByClientId(clientId)
                .map(creditLineEntityMapper::toDomain);
    }
    
    @Override
    public void update(CreditLine creditLine) {
        creditLineJpaRepository.findById(creditLine.getId())
                .ifPresent(entity -> {
                    entity.setAvailableAmount(creditLine.getAvailableAmount());
                    creditLineJpaRepository.save(entity);
                });
    }
}
