package com.bnpl.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
// import java.util.Optional;
import com.bnpl.application.port.in.PurchaseRegistrationCommand;
import com.bnpl.application.port.out.PurchaseRegistrationResponse;
import com.bnpl.domain.exception.ClientNotFoundException;
import com.bnpl.domain.model.Client;
import com.bnpl.domain.model.CreditLine;
import com.bnpl.domain.model.Loan;
import com.bnpl.domain.model.PaymentScheme;
import com.bnpl.domain.model.Purchase;
import com.bnpl.domain.port.in.PurchaseRegistrationUseCase;
import com.bnpl.domain.port.out.ClientRepository;
import com.bnpl.domain.port.out.CreditLineRepository;
import com.bnpl.domain.service.CreditLineService;
import com.bnpl.domain.service.LoanService;
import com.bnpl.domain.service.PaymentSchemeService;
import com.bnpl.domain.service.PurchaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class PurchaseRegistrationService implements PurchaseRegistrationUseCase {
    private static final Logger log = LoggerFactory.getLogger(PurchaseRegistrationService.class);
    
    private final ClientRepository clientRepository;
    private final CreditLineRepository creditLineRepository;
    private final CreditLineService creditLineService;
    private final PurchaseService purchaseService;
    private final PaymentSchemeService paymentSchemeService;
    private final LoanService loanService;
    
    public PurchaseRegistrationService(
            ClientRepository clientRepository,
            CreditLineRepository creditLineRepository,
            CreditLineService creditLineService,
            PurchaseService purchaseService,
            PaymentSchemeService paymentSchemeService,
            LoanService loanService) {
        this.clientRepository = clientRepository;
        this.creditLineRepository = creditLineRepository;
        this.creditLineService = creditLineService;
        this.purchaseService = purchaseService;
        this.paymentSchemeService = paymentSchemeService;
        this.loanService = loanService;
    }
    
    @Transactional
    public PurchaseRegistrationResponse registerPurchaseWithResponse(PurchaseRegistrationCommand command) {
        log.info("Processing purchase registration: client {}, amount {}", 
                 command.getClientId(), command.getAmount());
        
        // Get client
        Client client = clientRepository.findById(command.getClientId())
                .orElseThrow(() -> ClientNotFoundException.withId(command.getClientId()));
        
        // Get credit line
        CreditLine creditLine = creditLineRepository.findByClientId(client.getId())
                .orElseThrow(() -> new IllegalStateException("Credit line not found for client: " + client.getId()));
        
        // Validate credit availability
        creditLineService.validateCreditAvailability(creditLine, command.getAmount());
        
        // Determine payment scheme
        PaymentScheme paymentScheme = paymentSchemeService.determinePaymentScheme(client);
        
        // Create purchase
        Purchase purchase = purchaseService.createPurchase(
                client.getId(), command.getAmount(), paymentScheme.getSchemeType());
        
        // Calculate loan details
        BigDecimal commission = paymentSchemeService.calculateCommission(paymentScheme, command.getAmount());
        BigDecimal totalAmount = paymentSchemeService.calculateTotalAmount(paymentScheme, command.getAmount());
        BigDecimal installmentAmount = paymentSchemeService.calculateInstallmentAmount(paymentScheme, command.getAmount());
        List<LocalDate> paymentDates = paymentSchemeService.calculatePaymentDates(paymentScheme, purchase.getPurchaseDate());
        
        // Create loan
        Loan loan = loanService.createLoan(
                purchase.getId(), totalAmount, installmentAmount, paymentDates, commission);
        
        // Reduce credit line
        creditLineService.reduceCreditLine(creditLine, command.getAmount());
        
        log.info("Purchase registration completed: purchase id {}, total amount {}", 
                 purchase.getId(), totalAmount);
        
        return new PurchaseRegistrationResponse(
                purchase.getId(), totalAmount, installmentAmount, paymentDates);
    }

    @Override
    public Loan registerPurchase(Long clientId, BigDecimal amount) {
        // This method implements the domain port interface
        // Get client
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> ClientNotFoundException.withId(clientId));
        
        // Get credit line
        CreditLine creditLine = creditLineRepository.findByClientId(client.getId())
                .orElseThrow(() -> new IllegalStateException("Credit line not found for client: " + client.getId()));
        
        // Validate credit availability
        creditLineService.validateCreditAvailability(creditLine, amount);
        
        // Determine payment scheme
        PaymentScheme paymentScheme = paymentSchemeService.determinePaymentScheme(client);
        
        // Create purchase
        Purchase purchase = purchaseService.createPurchase(
                client.getId(), amount, paymentScheme.getSchemeType());
        
        // Calculate loan details
        BigDecimal commission = paymentSchemeService.calculateCommission(paymentScheme, amount);
        BigDecimal totalAmount = paymentSchemeService.calculateTotalAmount(paymentScheme, amount);
        BigDecimal installmentAmount = paymentSchemeService.calculateInstallmentAmount(paymentScheme, amount);
        List<LocalDate> paymentDates = paymentSchemeService.calculatePaymentDates(paymentScheme, purchase.getPurchaseDate());
        
        // Create loan
        Loan loan = loanService.createLoan(
                purchase.getId(), totalAmount, installmentAmount, paymentDates, commission);
        
        // Reduce credit line
        creditLineService.reduceCreditLine(creditLine, amount);
        
        return loan;
    }
}
