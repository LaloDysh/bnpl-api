package com.bnpl.application.usecase.loan;

import com.bnpl.application.port.out.customer.CustomerRepository;
import com.bnpl.application.port.out.loan.LoanRepository;
import com.bnpl.application.service.loan.InstallmentCalculator;
import com.bnpl.application.service.loan.PaymentSchemeAssigner;
import com.bnpl.domain.model.customer.CreditLine;
import com.bnpl.domain.model.customer.Customer;
import com.bnpl.domain.model.error.BnplException;
import com.bnpl.domain.model.loan.Installment;
import com.bnpl.domain.model.loan.Loan;
import com.bnpl.domain.model.loan.PaymentScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateLoanServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private PaymentSchemeAssigner paymentSchemeAssigner;

    @Mock
    private InstallmentCalculator installmentCalculator;

    private CreateLoanService createLoanService;

    @BeforeEach
    void setUp() {
        createLoanService = new CreateLoanService(
                customerRepository,
                loanRepository,
                paymentSchemeAssigner,
                installmentCalculator
        );
    }

    @Test
    void shouldCreateLoanSuccessfully() {
        // Given
        UUID customerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("1000.00");
        BigDecimal commissionAmount = new BigDecimal("130.00");
        PaymentScheme paymentScheme = PaymentScheme.SCHEME_1;
        
        Customer customer = Customer.builder()
                .id(customerId)
                .firstName("John")
                .lastName("Doe")
                .secondLastName("Smith")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .creditLine(CreditLine.of(new BigDecimal("5000.00")))
                .createdAt(LocalDateTime.now())
                .build();
        
        List<Installment> installments = Arrays.asList(
                Installment.builder().id(UUID.randomUUID()).amount(new BigDecimal("226.00")).build(),
                Installment.builder().id(UUID.randomUUID()).amount(new BigDecimal("226.00")).build(),
                Installment.builder().id(UUID.randomUUID()).amount(new BigDecimal("226.00")).build(),
                Installment.builder().id(UUID.randomUUID()).amount(new BigDecimal("226.00")).build(),
                Installment.builder().id(UUID.randomUUID()).amount(new BigDecimal("226.00")).build()
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(paymentSchemeAssigner.assignPaymentScheme(customer)).thenReturn(paymentScheme);
        when(installmentCalculator.calculateCommission(amount, paymentScheme)).thenReturn(commissionAmount);
        when(installmentCalculator.calculateInstallments(any(UUID.class), any(BigDecimal.class), eq(paymentScheme), any(LocalDate.class)))
                .thenReturn(installments);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Loan result = createLoanService.createLoan(customerId, amount);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(customerId, result.getCustomerId());
        assertEquals(amount, result.getAmount());
        assertEquals(paymentScheme, result.getPaymentScheme());
        assertEquals(commissionAmount, result.getCommissionAmount());
        assertEquals(5, result.getInstallments().size());
        assertNotNull(result.getCreatedAt());

        // Verify the credit line was updated
        ArgumentCaptor<UUID> customerIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).updateCreditLine(customerIdCaptor.capture(), customerCaptor.capture());
        assertEquals(customerId, customerIdCaptor.getValue());
        assertEquals(new BigDecimal("4000.00"), customerCaptor.getValue().getCreditLine().getAvailableAmount());

        // Verify loan was saved
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        UUID customerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("1000.00");

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When & Then
        BnplException exception = assertThrows(
                BnplException.CustomerNotFoundException.class,
                () -> createLoanService.createLoan(customerId, amount)
        );
        assertEquals("APZ000005", exception.getCode());
        assertEquals("CUSTOMER_NOT_FOUND", exception.getError());
        assertEquals("/v1/loans", exception.getPath());

        // Verify no loan was saved
        verifyNoInteractions(loanRepository);
    }

    @Test
    void shouldThrowExceptionWhenInsufficientCreditLine() {
        // Given
        UUID customerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("10000.00"); // More than available credit line
        
        Customer customer = Customer.builder()
                .id(customerId)
                .firstName("John")
                .lastName("Doe")
                .secondLastName("Smith")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .creditLine(CreditLine.of(new BigDecimal("5000.00")))
                .createdAt(LocalDateTime.now())
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When & Then
        BnplException exception = assertThrows(
                BnplException.InsufficientCreditLineException.class,
                () -> createLoanService.createLoan(customerId, amount)
        );
        assertEquals("APZ000009", exception.getCode());
        assertEquals("INSUFFICIENT_CREDIT_LINE", exception.getError());
        assertEquals("/v1/loans", exception.getPath());

        // Verify no loan was saved and credit line was not updated
        verifyNoInteractions(loanRepository);
        verify(customerRepository, never()).updateCreditLine(any(), any());
    }
}