package com.bnpl.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bnpl.application.port.in.ClientRegistrationCommand;
import com.bnpl.application.port.out.ClientRegistrationResponse;
import com.bnpl.domain.model.Client;
import com.bnpl.domain.model.CreditLine;
import com.bnpl.domain.service.ClientService;
import com.bnpl.domain.service.CreditLineService;

@ExtendWith(MockitoExtension.class)
public class ClientRegistrationServiceTest {
    
    @Mock
    private ClientService clientService;
    
    @Mock
    private CreditLineService creditLineService;
    
    private ClientRegistrationService clientRegistrationService;
    
    @BeforeEach
    public void setup() {
        clientRegistrationService = new ClientRegistrationService(clientService, creditLineService);
    }
    
    @Test
    public void registerClient_shouldReturnSuccessResponse() {
        // Arrange
        String name = "John Doe";
        LocalDate birthDate = LocalDate.now().minusYears(30);
        ClientRegistrationCommand command = new ClientRegistrationCommand(name, birthDate);
        
        Client client = new Client(null, name, birthDate);
        Client registeredClient = new Client(1L, name, birthDate);
        BigDecimal creditAmount = new BigDecimal("8000");
        CreditLine creditLine = new CreditLine(1L, 1L, creditAmount);
        
        when(clientService.registerClient(any(Client.class))).thenReturn(registeredClient);
        when(clientService.determineCreditLineAmount(registeredClient.getAge())).thenReturn(creditAmount);
        when(creditLineService.createCreditLine(registeredClient.getId(), creditAmount)).thenReturn(creditLine);
        
        // Act
        ClientRegistrationResponse response = clientRegistrationService.registerClient(command);
        
        // Assert
        assertNotNull(response);
        assertEquals(registeredClient.getId(), response.getClientId());
        assertEquals(creditAmount, response.getAssignedCreditAmount());
        
        verify(clientService, times(1)).registerClient(any(Client.class));
        verify(clientService, times(1)).determineCreditLineAmount(registeredClient.getAge());
        verify(creditLineService, times(1)).createCreditLine(registeredClient.getId(), creditAmount);
    }
    
    @Test
    public void registerClient_withDomainPort_shouldReturnCreditLine() {
        // Arrange
        String name = "John Doe";
        LocalDate birthDate = LocalDate.now().minusYears(30);
        Client client = new Client(null, name, birthDate);
        
        Client registeredClient = new Client(1L, name, birthDate);
        BigDecimal creditAmount = new BigDecimal("8000");
        CreditLine creditLine = new CreditLine(1L, 1L, creditAmount);
        
        when(clientService.registerClient(client)).thenReturn(registeredClient);
        when(clientService.determineCreditLineAmount(registeredClient.getAge())).thenReturn(creditAmount);
        when(creditLineService.createCreditLine(registeredClient.getId(), creditAmount)).thenReturn(creditLine);
        
        // Act
        CreditLine result = clientRegistrationService.registerClient(client);
        
        // Assert
        assertNotNull(result);
        assertEquals(creditLine.getId(), result.getId());
        assertEquals(creditLine.getClientId(), result.getClientId());
        assertEquals(creditLine.getTotalAmount(), result.getTotalAmount());
        
        verify(clientService, times(1)).registerClient(client);
        verify(clientService, times(1)).determineCreditLineAmount(registeredClient.getAge());
        verify(creditLineService, times(1)).createCreditLine(registeredClient.getId(), creditAmount);
    }
}