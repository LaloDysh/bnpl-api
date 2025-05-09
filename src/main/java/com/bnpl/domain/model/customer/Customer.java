package com.bnpl.domain.model.customer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Customer {
    private UUID id;
    private String firstName;
    private String lastName;
    private String secondLastName;
    private LocalDate dateOfBirth;
    private CreditLine creditLine;
    private LocalDateTime createdAt;

    private Customer(Builder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.secondLastName = builder.secondLastName;
        this.dateOfBirth = builder.dateOfBirth;
        this.creditLine = builder.creditLine;
        this.createdAt = builder.createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public CreditLine getCreditLine() {
        return creditLine;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public static class Builder {
        private UUID id;
        private String firstName;
        private String lastName;
        private String secondLastName;
        private LocalDate dateOfBirth;
        private CreditLine creditLine;
        private LocalDateTime createdAt;

        private Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder secondLastName(String secondLastName) {
            this.secondLastName = secondLastName;
            return this;
        }

        public Builder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder creditLine(CreditLine creditLine) {
            this.creditLine = creditLine;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }
}