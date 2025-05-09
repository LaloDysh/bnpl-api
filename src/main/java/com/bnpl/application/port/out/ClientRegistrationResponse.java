package com.bnpl.application.port.out;

import java.math.BigDecimal;

public class ClientRegistrationResponse {
    private final Long clientId;
    private final BigDecimal assignedCreditAmount;
    
    public ClientRegistrationResponse(Long clientId, BigDecimal assignedCreditAmount) {
        this.clientId = clientId;
        this.assignedCreditAmount = assignedCreditAmount;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public BigDecimal getAssignedCreditAmount() {
        return assignedCreditAmount;
    }
}
