package com.bnpl.domain.exception;

public class ClientNotEligibleException extends BusinessException{
    public ClientNotEligibleException(String message) {
        super(message);
    }
    
    public static ClientNotEligibleException becauseOfAge(int age) {
        return new ClientNotEligibleException(
            String.format("Client with age %d is not eligible for credit. Age must be between 18 and 65.", age)
        );
    }
}
