package com.bnpl.application.port.in.customer;

import com.bnpl.domain.model.customer.Customer;

import java.time.LocalDate;

public interface CreateCustomerUseCase {
    Customer createCustomer(String firstName, String lastName, String secondLastName, LocalDate dateOfBirth);
}