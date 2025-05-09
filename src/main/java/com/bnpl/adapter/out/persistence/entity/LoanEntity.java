package com.bnpl.adapter.out.persistence.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "loans")
public class LoanEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "purchase_id", nullable = false)
    private Long purchaseId;
    
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
    
    @Column(name = "installment_amount", nullable = false)
    private BigDecimal installmentAmount;
    
    @Column(name = "commission", nullable = false)
    private BigDecimal commission;
    
    public LoanEntity() {
    }
    
    public LoanEntity(Long id, Long purchaseId, BigDecimal totalAmount, BigDecimal installmentAmount, BigDecimal commission) {
        this.id = id;
        this.purchaseId = purchaseId;
        this.totalAmount = totalAmount;
        this.installmentAmount = installmentAmount;
        this.commission = commission;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getPurchaseId() {
        return purchaseId;
    }
    
    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }
    
    public void setInstallmentAmount(BigDecimal installmentAmount) {
        this.installmentAmount = installmentAmount;
    }
    
    public BigDecimal getCommission() {
        return commission;
    }
    
    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }
}