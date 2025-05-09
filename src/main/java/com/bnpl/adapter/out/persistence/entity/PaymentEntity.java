package com.bnpl.adapter.out.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class PaymentEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "loan_id", nullable = false)
    private Long loanId;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    
    @Column(name = "is_paid", nullable = false)
    private boolean isPaid;
    
    public PaymentEntity() {
        this.isPaid = false;
    }
    
    public PaymentEntity(Long id, Long loanId, LocalDate dueDate, BigDecimal amount) {
        this.id = id;
        this.loanId = loanId;
        this.dueDate = dueDate;
        this.amount = amount;
        this.isPaid = false;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getLoanId() {
        return loanId;
    }
    
    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public LocalDate getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public boolean isPaid() {
        return isPaid;
    }
    
    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}