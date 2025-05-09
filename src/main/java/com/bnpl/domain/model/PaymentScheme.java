package com.bnpl.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.bnpl.domain.model.enums.SchemeType;

public class PaymentScheme {
    private SchemeType schemeType;
    private int numberOfPayments;
    private int frequencyInDays;
    private BigDecimal interestRate;
    
    public PaymentScheme(SchemeType schemeType) {
        this.schemeType = schemeType;
        
        if (schemeType == SchemeType.SCHEME_1) {
            this.numberOfPayments = 5;
            this.frequencyInDays = 14; // Biweekly
            this.interestRate = new BigDecimal("0.13"); // 13%
        } else {
            this.numberOfPayments = 5;
            this.frequencyInDays = 14; // Biweekly
            this.interestRate = new BigDecimal("0.16"); // 16%
        }
    }
    
    // Getters
    public SchemeType getSchemeType() {
        return schemeType;
    }
    
    public int getNumberOfPayments() {
        return numberOfPayments;
    }
    
    public int getFrequencyInDays() {
        return frequencyInDays;
    }
    
    public BigDecimal getInterestRate() {
        return interestRate;
    }
    
    // Domain logic
    public BigDecimal calculateCommission(BigDecimal purchaseAmount) {
        return purchaseAmount.multiply(interestRate);
    }
    
    public BigDecimal calculateTotalAmount(BigDecimal purchaseAmount) {
        BigDecimal commission = calculateCommission(purchaseAmount);
        return purchaseAmount.add(commission);
    }
    
    public BigDecimal calculateInstallmentAmount(BigDecimal purchaseAmount) {
        BigDecimal totalAmount = calculateTotalAmount(purchaseAmount);
        return totalAmount.divide(BigDecimal.valueOf(numberOfPayments), 2, RoundingMode.HALF_UP);
    }
    
    public List<LocalDate> calculatePaymentDates(LocalDate purchaseDate) {
        List<LocalDate> paymentDates = new ArrayList<>();
        
        LocalDate currentDate = purchaseDate;
        for (int i = 0; i < numberOfPayments; i++) {
            currentDate = currentDate.plus(frequencyInDays, ChronoUnit.DAYS);
            paymentDates.add(currentDate);
        }
        
        return paymentDates;
    }
}
