package com.bnpl.adapter.out.persistence.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bnpl.adapter.out.persistence.entity.LoanEntity;
import com.bnpl.adapter.out.persistence.entity.PaymentEntity;
import com.bnpl.domain.model.Loan;

@Component
public class LoanEntityMapper {
    
    public Loan toDomain(LoanEntity entity, List<PaymentEntity> paymentEntities) {
        List<java.time.LocalDate> paymentDates = paymentEntities.stream()
                .map(PaymentEntity::getDueDate)
                .collect(Collectors.toList());
        
        return new Loan(
                entity.getId(),
                entity.getPurchaseId(),
                entity.getTotalAmount(),
                entity.getInstallmentAmount(),
                paymentDates,
                entity.getCommission()
        );
    }
}