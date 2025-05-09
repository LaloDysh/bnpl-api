package com.bnpl.domain.exception;

public class BusinessException  extends RuntimeException{
    public BusinessException(String message) {
        super(message);
    }
}
