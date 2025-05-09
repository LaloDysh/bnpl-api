package com.bnpl.domain.model.loan;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Loan {
    private final UUID id;
    private final UUID customerId;
    private final BigDecimal amount;
    private final PaymentScheme paymentScheme;
    private final BigDecimal commissionAmount;
    private final BigDecimal totalAmount;
    private final List<Installment> installments;
    private LoanStatus status;
    private final LocalDateTime createdAt;

    private Loan(Builder builder) {
        this.id = builder.id;
        this.customerId = builder.customerId;
        this.amount = builder.amount;
        this.paymentScheme = builder.paymentScheme;
        this.commissionAmount = builder.commissionAmount;
        this.totalAmount = builder.amount.add(builder.commissionAmount);
        this.installments = builder.installments;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentScheme getPaymentScheme() {
        return paymentScheme;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<Installment> getInstallments() {
        return Collections.unmodifiableList(installments);
    }

    public LoanStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void updateStatus() {
        boolean hasError = installments.stream()
                .anyMatch(installment -> installment.getStatus() == InstallmentStatus.ERROR);
        
        if (hasError) {
            this.status = LoanStatus.LATE;
            return;
        }

        boolean allCompleted = installments.stream()
                .allMatch(installment -> installment.getStatus() == InstallmentStatus.PAID);
        
        if (allCompleted) {
            this.status = LoanStatus.COMPLETED;
        } else {
            this.status = LoanStatus.ACTIVE;
        }
    }

    public static class Builder {
        private UUID id;
        private UUID customerId;
        private BigDecimal amount;
        private PaymentScheme paymentScheme;
        private BigDecimal commissionAmount;
        private List<Installment> installments = new ArrayList<>();
        private LoanStatus status = LoanStatus.ACTIVE;
        private LocalDateTime createdAt;

        private Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder customerId(UUID customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder paymentScheme(PaymentScheme paymentScheme) {
            this.paymentScheme = paymentScheme;
            return this;
        }

        public Builder commissionAmount(BigDecimal commissionAmount) {
            this.commissionAmount = commissionAmount;
            return this;
        }

        public Builder installments(List<Installment> installments) {
            this.installments = new ArrayList<>(installments);
            return this;
        }

        public Builder status(LoanStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Loan build() {
            return new Loan(this);
        }
    }
}
