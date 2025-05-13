package com.bnpl.application.service.customer;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Component
public class CreditLineCalculator {

    private static final BigDecimal YOUNG_CREDIT_LINE = new BigDecimal("3000.00");
    private static final BigDecimal ADULT_CREDIT_LINE = new BigDecimal("5000.00");
    private static final BigDecimal SENIOR_CREDIT_LINE = new BigDecimal("8000.00");

    public BigDecimal calculateCreditLine(LocalDate dateOfBirth, LocalDate currentDate) {
        int age = Period.between(dateOfBirth, currentDate).getYears();

        if (age >= 18 && age <= 25) {
            return YOUNG_CREDIT_LINE;
        } else if (age >= 26 && age <= 30) {
            return ADULT_CREDIT_LINE;
        } else {
            return SENIOR_CREDIT_LINE;
        }
    }
}