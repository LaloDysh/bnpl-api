package com.bnpl.application.usecase.loan;

import com.bnpl.application.port.in.loan.CreateLoanUseCase;
import com.bnpl.application.port.out.customer.CustomerRepository;
import com.bnpl.application.port.out.loan.LoanRepository;
import com.bnpl.application.service.loan.InstallmentCalculator;
import com.bnpl.application.service.loan.PaymentSchemeAssigner;
import com.bnpl.domain.model.customer.Customer;
import com.bnpl.domain.model.error.BnplException;
import com.bnpl.domain.model.loan.Installment;
import com.bnpl.domain.model.loan.Loan;
import com.bnpl.domain.model.loan.PaymentScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CreateLoanService implements CreateLoanUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateLoanService.class);

    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final PaymentSchemeAssigner paymentSchemeAssigner;
    private final InstallmentCalculator installmentCalculator;

    public CreateLoanService(
            CustomerRepository customerRepository,
            LoanRepository loanRepository,
            PaymentSchemeAssigner paymentSchemeAssigner,
            InstallmentCalculator installmentCalculator) {
        this.customerRepository = customerRepository;
        this.loanRepository = loanRepository;
        this.paymentSchemeAssigner = paymentSchemeAssigner;
        this.installmentCalculator = installmentCalculator;
    }

    @Override
    @Transactional
    public Loan createLoan(UUID customerId, BigDecimal amount) {
        log.info("Creating loan for customer ID: {} with amount: {}", customerId, amount);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", customerId);
                    return new BnplException.CustomerNotFoundException("/v1/loans");
                });

        if (customer.getCreditLine().getAvailableAmount().compareTo(amount) < 0) {
            log.error("Insufficient credit line for customer ID: {}", customerId);
            throw new BnplException.InsufficientCreditLineException("/v1/loans");
        }

        UUID loanId = UUID.randomUUID();
        PaymentScheme paymentScheme = paymentSchemeAssigner.assignPaymentScheme(customer);
        
        BigDecimal commissionAmount = installmentCalculator.calculateCommission(amount, paymentScheme);
        
        List<Installment> installments = installmentCalculator.calculateInstallments(
                loanId, amount.add(commissionAmount), paymentScheme, LocalDate.now());

        Loan loan = Loan.builder()
                .id(loanId)
                .customerId(customerId)
                .amount(amount)
                .paymentScheme(paymentScheme)
                .commissionAmount(commissionAmount)
                .installments(installments)
                .createdAt(LocalDateTime.now())
                .build();

        // Deduct loan amount from customer's credit line
        customer.getCreditLine().deduct(amount);
        customerRepository.updateCreditLine(customerId, customer);

        Loan savedLoan = loanRepository.save(loan);
        log.info("Loan created successfully with ID: {}", savedLoan.getId());

        return savedLoan;
    }
}
