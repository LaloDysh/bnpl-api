package com.bnpl.infrastructure.adapter.in.web.loan;

import com.bnpl.domain.model.loan.Installment;
import com.bnpl.domain.model.loan.Loan;
import com.bnpl.infrastructure.adapter.in.web.loan.response.InstallmentResponse;
import com.bnpl.infrastructure.adapter.in.web.loan.response.LoanResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LoanMapper {

    public LoanResponse toResponse(Loan loan) {
        LoanResponse response = new LoanResponse();
        response.setId(loan.getId());
        response.setCustomerId(loan.getCustomerId());
        response.setAmount(loan.getAmount());
        response.setStatus(loan.getStatus());
        response.setCreatedAt(loan.getCreatedAt());
        
        // Map payment plan
        LoanResponse.PaymentPlanResponse paymentPlan = new LoanResponse.PaymentPlanResponse();
        paymentPlan.setCommissionAmount(loan.getCommissionAmount());
        paymentPlan.setInstallments(mapInstallments(loan.getInstallments()));
        
        response.setPaymentPlan(paymentPlan);
        
        return response;
    }

    private List<InstallmentResponse> mapInstallments(List<Installment> installments) {
        return installments.stream()
                .map(this::mapInstallment)
                .collect(Collectors.toList());
    }

    private InstallmentResponse mapInstallment(Installment installment) {
        InstallmentResponse response = new InstallmentResponse();
        response.setAmount(installment.getAmount());
        response.setScheduledPaymentDate(installment.getScheduledPaymentDate());
        response.setStatus(installment.getStatus());
        return response;
    }
}