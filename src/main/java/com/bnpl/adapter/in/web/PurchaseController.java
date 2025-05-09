package com.bnpl.adapter.in.web;

import com.bnpl.adapter.in.web.dto.request.PurchaseRegistrationRequest;
import com.bnpl.adapter.in.web.dto.response.PurchaseRegistrationResponseDto;
import com.bnpl.application.port.in.PurchaseRegistrationCommand;
import com.bnpl.application.port.out.PurchaseRegistrationResponse;
import com.bnpl.application.service.PurchaseRegistrationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {
    private static final Logger log = LoggerFactory.getLogger(PurchaseController.class);
    
    private final PurchaseRegistrationService purchaseRegistrationService;
    
    public PurchaseController(PurchaseRegistrationService purchaseRegistrationService) {
        this.purchaseRegistrationService = purchaseRegistrationService;
    }
    
    @PostMapping
    public ResponseEntity<PurchaseRegistrationResponseDto> registerPurchase(
            @Valid @RequestBody PurchaseRegistrationRequest request) {
        log.info("Received purchase registration request: client {}, amount {}", 
                 request.getIdCliente(), request.getMontoCompra());
        
        // Convert request to command
        PurchaseRegistrationCommand command = new PurchaseRegistrationCommand(
                request.getIdCliente(), request.getMontoCompra());
        
        // Process registration
        PurchaseRegistrationResponse response = purchaseRegistrationService.registerPurchaseWithResponse(command);
        
        // Convert response to DTO
        PurchaseRegistrationResponseDto responseDto = new PurchaseRegistrationResponseDto(
                response.getPurchaseId(), 
                response.getTotalAmount(),
                response.getInstallmentAmount(),
                response.getPaymentDates());
        
        log.info("Purchase registration successful: {}", responseDto.getIdCompra());
        
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
