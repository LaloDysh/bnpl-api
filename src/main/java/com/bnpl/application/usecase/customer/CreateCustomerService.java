package com.bnpl.application.usecase.customer;

import com.bnpl.application.port.in.customer.CreateCustomerUseCase;
import com.bnpl.application.port.out.customer.CustomerRepository;
import com.bnpl.application.service.customer.CreditLineCalculator;
import com.bnpl.domain.model.customer.CreditLine;
import com.bnpl.domain.model.customer.Customer;
import com.bnpl.domain.model.error.BnplException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.UUID;

@Service
public class CreateCustomerService implements CreateCustomerUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateCustomerService.class);

    private final CustomerRepository customerRepository;
    private final CreditLineCalculator creditLineCalculator;

    public CreateCustomerService(CustomerRepository customerRepository, CreditLineCalculator creditLineCalculator) {
        this.customerRepository = customerRepository;
        this.creditLineCalculator = creditLineCalculator;
    }

    @Override
    @Transactional
    public Customer createCustomer(String firstName, String lastName, String secondLastName, LocalDate dateOfBirth) {
        log.info("Creating customer with first name: {}, last name: {}, second last name: {}, date of birth: {}", 
                firstName, lastName, secondLastName, dateOfBirth);

        LocalDate now = LocalDate.now();
        
        try {
            validateAge(dateOfBirth, now);
            
            BigDecimal creditLineAmount = creditLineCalculator.calculateCreditLine(dateOfBirth, now);
            
            Customer customer = Customer.builder()
                    .id(UUID.randomUUID())
                    .firstName(firstName)
                    .lastName(lastName)
                    .secondLastName(secondLastName)
                    .dateOfBirth(dateOfBirth)
                    .creditLine(CreditLine.of(creditLineAmount))
                    .createdAt(LocalDateTime.now())
                    .build();
            
            Customer savedCustomer = customerRepository.save(customer);
            log.info("Customer created successfully with ID: {}", savedCustomer.getId());
            
            return savedCustomer;
        } catch (IllegalArgumentException e) {
            log.error("Invalid age for customer creation: {}", e.getMessage());
            throw new BnplException.InvalidAgeException("/v1/customers");
        }
    }

    private void validateAge(LocalDate dateOfBirth, LocalDate currentDate) {
        int age = Period.between(dateOfBirth, currentDate).getYears();
        if (age < 18 || age > 65) {
            throw new IllegalArgumentException("Customer age must be between 18 and 65 years");
        }
    }
}
