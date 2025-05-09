package com.bnpl.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bnpl.domain.exception.InsufficientCreditException;
import com.bnpl.domain.model.CreditLine;
import com.bnpl.domain.port.out.CreditLineRepository;

@ExtendWith(MockitoExtension.class)
public class CreditLineServiceTest {
    
    @Mock
    private CreditLineRepository creditLineRepository;
    
    private CreditLineService creditLineService;
    
    @BeforeEach
    public void setup() {
        creditLineService = new CreditLineService(creditLineRepository);
    }
    
    @Test
    public void createCreditLine_shouldSaveCreditLine() {
        // Arrange
        Long clientId = 1L;
        BigDecimal amount = new BigDecimal("5000");
        CreditLine creditLine = new CreditLine(null, clientId, amount);
        
        when(creditLineRepository.save(any(CreditLine.class)))
            .thenReturn(new CreditLine(1L, clientId, amount));
        
        // Act
        CreditLine result = creditLineService.createCreditLine(clientId, amount);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(clientId, result.getClientId());
        assertEquals(amount, result.getTotalAmount());
        assertEquals(amount, result.getAvailableAmount());
        
        verify(creditLineRepository, times(1)).save(any(CreditLine.class));
    }
    
    @Test
    public void validateCreditAvailability_withSufficientCredit_shouldNotThrowException() {
        // Arrange
        CreditLine creditLine = new CreditLine(1L, 1L, new BigDecimal("5000"));
        BigDecimal purchaseAmount = new BigDecimal("3000");
        
        // Act & Assert
        assertDoesNotThrow(() -> {
            creditLineService.validateCreditAvailability(creditLine, purchaseAmount);
        });
    }
    
    @Test
    public void validateCreditAvailability_withInsufficientCredit_shouldThrowException() {
        // Arrange
        CreditLine creditLine = new CreditLine(1L, 1L, new BigDecimal("2000"));
        BigDecimal purchaseAmount = new BigDecimal("3000");
        
        // Act & Assert
        assertThrows(InsufficientCreditException.class, () -> {
            creditLineService.validateCreditAvailability(creditLine, purchaseAmount);
        });
    }
    
    @Test
    public void reduceCreditLine_shouldUpdateAvailableAmount() {
        // Arrange
        CreditLine creditLine = new CreditLine(1L, 1L, new BigDecimal("5000"));
        BigDecimal amount = new BigDecimal("2000");
        
        // Act
        creditLineService.reduceCreditLine(creditLine, amount);
        
        // Assert
        assertEquals(new BigDecimal("3000"), creditLine.getAvailableAmount());
        verify(creditLineRepository, times(1)).update(creditLine);
    }
}
