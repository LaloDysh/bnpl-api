package com.bnpl.domain.model.loan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Installment {
    private final UUID id;
    private final UUID loanId;
    private final int installmentNumber;
    private final BigDecimal amount;
    private final LocalDate scheduledPaymentDate;
    private InstallmentStatus status;

    private Installment(Builder builder) {
        this.id = builder.id;
        this.loanId = builder.loanId;
        this.installmentNumber = builder.installmentNumber;
        this.amount = builder.amount;
        this.scheduledPaymentDate = builder.scheduledPaymentDate;
        this.status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public UUID getLoanId() {
        return loanId;
    }

    public int getInstallmentNumber() {
        return installmentNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getScheduledPaymentDate() {
        return scheduledPaymentDate;
    }

    public InstallmentStatus getStatus() {
        return status;
    }

    public void setStatus(InstallmentStatus status) {
        this.status = status;
    }

    public static class Builder {
        private UUID id;
        private UUID loanId;
        private int installmentNumber;
        private BigDecimal amount;
        private LocalDate scheduledPaymentDate;
        private InstallmentStatus status = InstallmentStatus.PENDING;

        private Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder loanId(UUID loanId) {
            this.loanId = loanId;
            return this;
        }

        public Builder installmentNumber(int installmentNumber) {
            this.installmentNumber = installmentNumber;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder scheduledPaymentDate(LocalDate scheduledPaymentDate) {
            this.scheduledPaymentDate = scheduledPaymentDate;
            return this;
        }

        public Builder status(InstallmentStatus status) {
            this.status = status;
            return this;
        }

        public Installment build() {
            return new Installment(this);
        }
    }
}
