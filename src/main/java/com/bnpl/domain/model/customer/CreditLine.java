package com.bnpl.domain.model.customer;

import java.math.BigDecimal;

public class CreditLine {
    private final BigDecimal totalAmount;
    private BigDecimal availableAmount;

    private CreditLine(BigDecimal totalAmount, BigDecimal availableAmount) {
        this.totalAmount = totalAmount;
        this.availableAmount = availableAmount;
    }

    public static CreditLine of(BigDecimal amount) {
        return new CreditLine(amount, amount);
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void deduct(BigDecimal amount) {
        if (amount.compareTo(availableAmount) > 0) {
            throw new IllegalArgumentException("Insufficient available credit line");
        }
        availableAmount = availableAmount.subtract(amount);
    }

    public void restore(BigDecimal amount) {
        availableAmount = availableAmount.add(amount);
        if (availableAmount.compareTo(totalAmount) > 0) {
            availableAmount = totalAmount;
        }
    }
}