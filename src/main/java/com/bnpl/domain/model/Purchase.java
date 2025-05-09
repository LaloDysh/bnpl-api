package com.bnpl.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.bnpl.domain.model.enums.SchemeType;

public class Purchase {
    private Long id;
    private Long clientId;
    private BigDecimal amount;
    private LocalDate purchaseDate;
    private SchemeType schemeType;
    
    public Purchase(Long id, Long clientId, BigDecimal amount, LocalDate purchaseDate, SchemeType schemeType) {
        this.id = id;
        this.clientId = clientId;
        this.amount = amount;
        this.purchaseDate = purchaseDate;
        this.schemeType = schemeType;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }
    
    public SchemeType getSchemeType() {
        return schemeType;
    }
}
