package com.bnpl.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bnpl.domain.model.Client;
import com.bnpl.domain.model.PaymentScheme;
import com.bnpl.domain.model.enums.SchemeType;

public class PaymentSchemeServiceTest {
    
    private PaymentSchemeService paymentSchemeService;
    
    @BeforeEach
    public void setup() {
        paymentSchemeService = new PaymentSchemeService();
    }
    
    @Test
    public void determinePaymentScheme_withNameStartingWithC_shouldReturnScheme1() {
        // Arrange
        Client client = new Client(1L, "Carlos", LocalDate.now().minusYears(30));
        
        // Act
        PaymentScheme scheme = paymentSchemeService.determinePaymentScheme(client);
        
        // Assert
        assertEquals(SchemeType.SCHEME_1, scheme.getSchemeType());
    }
    
    @Test
    public void determinePaymentScheme_withNameStartingWithL_shouldReturnScheme1() {
        // Arrange
        Client client = new Client(1L, "Laura", LocalDate.now().minusYears(30));
        
        // Act
        PaymentScheme scheme = paymentSchemeService.determinePaymentScheme(client);
        
        // Assert
        assertEquals(SchemeType.SCHEME_1, scheme.getSchemeType());
    }
    
    @Test
    public void determinePaymentScheme_withNameStartingWithH_shouldReturnScheme1() {
        // Arrange
        Client client = new Client(1L, "Henry", LocalDate.now().minusYears(30));
        
        // Act
        PaymentScheme scheme = paymentSchemeService.determinePaymentScheme(client);
        
        // Assert
        assertEquals(SchemeType.SCHEME_1, scheme.getSchemeType());
    }
    
    @Test
    public void determinePaymentScheme_withIdGreaterThan25_shouldReturnScheme2() {
        // Arrange
        Client client = new Client(26L, "John", LocalDate.now().minusYears(30));
        
        // Act
        PaymentScheme scheme = paymentSchemeService.determinePaymentScheme(client);
        
        // Assert
        assertEquals(SchemeType.SCHEME_2, scheme.getSchemeType());
    }
    
    @Test
    public void determinePaymentScheme_withNoMatchingRule_shouldReturnScheme2() {
        // Arrange
        Client client = new Client(20L, "John", LocalDate.now().minusYears(30));
        
        // Act
        PaymentScheme scheme = paymentSchemeService.determinePaymentScheme(client);
        
        // Assert
        assertEquals(SchemeType.SCHEME_2, scheme.getSchemeType());
    }
    
    @Test
    public void calculateCommission_forScheme1_shouldReturn13Percent() {
        // Arrange
        PaymentScheme scheme = new PaymentScheme(SchemeType.SCHEME_1);
        BigDecimal amount = new BigDecimal("1000");
        
        // Act
        BigDecimal commission = paymentSchemeService.calculateCommission(scheme, amount);
        
        // Assert
        assertEquals(new BigDecimal("130.0"), commission);
    }
    
    @Test
    public void calculateCommission_forScheme2_shouldReturn16Percent() {
        // Arrange
        PaymentScheme scheme = new PaymentScheme(SchemeType.SCHEME_2);
        BigDecimal amount = new BigDecimal("1000");
        
        // Act
        BigDecimal commission = paymentSchemeService.calculateCommission(scheme, amount);
        
        // Assert
        assertEquals(new BigDecimal("160.0"), commission);
    }
    
    @Test
    public void calculateTotalAmount_shouldAddCommissionToAmount() {
        // Arrange
        PaymentScheme scheme = new PaymentScheme(SchemeType.SCHEME_1);
        BigDecimal amount = new BigDecimal("1000");
        
        // Act
        BigDecimal totalAmount = paymentSchemeService.calculateTotalAmount(scheme, amount);
        
        // Assert
        assertEquals(new BigDecimal("1130.0"), totalAmount);
    }
    
    @Test
    public void calculateInstallmentAmount_shouldDivideTotalByNumberOfPayments() {
        // Arrange
        PaymentScheme scheme = new PaymentScheme(SchemeType.SCHEME_1);
        BigDecimal amount = new BigDecimal("1000");
        
        // Act
        BigDecimal installmentAmount = paymentSchemeService.calculateInstallmentAmount(scheme, amount);
        
        // Assert
        assertEquals(new BigDecimal("226.00"), installmentAmount);
    }
    
    @Test
    public void calculatePaymentDates_shouldGenerateBiweeklyDates() {
        // Arrange
        PaymentScheme scheme = new PaymentScheme(SchemeType.SCHEME_1);
        LocalDate purchaseDate = LocalDate.of(2023, 1, 1);
        
        // Act
        List<LocalDate> paymentDates = paymentSchemeService.calculatePaymentDates(scheme, purchaseDate);
        
        // Assert
        assertEquals(5, paymentDates.size());
        assertEquals(LocalDate.of(2023, 1, 15), paymentDates.get(0));
        assertEquals(LocalDate.of(2023, 1, 29), paymentDates.get(1));
        assertEquals(LocalDate.of(2023, 2, 12), paymentDates.get(2));
        assertEquals(LocalDate.of(2023, 2, 26), paymentDates.get(3));
        assertEquals(LocalDate.of(2023, 3, 12), paymentDates.get(4));
    }
}
