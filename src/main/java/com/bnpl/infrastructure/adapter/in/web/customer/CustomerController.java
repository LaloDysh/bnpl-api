package com.bnpl.infrastructure.adapter.in.web.customer;

import com.bnpl.application.port.in.customer.CreateCustomerUseCase;
import com.bnpl.application.port.in.customer.GetCustomerUseCase;
import com.bnpl.application.service.security.JwtService;
import com.bnpl.domain.model.customer.Customer;
import com.bnpl.infrastructure.adapter.in.web.customer.request.CustomerRequest;
import com.bnpl.infrastructure.adapter.in.web.customer.response.CustomerResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/v1/customers")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final CustomerMapper customerMapper;
    private final JwtService jwtService;

    public CustomerController(
            CreateCustomerUseCase createCustomerUseCase,
            GetCustomerUseCase getCustomerUseCase,
            CustomerMapper customerMapper,
            JwtService jwtService) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.getCustomerUseCase = getCustomerUseCase;
        this.customerMapper = customerMapper;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        log.info("REST request to create a new customer");
        
        Customer customer = createCustomerUseCase.createCustomer(
                request.getFirstName(),
                request.getLastName(),
                request.getSecondLastName(),
                request.getDateOfBirth()
        );
        
        CustomerResponse response = customerMapper.toResponse(customer);
        
        String token = jwtService.generateToken(customer.getId());
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(customer.getId())
                .toUri();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        headers.add("X-Auth-Token", token);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(headers)
                .body(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable UUID customerId) {
        log.info("REST request to get customer with ID: {}", customerId);
        
        Customer customer = getCustomerUseCase.getCustomerById(customerId);
        CustomerResponse response = customerMapper.toResponse(customer);
        
        return ResponseEntity.ok(response);
    }
}