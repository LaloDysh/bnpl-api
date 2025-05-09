package com.bnpl.domain.port.in;

import java.math.BigDecimal;
import com.bnpl.domain.model.Loan;
// import com.bnpl.domain.model.Purchase;

public interface PurchaseRegistrationUseCase {
    /**
     * Registers a new purchase for a client and creates a loan.
     *
     * @param clientId The client ID
     * @param amount The purchase amount
     * @return The created loan
     * @throws ClientNotFoundException if client is not found
     * @throws InsufficientCreditException if client has insufficient credit
     */
    Loan registerPurchase(Long clientId, BigDecimal amount);
}
