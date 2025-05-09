package com.bnpl.adapter.in.web.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PurchaseRegistrationResponseDto {
    private Long idCompra;
    private BigDecimal totalAmount;
    private BigDecimal installmentAmount;
    private List<LocalDate> paymentDates;
    
    public PurchaseRegistrationResponseDto() {
    }
    
    public PurchaseRegistrationResponseDto(Long idCompra, BigDecimal totalAmount, 
                                         BigDecimal installmentAmount, List<LocalDate> paymentDates) {
        this.idCompra = idCompra;
        this.totalAmount = totalAmount;
        this.installmentAmount = installmentAmount;
        this.paymentDates = paymentDates;
    }
    
    public Long getIdCompra() {
        return idCompra;
    }
    
    public void setIdCompra(Long idCompra) {
        this.idCompra = idCompra;
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
    
    public List<LocalDate> getPaymentDates() {
        return paymentDates;
    }
    
    public void setPaymentDates(List<LocalDate> paymentDates) {
        this.paymentDates = paymentDates;
    }
}
