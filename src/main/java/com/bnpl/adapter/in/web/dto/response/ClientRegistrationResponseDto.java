package com.bnpl.adapter.in.web.dto.response;

import java.math.BigDecimal;

public class ClientRegistrationResponseDto {
    private Long idCliente;
    private BigDecimal assignedCreditAmount;
    
    public ClientRegistrationResponseDto() {
    }
    
    public ClientRegistrationResponseDto(Long idCliente, BigDecimal assignedCreditAmount) {
        this.idCliente = idCliente;
        this.assignedCreditAmount = assignedCreditAmount;
    }
    
    public Long getIdCliente() {
        return idCliente;
    }
    
    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }
    
    public BigDecimal getAssignedCreditAmount() {
        return assignedCreditAmount;
    }
    
    public void setAssignedCreditAmount(BigDecimal assignedCreditAmount) {
        this.assignedCreditAmount = assignedCreditAmount;
    }
}
