package com.bnpl.infrastructure.adapter.in.web.loan;

import com.bnpl.application.port.in.loan.CreateLoanUseCase;
import com.bnpl.application.port.in.loan.GetLoanUseCase;
import com.bnpl.domain.model.loan.*;
import com.bnpl.infrastructure.adapter.in.web.loan.request.LoanRequest;
import com.bnpl.infrastructure.adapter.in.web.loan.response.LoanResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateLoanUseCase createLoanUseCase;

    @MockBean
    private GetLoanUseCase getLoanUseCase;

    @MockBean
    private LoanMapper loanMapper;

    @Test
    void shouldCreateLoanSuccessfully() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID loanId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("1000.00");
        
        LoanRequest request = new LoanRequest();
        request.setCustomerId(customerId);
        request.setAmount(amount);
        
        Loan loan = createTestLoan(loanId, customerId, amount);
        LoanResponse response = createTestLoanResponse(loan);
        
        when(createLoanUseCase.createLoan(customerId, amount)).thenReturn(loan);
        when(loanMapper.toResponse(loan)).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/v1/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(loanId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.amount").value("1000.00"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.paymentPlan.commissionAmount").value("130.00"))
                .andExpect(jsonPath("$.paymentPlan.installments.length()").value(5));
    }

    @Test
    void shouldGetLoanByIdSuccessfully() throws Exception {
        // Given
        UUID loanId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("1000.00");
        
        Loan loan = createTestLoan(loanId, customerId, amount);
        LoanResponse response = createTestLoanResponse(loan);
        
        when(getLoanUseCase.getLoanById(loanId)).thenReturn(loan);
        when(loanMapper.toResponse(loan)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/v1/loans/{loanId}", loanId)
                        .header("Authorization", "Bearer dummy-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(loanId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.amount").value("1000.00"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.paymentPlan.commissionAmount").value("130.00"))
                .andExpect(jsonPath("$.paymentPlan.installments.length()").value(5));
    }
    
    private Loan createTestLoan(UUID loanId, UUID customerId, BigDecimal amount) {
        BigDecimal commissionAmount = new BigDecimal("130.00");
        PaymentScheme paymentScheme = PaymentScheme.SCHEME_1;
        
        // Create installments
        Installment installment1 = Installment.builder()
                .id(UUID.randomUUID())
                .loanId(loanId)
                .installmentNumber(1)
                .amount(new BigDecimal("226.00"))
                .scheduledPaymentDate(LocalDate.now())
                .status(InstallmentStatus.NEXT)
                .build();
        
        Installment installment2 = Installment.builder()
                .id(UUID.randomUUID())
                .loanId(loanId)
                .installmentNumber(2)
                .amount(new BigDecimal("226.00"))
                .scheduledPaymentDate(LocalDate.now().plusDays(14))
                .status(InstallmentStatus.PENDING)
                .build();
        
        Installment installment3 = Installment.builder()
                .id(UUID.randomUUID())
                .loanId(loanId)
                .installmentNumber(3)
                .amount(new BigDecimal("226.00"))
                .scheduledPaymentDate(LocalDate.now().plusDays(28))
                .status(InstallmentStatus.PENDING)
                .build();
        
        Installment installment4 = Installment.builder()
                .id(UUID.randomUUID())
                .loanId(loanId)
                .installmentNumber(4)
                .amount(new BigDecimal("226.00"))
                .scheduledPaymentDate(LocalDate.now().plusDays(42))
                .status(InstallmentStatus.PENDING)
                .build();
        
        Installment installment5 = Installment.builder()
                .id(UUID.randomUUID())
                .loanId(loanId)
                .installmentNumber(5)
                .amount(new BigDecimal("226.00"))
                .scheduledPaymentDate(LocalDate.now().plusDays(56))
                .status(InstallmentStatus.PENDING)
                .build();
        
        return Loan.builder()
                .id(loanId)
                .customerId(customerId)
                .amount(amount)
                .commissionAmount(commissionAmount)
                .paymentScheme(paymentScheme)
                .installments(Arrays.asList(installment1, installment2, installment3, installment4, installment5))
                .status(LoanStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    private LoanResponse createTestLoanResponse(Loan loan) {
        LoanResponse response = new LoanResponse();
        response.setId(loan.getId());
        response.setCustomerId(loan.getCustomerId());
        response.setAmount(loan.getAmount());
        response.setStatus(loan.getStatus());
        response.setCreatedAt(loan.getCreatedAt());
        
        LoanResponse.PaymentPlanResponse paymentPlan = new LoanResponse.PaymentPlanResponse();
        paymentPlan.setCommissionAmount(loan.getCommissionAmount());
        
        // Create response installments
        paymentPlan.setInstallments(new ArrayList<>());
        
        response.setPaymentPlan(paymentPlan);
        
        return response;
    }
}
