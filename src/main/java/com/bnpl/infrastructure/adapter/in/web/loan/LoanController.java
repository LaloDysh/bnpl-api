package com.bnpl.infrastructure.adapter.in.web.loan;

import com.bnpl.application.port.in.loan.CreateLoanUseCase;
import com.bnpl.application.port.in.loan.GetLoanUseCase;
import com.bnpl.domain.model.loan.Loan;
import com.bnpl.infrastructure.adapter.in.web.loan.request.LoanRequest;
import com.bnpl.infrastructure.adapter.in.web.loan.response.LoanResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/v1/loans")
public class LoanController {

    private static final Logger log = LoggerFactory.getLogger(LoanController.class);

    private final CreateLoanUseCase createLoanUseCase;
    private final GetLoanUseCase getLoanUseCase;
    private final LoanMapper loanMapper;

    public LoanController(
            CreateLoanUseCase createLoanUseCase,
            GetLoanUseCase getLoanUseCase,
            LoanMapper loanMapper) {
        this.createLoanUseCase = createLoanUseCase;
        this.getLoanUseCase = getLoanUseCase;
        this.loanMapper = loanMapper;
    }

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody LoanRequest request) {
        log.info("REST request to create a new loan for customer ID: {} with amount: {}", 
                request.getCustomerId(), request.getAmount());
        
        Loan loan = createLoanUseCase.createLoan(request.getCustomerId(), request.getAmount());
        LoanResponse response = loanMapper.toResponse(loan);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(loan.getId())
                .toUri();
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(location)
                .body(response);
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponse> getLoanById(@PathVariable UUID loanId) {
        log.info("REST request to get loan with ID: {}", loanId);
        
        Loan loan = getLoanUseCase.getLoanById(loanId);
        LoanResponse response = loanMapper.toResponse(loan);
        
        return ResponseEntity.ok(response);
    }
}