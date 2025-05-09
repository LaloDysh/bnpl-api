package com.bnpl.domain.model.loan;

import java.math.BigDecimal;

public enum PaymentScheme {
    SCHEME_1(5, 14, new BigDecimal("0.13")),
    SCHEME_2(5, 14, new BigDecimal("0.16"));

    private final int numberOfPayments;
    private final int frequencyInDays;
    private final BigDecimal interestRate;

    PaymentScheme(int numberOfPayments, int frequencyInDays, BigDecimal interestRate) {
        this.numberOfPayments = numberOfPayments;
        this.frequencyInDays = frequencyInDays;
        this.interestRate = interestRate;
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
}