package com.bnpl.application.port.in;

import java.time.LocalDate;

public class ClientRegistrationCommand {
    private final String name;
    private final LocalDate birthDate;
    
    public ClientRegistrationCommand(String name, LocalDate birthDate) {
        this.name = name;
        this.birthDate = birthDate;
    }
    
    public String getName() {
        return name;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
}