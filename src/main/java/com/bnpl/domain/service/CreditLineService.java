package com.bnpl.domain.service;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bnpl.domain.exception.InsufficientCreditException;
import com.bnpl.domain.model.CreditLine;
import com.bnpl.domain.port.out.CreditLineRepository;

public class CreditLineService {
    private static final Logger log = LoggerFactory.getLogger(CreditLineService.class);
    
    private final CreditLineRepository creditLineRepository;
    
    public CreditLineService(CreditLineRepository creditLineRepository) {
        this.creditLineRepository = creditLineRepository;
    }
    
    public CreditLine createCreditLine(Long clientId, BigDecimal amount) {
        log.info("Creating credit line for client {}: {}", clientId, amount);
        
        CreditLine creditLine = new CreditLine(null, clientId, amount);
        return creditLineRepository.save(creditLine);
    }
    
    public void validateCreditAvailability(CreditLine creditLine, BigDecimal purchaseAmount) {
        log.info("Validating credit availability for client {}: requested {}, available {}", 
                 creditLine.getClientId(), purchaseAmount, creditLine.getAvailableAmount());
        
        if (!creditLine.hasSufficientCredit(purchaseAmount)) {
            log.warn("Insufficient credit for client {}: requested {}, available {}", 
                     creditLine.getClientId(), purchaseAmount, creditLine.getAvailableAmount());
            throw InsufficientCreditException.create(purchaseAmount, creditLine.getAvailableAmount());
        }
    }
    
    public void reduceCreditLine(CreditLine creditLine, BigDecimal amount) {
        log.info("Reducing credit line for client {} by {}", creditLine.getClientId(), amount);
        
        creditLine.reduceCreditBy(amount);
        creditLineRepository.update(creditLine);
    }
}
