package com.bnpl.domain.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bnpl.domain.exception.ClientNotEligibleException;
import com.bnpl.domain.model.Client;
import com.bnpl.domain.port.out.ClientRepository;

public class ClientService {
    private static final Logger log = LoggerFactory.getLogger(ClientService.class);
    
    private final ClientRepository clientRepository;
    
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }
    
    public Client registerClient(Client client) {
        log.info("Registering new client: {}", client.getName());
        
        if (!client.isEligibleForCredit()) {
            log.warn("Client {} not eligible for credit due to age: {}", client.getName(), client.getAge());
            throw ClientNotEligibleException.becauseOfAge(client.getAge());
        }
        
        return clientRepository.save(client);
    }
    
    public BigDecimal determineCreditLineAmount(int age) {
        if (age >= 18 && age <= 25) {
            return new BigDecimal("3000");
        } else if (age >= 26 && age <= 30) {
            return new BigDecimal("5000");
        } else if (age >= 31 && age <= 65) {
            return new BigDecimal("8000");
        } else {
            throw ClientNotEligibleException.becauseOfAge(age);
        }
    }
}
