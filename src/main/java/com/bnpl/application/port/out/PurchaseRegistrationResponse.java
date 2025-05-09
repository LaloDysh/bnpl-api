package com.bnpl.application.port.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PurchaseRegistrationResponse {
    private final Long purchaseId;
    private final BigDecimal totalAmount;
    private final BigDecimal installmentAmount;
    private final List<LocalDate> paymentDates;
    
    public PurchaseRegistrationResponse(Long purchaseId, BigDecimal totalAmount, 
                                     BigDecimal installmentAmount, List<LocalDate> paymentDates) {
        this.purchaseId = purchaseId;
        this.totalAmount = totalAmount;
        this.installmentAmount = installmentAmount;
        this.paymentDates = paymentDates;
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
}
