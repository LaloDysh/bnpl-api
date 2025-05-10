package com.bnpl.infrastructure.adapter.out.persistence.customer;


import com.bnpl.domain.model.customer.CreditLine;
import com.bnpl.domain.model.customer.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceMapper {

    public CustomerEntity toEntity(Customer domain) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(domain.getId());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setSecondLastName(domain.getSecondLastName());
        entity.setDateOfBirth(domain.getDateOfBirth());
        entity.setCreditLineAmount(domain.getCreditLine().getTotalAmount());
        entity.setAvailableCreditLineAmount(domain.getCreditLine().getAvailableAmount());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    public Customer toDomain(CustomerEntity entity) {
        return Customer.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .secondLastName(entity.getSecondLastName())
                .dateOfBirth(entity.getDateOfBirth())
                .creditLine(mapCreditLine(entity))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private CreditLine mapCreditLine(CustomerEntity entity) {
        CreditLine creditLine = CreditLine.of(entity.getCreditLineAmount());
        // Adjust available amount to match the stored value
        creditLine.deduct(entity.getCreditLineAmount().subtract(entity.getAvailableCreditLineAmount()));
        return creditLine;
    }
}
