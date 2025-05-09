package com.bnpl.application.port.in.customer;

import com.bnpl.domain.model.customer.Customer;

import java.util.UUID;

public interface GetCustomerUseCase {
    Customer getCustomerById(UUID customerId);
}
