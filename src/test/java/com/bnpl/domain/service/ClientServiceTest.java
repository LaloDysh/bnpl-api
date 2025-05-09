package com.bnpl.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bnpl.domain.exception.ClientNotEligibleException;
import com.bnpl.domain.model.Client;
import com.bnpl.domain.port.out.ClientRepository;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {
    
    @Mock
    private ClientRepository clientRepository;
    
    private ClientService clientService;
    
    @BeforeEach
    public void setup() {
        clientService = new ClientService(clientRepository);
    }
    
    @Test
    public void registerClient_withValidAge_shouldRegisterClient() {
        // Arrange
        LocalDate birthDate = LocalDate.now().minusYears(30);
        Client client = new Client(null, "John Doe", birthDate);
        
        when(clientRepository.save(client)).thenReturn(new Client(1L, "John Doe", birthDate));
        
        // Act
        Client registeredClient = clientService.registerClient(client);
        
        // Assert
        assertNotNull(registeredClient);
        assertEquals(1L, registeredClient.getId());
        verify(clientRepository, times(1)).save(client);
    }
    
    @Test
    public void registerClient_withInvalidAge_shouldThrowException() {
        // Arrange
        LocalDate birthDate = LocalDate.now().minusYears(16);
        Client client = new Client(null, "Young Client", birthDate);
        
        // Act & Assert
        assertThrows(ClientNotEligibleException.class, () -> {
            clientService.registerClient(client);
        });
        
        verify(clientRepository, never()).save(any(Client.class));
    }
    
    @Test
    public void registerClient_withAgeOverLimit_shouldThrowException() {
        // Arrange
        LocalDate birthDate = LocalDate.now().minusYears(70);
        Client client = new Client(null, "Old Client", birthDate);
        
        // Act & Assert
        assertThrows(ClientNotEligibleException.class, () -> {
            clientService.registerClient(client);
        });
        
        verify(clientRepository, never()).save(any(Client.class));
    }
    
    @Test
    public void determineCreditLineAmount_forAgeBetween18And25_shouldReturn3000() {
        // Act
        BigDecimal amount = clientService.determineCreditLineAmount(20);
        
        // Assert
        assertEquals(new BigDecimal("3000"), amount);
    }
    
    @Test
    public void determineCreditLineAmount_forAgeBetween26And30_shouldReturn5000() {
        // Act
        BigDecimal amount = clientService.determineCreditLineAmount(28);
        
        // Assert
        assertEquals(new BigDecimal("5000"), amount);
    }
    
    @Test
    public void determineCreditLineAmount_forAgeBetween31And65_shouldReturn8000() {
        // Act
        BigDecimal amount = clientService.determineCreditLineAmount(40);
        
        // Assert
        assertEquals(new BigDecimal("8000"), amount);
    }
    
    @Test
    public void determineCreditLineAmount_forInvalidAge_shouldThrowException() {
        // Act & Assert
        assertThrows(ClientNotEligibleException.class, () -> {
            clientService.determineCreditLineAmount(17);
        });
        
        assertThrows(ClientNotEligibleException.class, () -> {
            clientService.determineCreditLineAmount(66);
        });
    }
}
