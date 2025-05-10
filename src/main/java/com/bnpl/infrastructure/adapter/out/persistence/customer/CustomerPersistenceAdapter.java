package com.bnpl.infrastructure.adapter.out.persistence.customer;

import com.bnpl.application.port.out.customer.CustomerRepository;
import com.bnpl.domain.model.customer.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerPersistenceAdapter implements CustomerRepository {

    private static final Logger log = LoggerFactory.getLogger(CustomerPersistenceAdapter.class);

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerPersistenceMapper customerPersistenceMapper;

    public CustomerPersistenceAdapter(
            CustomerJpaRepository customerJpaRepository,
            CustomerPersistenceMapper customerPersistenceMapper) {
        this.customerJpaRepository = customerJpaRepository;
        this.customerPersistenceMapper = customerPersistenceMapper;
    }

    @Override
    public Customer save(Customer customer) {
        log.debug("Saving customer with ID: {}", customer.getId());
        
        CustomerEntity entity = customerPersistenceMapper.toEntity(customer);
        CustomerEntity savedEntity = customerJpaRepository.save(entity);
        
        return customerPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        log.debug("Finding customer with ID: {}", customerId);
        
        return customerJpaRepository.findById(customerId)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsById(UUID customerId) {
        return customerJpaRepository.existsById(customerId);
    }

    @Override
    public void updateCreditLine(UUID customerId, Customer customer) {
        log.debug("Updating credit line for customer with ID: {}", customerId);
        
        customerJpaRepository.findById(customerId)
                .ifPresent(entity -> {
                    entity.setAvailableCreditLineAmount(customer.getCreditLine().getAvailableAmount());
                    customerJpaRepository.save(entity);
                });
    }
}
