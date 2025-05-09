package com.bnpl.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bnpl.application.port.in.PurchaseRegistrationCommand;
import com.bnpl.application.port.out.PurchaseRegistrationResponse;
import com.bnpl.domain.exception.ClientNotFoundException;
import com.bnpl.domain.model.Client;
import com.bnpl.domain.model.CreditLine;
import com.bnpl.domain.model.Loan;
import com.bnpl.domain.model.PaymentScheme;
import com.bnpl.domain.model.Purchase;
import com.bnpl.domain.model.enums.SchemeType;
import com.bnpl.domain.port.out.ClientRepository;
import com.bnpl.domain.port.out.CreditLineRepository;
import com.bnpl.domain.service.CreditLineService;
import com.bnpl.domain.service.LoanService;
import com.bnpl.domain.service.PaymentSchemeService;
import com.bnpl.domain.service.PurchaseService;

@ExtendWith(MockitoExtension.class)
public class PurchaseRegistrationServiceTest {
    
    @Mock
    private ClientRepository clientRepository;
    
    @Mock
    private CreditLineRepository creditLineRepository;
    
    @Mock
    private CreditLineService creditLineService;
    
    @Mock
    private PurchaseService purchaseService;
    
    @Mock
    private PaymentSchemeService paymentSchemeService;
    
    @Mock
    private LoanService loanService;
    
    private PurchaseRegistrationService purchaseRegistrationService;
    
    @BeforeEach
    public void setup() {
        purchaseRegistrationService = new PurchaseRegistrationService(
                clientRepository,
                creditLineRepository,
                creditLineService,
                purchaseService,
                paymentSchemeService,
                loanService
        );
    }
    
    @Test
    public void registerPurchase_shouldReturnSuccessResponse() {
        // Arrange
        Long clientId = 1L;
        BigDecimal amount = new BigDecimal("1000");
        PurchaseRegistrationCommand command = new PurchaseRegistrationCommand(clientId, amount);
        
        Client client = new Client(clientId, "John Doe", LocalDate.now().minusYears(30));
        CreditLine creditLine = new CreditLine(1L, clientId, new BigDecimal("5000"));
        PaymentScheme paymentScheme = new PaymentScheme(SchemeType.SCHEME_1);
        Purchase purchase = new Purchase(1L, clientId, amount, LocalDate.now(), SchemeType.SCHEME_1);
        
        BigDecimal commission = new BigDecimal("130");
        BigDecimal totalAmount = new BigDecimal("1130");
        BigDecimal installmentAmount = new BigDecimal("226");
        List<LocalDate> paymentDates = Arrays.asList(
                LocalDate.now().plusDays(14),
                LocalDate.now().plusDays(28),
                LocalDate.now().plusDays(42),
                LocalDate.now().plusDays(56),
                LocalDate.now().plusDays(70)
        );
        
        Loan loan = new Loan(1L, purchase.getId(), totalAmount, installmentAmount, paymentDates, commission);
        
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(creditLineRepository.findByClientId(clientId)).thenReturn(Optional.of(creditLine));
        doNothing().when(creditLineService).validateCreditAvailability(creditLine, amount);
        when(paymentSchemeService.determinePaymentScheme(client)).thenReturn(paymentScheme);
        when(purchaseService.createPurchase(clientId, amount, paymentScheme.getSchemeType())).thenReturn(purchase);
        when(paymentSchemeService.calculateCommission(paymentScheme, amount)).thenReturn(commission);
        when(paymentSchemeService.calculateTotalAmount(paymentScheme, amount)).thenReturn(totalAmount);
        when(paymentSchemeService.calculateInstallmentAmount(paymentScheme, amount)).thenReturn(installmentAmount);
        when(paymentSchemeService.calculatePaymentDates(paymentScheme, purchase.getPurchaseDate())).thenReturn(paymentDates);
        when(loanService.createLoan(purchase.getId(), totalAmount, installmentAmount, paymentDates, commission)).thenReturn(loan);
        doNothing().when(creditLineService).reduceCreditLine(creditLine, amount);
        
        // Act
        PurchaseRegistrationResponse response = purchaseRegistrationService.registerPurchase(command);
        
        // Assert
        assertNotNull(response);
        assertEquals(purchase.getId(), response.getPurchaseId());
        assertEquals(totalAmount, response.getTotalAmount());
        assertEquals(installmentAmount, response.getInstallmentAmount());
        assertEquals(paymentDates, response.getPaymentDates());
        
        verify(clientRepository, times(1)).findById(clientId);
        verify(creditLineRepository, times(1)).findByClientId(clientId);
        verify(creditLineService, times(1)).validateCreditAvailability(creditLine, amount);
        verify(paymentSchemeService, times(1)).determinePaymentScheme(client);
        verify(purchaseService, times(1)).createPurchase(clientId, amount, paymentScheme.getSchemeType());
        verify(paymentSchemeService, times(1)).calculateCommission(paymentScheme, amount);
        verify(paymentSchemeService, times(1)).calculateTotalAmount(paymentScheme, amount);
        verify(paymentSchemeService, times(1)).calculateInstallmentAmount(paymentScheme, amount);
        verify(paymentSchemeService, times(1)).calculatePaymentDates(paymentScheme, purchase.getPurchaseDate());
        verify(loanService, times(1)).createLoan(purchase.getId(), totalAmount, installmentAmount, paymentDates, commission);
        verify(creditLineService, times(1)).reduceCreditLine(creditLine, amount);
    }
    
    @Test
    public void registerPurchase_withNonExistingClient_shouldThrowException() {
        // Arrange
        Long clientId = 999L;
        BigDecimal amount = new BigDecimal("1000");
        PurchaseRegistrationCommand command = new PurchaseRegistrationCommand(clientId, amount);
        
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ClientNotFoundException.class, () -> {
            purchaseRegistrationService.registerPurchase(command);
        });
        
        verify(clientRepository, times(1)).findById(clientId);
        verify(creditLineRepository, never()).findByClientId(anyLong());
        verify(purchaseService, never()).createPurchase(anyLong(), any(BigDecimal.class), any(SchemeType.class));
    }
    
    @Test
    public void registerPurchase_withDomainPort_shouldReturnLoan() {
        // Arrange
        Long clientId = 1L;
        BigDecimal amount = new BigDecimal("1000");
        
        Client client = new Client(clientId, "John Doe", LocalDate.now().minusYears(30));
        CreditLine creditLine = new CreditLine(1L, clientId, new BigDecimal("5000"));
        PaymentScheme paymentScheme = new PaymentScheme(SchemeType.SCHEME_1);
        Purchase purchase = new Purchase(1L, clientId, amount, LocalDate.now(), SchemeType.SCHEME_1);
        
        BigDecimal commission = new BigDecimal("130");
        BigDecimal totalAmount = new BigDecimal("1130");
        BigDecimal installmentAmount = new BigDecimal("226");
        List<LocalDate> paymentDates = Arrays.asList(
                LocalDate.now().plusDays(14),
                LocalDate.now().plusDays(28),
                LocalDate.now().plusDays(42),
                LocalDate.now().plusDays(56),
                LocalDate.now().plusDays(70)
        );
        
        Loan loan = new Loan(1L, purchase.getId(), totalAmount, installmentAmount, paymentDates, commission);
        
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(creditLineRepository.findByClientId(clientId)).thenReturn(Optional.of(creditLine));
        doNothing().when(creditLineService).validateCreditAvailability(creditLine, amount);
        when(paymentSchemeService.determinePaymentScheme(client)).thenReturn(paymentScheme);
        when(purchaseService.createPurchase(clientId, amount, paymentScheme.getSchemeType())).thenReturn(purchase);
        when(paymentSchemeService.calculateCommission(paymentScheme, amount)).thenReturn(commission);
        when(paymentSchemeService.calculateTotalAmount(paymentScheme, amount)).thenReturn(totalAmount);
        when(paymentSchemeService.calculateInstallmentAmount(paymentScheme, amount)).thenReturn(installmentAmount);
        when(paymentSchemeService.calculatePaymentDates(paymentScheme, purchase.getPurchaseDate())).thenReturn(paymentDates);
        when(loanService.createLoan(purchase.getId(), totalAmount, installmentAmount, paymentDates, commission)).thenReturn(loan);
        doNothing().when(creditLineService).reduceCreditLine(creditLine, amount);
        
        // Act
        Loan result = purchaseRegistrationService.registerPurchase(clientId, amount);
        
        // Assert
        assertNotNull(result);
        assertEquals(loan.getId(), result.getId());
        assertEquals(loan.getPurchaseId(), result.getPurchaseId());
        assertEquals(loan.getTotalAmount(), result.getTotalAmount());
        assertEquals(loan.getInstallmentAmount(), result.getInstallmentAmount());
        assertEquals(loan.getPaymentDates(), result.getPaymentDates());
        assertEquals(loan.getCommission(), result.getCommission());
    }
}