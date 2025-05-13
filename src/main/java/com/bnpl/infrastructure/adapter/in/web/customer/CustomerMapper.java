package com.bnpl.infrastructure.adapter.in.web.customer;

import com.bnpl.domain.model.customer.Customer;
import com.bnpl.infrastructure.adapter.in.web.customer.response.CustomerResponse;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setCreatedAt(customer.getCreatedAt());
        response.setCreditLineAmount(customer.getCreditLine().getTotalAmount());
        response.setAvailableCreditLineAmount(customer.getCreditLine().getAvailableAmount());
        return response;
    }
}