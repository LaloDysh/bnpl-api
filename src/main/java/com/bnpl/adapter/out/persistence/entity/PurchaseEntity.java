package com.bnpl.adapter.out.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.bnpl.domain.model.enums.SchemeType;

@Entity
@Table(name = "purchases")
public class PurchaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "client_id", nullable = false)
    private Long clientId;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "scheme_type", nullable = false)
    private SchemeType schemeType;
    
    public PurchaseEntity() {
    }
    
    public PurchaseEntity(Long id, Long clientId, BigDecimal amount, LocalDate purchaseDate, SchemeType schemeType) {
        this.id = id;
        this.clientId = clientId;
        this.amount = amount;
        this.purchaseDate = purchaseDate;
        this.schemeType = schemeType;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }
    
    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public SchemeType getSchemeType() {
        return schemeType;
    }
    
    public void setSchemeType(SchemeType schemeType) {
        this.schemeType = schemeType;
    }
}
