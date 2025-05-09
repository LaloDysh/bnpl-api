package com.bnpl.adapter.in.web;

import com.bnpl.adapter.in.web.dto.request.ClientRegistrationRequest;
import com.bnpl.adapter.in.web.dto.response.ClientRegistrationResponseDto;
import com.bnpl.application.port.in.ClientRegistrationCommand;
import com.bnpl.application.port.out.ClientRegistrationResponse;
import com.bnpl.application.service.ClientRegistrationService;
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
@RequestMapping("/api/clients")
public class ClientController {
    private static final Logger log = LoggerFactory.getLogger(ClientController.class);
    
    private final ClientRegistrationService clientRegistrationService;
    
    public ClientController(ClientRegistrationService clientRegistrationService) {
        this.clientRegistrationService = clientRegistrationService;
    }
    
    @PostMapping
    public ResponseEntity<ClientRegistrationResponseDto> registerClient(
            @Valid @RequestBody ClientRegistrationRequest request) {
        log.info("Received client registration request: {}", request.getName());
        
        // Convert request to command
        ClientRegistrationCommand command = new ClientRegistrationCommand(
                request.getName(), request.getBirthDate());
        
        // Process registration
        ClientRegistrationResponse response = clientRegistrationService.registerClientWithResponse(command);
        
        // Convert response to DTO
        ClientRegistrationResponseDto responseDto = new ClientRegistrationResponseDto(
                response.getClientId(), response.getAssignedCreditAmount());
        
        log.info("Client registration successful: {}", responseDto.getIdCliente());
        
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
