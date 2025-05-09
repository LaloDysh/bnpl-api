package com.bnpl.adapter.in.web.dto.request;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public class ClientRegistrationRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
    
    // Default constructor for JSON deserialization
    public ClientRegistrationRequest() {
    }
    
    public ClientRegistrationRequest(String name, LocalDate birthDate) {
        this.name = name;
        this.birthDate = birthDate;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}

