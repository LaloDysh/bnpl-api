package com.bnpl.infrastructure.adapter.in.web.customer.response;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerResponse {

    private UUID id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime createdAt;

    private BigDecimal creditLineAmount;

    private BigDecimal availableCreditLineAmount;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getCreditLineAmount() {
        return creditLineAmount;
    }

    public void setCreditLineAmount(BigDecimal creditLineAmount) {
        this.creditLineAmount = creditLineAmount;
    }

    public BigDecimal getAvailableCreditLineAmount() {
        return availableCreditLineAmount;
    }

    public void setAvailableCreditLineAmount(BigDecimal availableCreditLineAmount) {
        this.availableCreditLineAmount = availableCreditLineAmount;
    }
}