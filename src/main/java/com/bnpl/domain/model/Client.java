package com.bnpl.domain.model;

import java.time.LocalDate;
import java.time.Period;

import com.bnpl.domain.model.enums.SchemeType;

public class Client {
    private Long id;
    private String name;
    private LocalDate birthDate;
    
    public Client(Long id, String name, LocalDate birthDate) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    // Domain logic
    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
    
    public boolean isEligibleForCredit() {
        int age = getAge();
        return age >= 18 && age <= 65;
    }
    
    public SchemeType determinePaymentScheme() {
        if (name != null && (name.toLowerCase().startsWith("c") || 
                            name.toLowerCase().startsWith("l") || 
                            name.toLowerCase().startsWith("h"))) {
            return SchemeType.SCHEME_1;
        } else if (id != null && id > 25) {
            return SchemeType.SCHEME_2;
        } else {
            return SchemeType.SCHEME_2; // Default scheme
        }
    }
}
