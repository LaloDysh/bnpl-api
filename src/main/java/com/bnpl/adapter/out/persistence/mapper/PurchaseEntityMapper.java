package com.bnpl.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.bnpl.adapter.out.persistence.entity.PurchaseEntity;
import com.bnpl.domain.model.Purchase;

@Component
public class PurchaseEntityMapper {
    
    public PurchaseEntity toEntity(Purchase domain) {
        return new PurchaseEntity(
                domain.getId(),
                domain.getClientId(),
                domain.getAmount(),
                domain.getPurchaseDate(),
                domain.getSchemeType()
        );
    }
    
    public Purchase toDomain(PurchaseEntity entity) {
        return new Purchase(
                entity.getId(),
                entity.getClientId(),
                entity.getAmount(),
                entity.getPurchaseDate(),
                entity.getSchemeType()
        );
    }
}