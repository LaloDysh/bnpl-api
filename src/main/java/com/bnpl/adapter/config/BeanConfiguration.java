package com.bnpl.adapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// import com.bnpl.adapter.out.persistence.ClientPersistenceAdapter;
// import com.bnpl.adapter.out.persistence.CreditLinePersistenceAdapter;
// import com.bnpl.adapter.out.persistence.LoanPersistenceAdapter;
// import com.bnpl.adapter.out.persistence.PurchasePersistenceAdapter;
import com.bnpl.application.service.ClientRegistrationService;
import com.bnpl.application.service.PurchaseRegistrationService;
import com.bnpl.domain.port.out.ClientRepository;
import com.bnpl.domain.port.out.CreditLineRepository;
import com.bnpl.domain.port.out.LoanRepository;
import com.bnpl.domain.port.out.PurchaseRepository;
import com.bnpl.domain.service.ClientService;
import com.bnpl.domain.service.CreditLineService;
import com.bnpl.domain.service.LoanService;
import com.bnpl.domain.service.PaymentSchemeService;
import com.bnpl.domain.service.PurchaseService;

@Configuration
public class BeanConfiguration {
    
    // Domain services
    @Bean
    public ClientService clientService(ClientRepository clientRepository) {
        return new ClientService(clientRepository);
    }
    
    @Bean
    public CreditLineService creditLineService(CreditLineRepository creditLineRepository) {
        return new CreditLineService(creditLineRepository);
    }
    
    @Bean
    public PaymentSchemeService paymentSchemeService() {
        return new PaymentSchemeService();
    }
    
    @Bean
    public PurchaseService purchaseService(PurchaseRepository purchaseRepository) {
        return new PurchaseService(purchaseRepository);
    }
    
    @Bean
    public LoanService loanService(LoanRepository loanRepository) {
        return new LoanService(loanRepository);
    }
    
    // Application services
    @Bean
    public ClientRegistrationService clientRegistrationService(
            ClientService clientService,
            CreditLineService creditLineService) {
        return new ClientRegistrationService(clientService, creditLineService);
    }
    
    @Bean
    public PurchaseRegistrationService purchaseRegistrationService(
            ClientRepository clientRepository,
            CreditLineRepository creditLineRepository,
            CreditLineService creditLineService,
            PurchaseService purchaseService,
            PaymentSchemeService paymentSchemeService,
            LoanService loanService) {
        return new PurchaseRegistrationService(
                clientRepository,
                creditLineRepository,
                creditLineService,
                purchaseService,
                paymentSchemeService,
                loanService
        );
    }
}
