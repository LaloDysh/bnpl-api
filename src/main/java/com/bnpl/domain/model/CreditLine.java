package com.bnpl.domain.model;

import java.math.BigDecimal;

public class CreditLine {
    private Long id;
    private Long clientId;
    private BigDecimal totalAmount;
    private BigDecimal availableAmount;
    
    public CreditLine(Long id, Long clientId, BigDecimal totalAmount) {
        this.id = id;
        this.clientId = clientId;
        this.totalAmount = totalAmount;
        this.availableAmount = totalAmount;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }
    
    // Domain logic
    public boolean hasSufficientCredit(BigDecimal purchaseAmount) {
        return availableAmount.compareTo(purchaseAmount) >= 0;
    }
    
    public void reduceCreditBy(BigDecimal amount) {
        this.availableAmount = this.availableAmount.subtract(amount);
    }
    
    public void increaseCreditBy(BigDecimal amount) {
        this.availableAmount = this.availableAmount.add(amount);
        if (this.availableAmount.compareTo(this.totalAmount) > 0) {
            this.availableAmount = this.totalAmount;
        }
    }
}
