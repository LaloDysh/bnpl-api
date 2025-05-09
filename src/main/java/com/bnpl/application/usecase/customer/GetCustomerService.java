package com.bnpl.application.usecase.customer;

import com.bnpl.application.port.in.customer.GetCustomerUseCase;
import com.bnpl.application.port.out.customer.CustomerRepository;
import com.bnpl.domain.model.customer.Customer;
import com.bnpl.domain.model.error.BnplException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetCustomerService implements GetCustomerUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetCustomerService.class);

    private final CustomerRepository customerRepository;

    public GetCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerById(UUID customerId) {
        log.info("Getting customer with ID: {}", customerId);
        
        return customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", customerId);
                    return new BnplException.CustomerNotFoundException("/v1/customers/" + customerId);
                });
    }
}
