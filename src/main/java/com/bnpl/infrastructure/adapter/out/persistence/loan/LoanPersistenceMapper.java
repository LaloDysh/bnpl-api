package com.bnpl.infrastructure.adapter.out.persistence.loan;

import com.bnpl.domain.model.loan.Installment;
import com.bnpl.domain.model.loan.Loan;
import com.bnpl.domain.model.loan.PaymentScheme;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LoanPersistenceMapper {

    public LoanEntity toEntity(Loan domain) {
        LoanEntity entity = new LoanEntity();
        entity.setId(domain.getId());
        entity.setCustomerId(domain.getCustomerId());
        entity.setAmount(domain.getAmount());
        entity.setCommissionAmount(domain.getCommissionAmount());
        entity.setPaymentScheme(domain.getPaymentScheme().name());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        
        // Map installments
        List<InstallmentEntity> installmentEntities = domain.getInstallments().stream()
                .map(this::toInstallmentEntity)
                .collect(Collectors.toList());
        
        // Set bidirectional relationship
        for (InstallmentEntity installmentEntity : installmentEntities) {
            entity.addInstallment(installmentEntity);
        }
        
        return entity;
    }

    public Loan toDomain(LoanEntity entity) {
        return Loan.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .amount(entity.getAmount())
                .paymentScheme(PaymentScheme.valueOf(entity.getPaymentScheme()))
                .commissionAmount(entity.getCommissionAmount())
                .installments(mapInstallments(entity.getInstallments()))
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private InstallmentEntity toInstallmentEntity(Installment domain) {
        InstallmentEntity entity = new InstallmentEntity();
        entity.setId(domain.getId());
        entity.setInstallmentNumber(domain.getInstallmentNumber());
        entity.setAmount(domain.getAmount());
        entity.setScheduledPaymentDate(domain.getScheduledPaymentDate());
        entity.setStatus(domain.getStatus());
        return entity;
    }

    private List<Installment> mapInstallments(List<InstallmentEntity> entities) {
        return entities.stream()
                .map(this::toInstallmentDomain)
                .collect(Collectors.toList());
    }

    private Installment toInstallmentDomain(InstallmentEntity entity) {
        return Installment.builder()
                .id(entity.getId())
                .loanId(entity.getLoan().getId())
                .installmentNumber(entity.getInstallmentNumber())
                .amount(entity.getAmount())
                .scheduledPaymentDate(entity.getScheduledPaymentDate())
                .status(entity.getStatus())
                .build();
    }
}
