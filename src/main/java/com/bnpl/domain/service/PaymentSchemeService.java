package com.bnpl.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bnpl.domain.model.Client;
import com.bnpl.domain.model.PaymentScheme;
import com.bnpl.domain.model.enums.SchemeType;

public class PaymentSchemeService {
    private static final Logger log = LoggerFactory.getLogger(PaymentSchemeService.class);
    
    public PaymentScheme determinePaymentScheme(Client client) {
        SchemeType schemeType = client.determinePaymentScheme();
        log.info("Determined payment scheme for client {}: {}", client.getId(), schemeType);
        
        return new PaymentScheme(schemeType);
    }
    
    public BigDecimal calculateCommission(PaymentScheme scheme, BigDecimal amount) {
        BigDecimal commission = scheme.calculateCommission(amount);
        log.info("Calculated commission for scheme {}: {}", scheme.getSchemeType(), commission);
        
        return commission;
    }
    
    public BigDecimal calculateTotalAmount(PaymentScheme scheme, BigDecimal amount) {
        BigDecimal totalAmount = scheme.calculateTotalAmount(amount);
        log.info("Calculated total amount for scheme {}: {}", scheme.getSchemeType(), totalAmount);
        
        return totalAmount;
    }
    
    public BigDecimal calculateInstallmentAmount(PaymentScheme scheme, BigDecimal amount) {
        BigDecimal installmentAmount = scheme.calculateInstallmentAmount(amount);
        log.info("Calculated installment amount for scheme {}: {}", scheme.getSchemeType(), installmentAmount);
        
        return installmentAmount;
    }
    
    public List<LocalDate> calculatePaymentDates(PaymentScheme scheme, LocalDate purchaseDate) {
        List<LocalDate> paymentDates = scheme.calculatePaymentDates(purchaseDate);
        log.info("Calculated payment dates for scheme {}: {}", scheme.getSchemeType(), paymentDates);
        
        return paymentDates;
    }
}
