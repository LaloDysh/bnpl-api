package com.bnpl.application.port.out.customer;

import com.bnpl.domain.model.customer.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(UUID customerId);
    boolean existsById(UUID customerId);
    void updateCreditLine(UUID customerId, Customer customer);
}