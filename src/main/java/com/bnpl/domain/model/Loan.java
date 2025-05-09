package com.bnpl.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Loan {
    private Long id;
    private Long purchaseId;
    private BigDecimal totalAmount;
    private BigDecimal installmentAmount;
    private List<LocalDate> paymentDates;
    private BigDecimal commission;
    
    public Loan(Long id, Long purchaseId, BigDecimal totalAmount, BigDecimal installmentAmount, 
                List<LocalDate> paymentDates, BigDecimal commission) {
        this.id = id;
        this.purchaseId = purchaseId;
        this.totalAmount = totalAmount;
        this.installmentAmount = installmentAmount;
        this.paymentDates = paymentDates;
        this.commission = commission;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public Long getPurchaseId() {
        return purchaseId;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }
    
    public List<LocalDate> getPaymentDates() {
        return paymentDates;
    }
    
    public BigDecimal getCommission() {
        return commission;
    }
}
