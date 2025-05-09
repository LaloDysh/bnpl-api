package com.bnpl.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bnpl.domain.model.Purchase;
import com.bnpl.domain.model.enums.SchemeType;
import com.bnpl.domain.port.out.PurchaseRepository;

public class PurchaseService {
    private static final Logger log = LoggerFactory.getLogger(PurchaseService.class);
    
    private final PurchaseRepository purchaseRepository;
    
    public PurchaseService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }
    
    public Purchase createPurchase(Long clientId, BigDecimal amount, SchemeType schemeType) {
        log.info("Creating purchase for client {}: amount {}, scheme {}", clientId, amount, schemeType);
        
        Purchase purchase = new Purchase(null, clientId, amount, LocalDate.now(), schemeType);
        return purchaseRepository.save(purchase);
    }
}
