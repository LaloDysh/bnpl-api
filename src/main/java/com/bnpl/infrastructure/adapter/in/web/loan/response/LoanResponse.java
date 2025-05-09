package com.bnpl.infrastructure.adapter.in.web.loan.response;

import com.bnpl.domain.model.loan.LoanStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class LoanResponse {

    private UUID id;
    private UUID customerId;
    private BigDecimal amount;
    private LoanStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime createdAt;

    private PaymentPlanResponse paymentPlan;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public PaymentPlanResponse getPaymentPlan() {
        return paymentPlan;
    }

    public void setPaymentPlan(PaymentPlanResponse paymentPlan) {
        this.paymentPlan = paymentPlan;
    }

    public static class PaymentPlanResponse {
        private BigDecimal commissionAmount;
        private List<InstallmentResponse> installments;

        public BigDecimal getCommissionAmount() {
            return commissionAmount;
        }

        public void setCommissionAmount(BigDecimal commissionAmount) {
            this.commissionAmount = commissionAmount;
        }

        public List<InstallmentResponse> getInstallments() {
            return installments;
        }

        public void setInstallments(List<InstallmentResponse> installments) {
            this.installments = installments;
        }
    }
}