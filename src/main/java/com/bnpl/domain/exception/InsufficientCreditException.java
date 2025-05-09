package com.bnpl.domain.exception;

import java.math.BigDecimal;

public class InsufficientCreditException extends BusinessException {
    public InsufficientCreditException(String message) {
        super(message);
    }
    
    public static InsufficientCreditException create(BigDecimal requested, BigDecimal available) {
        return new InsufficientCreditException(
            String.format("Insufficient credit. Requested: %s, Available: %s", requested, available)
        );
    }
}