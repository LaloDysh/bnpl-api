package com.bnpl.application.port.in;

import java.math.BigDecimal;

public class PurchaseRegistrationCommand {
    private final Long clientId;
    private final BigDecimal amount;
    
    public PurchaseRegistrationCommand(Long clientId, BigDecimal amount) {
        this.clientId = clientId;
        this.amount = amount;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
}