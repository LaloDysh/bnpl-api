package com.bnpl.adapter.in.web.dto.request;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PurchaseRegistrationRequest {
    @NotNull(message = "Client ID is required")
    private Long idCliente;
    
    @NotNull(message = "Purchase amount is required")
    @Positive(message = "Purchase amount must be positive")
    private BigDecimal montoCompra;
    
    // Default constructor for JSON deserialization
    public PurchaseRegistrationRequest() {
    }
    
    public PurchaseRegistrationRequest(Long idCliente, BigDecimal montoCompra) {
        this.idCliente = idCliente;
        this.montoCompra = montoCompra;
    }
    
    public Long getIdCliente() {
        return idCliente;
    }
    
    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }
    
    public BigDecimal getMontoCompra() {
        return montoCompra;
    }
    
    public void setMontoCompra(BigDecimal montoCompra) {
        this.montoCompra = montoCompra;
    }
}