package com.bnpl.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.bnpl.adapter.out.persistence.entity.PurchaseEntity;
import com.bnpl.adapter.out.persistence.mapper.PurchaseEntityMapper;
import com.bnpl.adapter.out.persistence.repository.PurchaseJpaRepository;
import com.bnpl.domain.model.Purchase;
import com.bnpl.domain.port.out.PurchaseRepository;

@Component
public class PurchasePersistenceAdapter implements PurchaseRepository {
    
    private final PurchaseJpaRepository purchaseJpaRepository;
    private final PurchaseEntityMapper purchaseEntityMapper;
    
    public PurchasePersistenceAdapter(
            PurchaseJpaRepository purchaseJpaRepository,
            PurchaseEntityMapper purchaseEntityMapper) {
        this.purchaseJpaRepository = purchaseJpaRepository;
        this.purchaseEntityMapper = purchaseEntityMapper;
    }
    
    @Override
    public Purchase save(Purchase purchase) {
        PurchaseEntity entity = purchaseEntityMapper.toEntity(purchase);
        PurchaseEntity savedEntity = purchaseJpaRepository.save(entity);
        return purchaseEntityMapper.toDomain(savedEntity);
    }
}