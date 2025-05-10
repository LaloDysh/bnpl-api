package com.bnpl.infrastructure.adapter.out.persistence.customer;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class CustomerEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "second_last_name", nullable = false)
    private String secondLastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "credit_line_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal creditLineAmount;

    @Column(name = "available_credit_line_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal availableCreditLineAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public BigDecimal getCreditLineAmount() {
        return creditLineAmount;
    }

    public void setCreditLineAmount(BigDecimal creditLineAmount) {
        this.creditLineAmount = creditLineAmount;
    }

    public BigDecimal getAvailableCreditLineAmount() {
        return availableCreditLineAmount;
    }

    public void setAvailableCreditLineAmount(BigDecimal availableCreditLineAmount) {
        this.availableCreditLineAmount = availableCreditLineAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
