package com.bnpl.application.service.loan;

import com.bnpl.domain.model.loan.Installment;
import com.bnpl.domain.model.loan.InstallmentStatus;
import com.bnpl.domain.model.loan.PaymentScheme;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class InstallmentCalculator {

    public List<Installment> calculateInstallments(UUID loanId, BigDecimal amount, PaymentScheme paymentScheme, LocalDate startDate) {
        List<Installment> installments = new ArrayList<>();
        
        BigDecimal installmentAmount = amount
                .divide(BigDecimal.valueOf(paymentScheme.getNumberOfPayments()), 2, RoundingMode.HALF_UP);
        
        for (int i = 0; i < paymentScheme.getNumberOfPayments(); i++) {
            LocalDate paymentDate = startDate.plusDays((long) i * paymentScheme.getFrequencyInDays());
            
            InstallmentStatus status = (i == 0) ? InstallmentStatus.NEXT : InstallmentStatus.PENDING;
            
            Installment installment = Installment.builder()
                    .id(UUID.randomUUID())
                    .loanId(loanId)
                    .installmentNumber(i + 1)
                    .amount(installmentAmount)
                    .scheduledPaymentDate(paymentDate)
                    .status(status)
                    .build();
            
            installments.add(installment);
        }
        
        return installments;
    }

    public BigDecimal calculateCommission(BigDecimal amount, PaymentScheme paymentScheme) {
        return amount.multiply(paymentScheme.getInterestRate()).setScale(2, RoundingMode.HALF_UP);
    }
}
