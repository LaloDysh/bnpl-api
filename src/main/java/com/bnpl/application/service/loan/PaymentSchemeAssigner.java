package com.bnpl.application.service.loan;

import com.bnpl.domain.model.customer.Customer;
import com.bnpl.domain.model.loan.PaymentScheme;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PaymentSchemeAssigner {

    private static final List<Character> SCHEME_1_NAME_PREFIXES = Arrays.asList('C', 'L', 'H');
    private static final int SCHEME_2_ID_THRESHOLD = 25;

    public PaymentScheme assignPaymentScheme(Customer customer) {
        // Rule 1: If the first name starts with C, L, or H, assign Scheme 1
        if (customer.getFirstName() != null && !customer.getFirstName().isEmpty()) {
            char firstChar = customer.getFirstName().toUpperCase().charAt(0);
            if (SCHEME_1_NAME_PREFIXES.contains(firstChar)) {
                return PaymentScheme.SCHEME_1;
            }
        }

        // Rule 2: If the customer ID is greater than 25
        int idValue = Math.abs(customer.getId().hashCode() % 100);
        if (idValue > SCHEME_2_ID_THRESHOLD) {
            return PaymentScheme.SCHEME_2;
        }
        
        return PaymentScheme.SCHEME_2;
    }
}
