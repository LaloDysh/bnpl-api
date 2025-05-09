package com.bnpl.domain.port.in;

import com.bnpl.domain.model.Client;
import com.bnpl.domain.model.CreditLine;

public interface ClientRegistrationUseCase {
    /**
     * Registers a new client and assigns a credit line based on age rules.
     *
     * @param client The client to register
     * @return The assigned credit line
     * @throws ClientNotEligibleException if client is not eligible for credit
     */
    CreditLine registerClient(Client client);
}