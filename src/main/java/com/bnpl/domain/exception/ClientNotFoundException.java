package com.bnpl.domain.exception;

public class ClientNotFoundException extends BusinessException{
    public ClientNotFoundException(String message) {
        super(message);
    }
    
    public static ClientNotFoundException withId(Long id) {
        return new ClientNotFoundException(String.format("Client with id %d not found", id));
    }
}
