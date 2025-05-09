package com.bnpl.adapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI bnplOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BNPL API")
                        .description("Buy Now Pay Later API for managing credit and purchases")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("BNPL API Team")
                                .email("api@bnpl.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}

