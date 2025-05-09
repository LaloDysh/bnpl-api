package com.bnpl.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.bnpl.adapter.out.persistence.entity.CreditLineEntity;
import com.bnpl.domain.model.CreditLine;

@Component
public class CreditLineEntityMapper {
    
    public CreditLineEntity toEntity(CreditLine domain) {
        return new CreditLineEntity(
                domain.getId(),
                domain.getClientId(),
                domain.getTotalAmount(),
                domain.getAvailableAmount()
        );
    }
    
    public CreditLine toDomain(CreditLineEntity entity) {
        return new CreditLine(
                entity.getId(),
                entity.getClientId(),
                entity.getTotalAmount()
        );
    }
}