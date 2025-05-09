package com.bnpl.application.service;

import java.math.BigDecimal;
import com.bnpl.application.port.in.ClientRegistrationCommand;
import com.bnpl.application.port.out.ClientRegistrationResponse;
import com.bnpl.domain.model.Client;
import com.bnpl.domain.model.CreditLine;
import com.bnpl.domain.port.in.ClientRegistrationUseCase;
import com.bnpl.domain.service.ClientService;
import com.bnpl.domain.service.CreditLineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class ClientRegistrationService implements ClientRegistrationUseCase {
    private static final Logger log = LoggerFactory.getLogger(ClientRegistrationService.class);
    
    private final ClientService clientService;
    private final CreditLineService creditLineService;
    
    public ClientRegistrationService(ClientService clientService, CreditLineService creditLineService) {
        this.clientService = clientService;
        this.creditLineService = creditLineService;
    }
    
    @Transactional
    public ClientRegistrationResponse registerClientWithResponse(ClientRegistrationCommand command) {
        log.info("Processing client registration: {}", command.getName());
        
        // Create and register client
        Client client = new Client(null, command.getName(), command.getBirthDate());
        Client registeredClient = clientService.registerClient(client);
        
        // Determine credit line amount
        BigDecimal creditAmount = clientService.determineCreditLineAmount(registeredClient.getAge());
        
        // Create credit line
        CreditLine creditLine = creditLineService.createCreditLine(registeredClient.getId(), creditAmount);
        
        log.info("Client registration completed: id {}, credit amount {}", 
                registeredClient.getId(), creditLine.getTotalAmount());
        
        return new ClientRegistrationResponse(registeredClient.getId(), creditLine.getTotalAmount());
    }

    @Override
    public CreditLine registerClient(Client client) {
        // This method implements the domain port interface
        Client registeredClient = clientService.registerClient(client);
        BigDecimal creditAmount = clientService.determineCreditLineAmount(registeredClient.getAge());
        return creditLineService.createCreditLine(registeredClient.getId(), creditAmount);
    }
}
