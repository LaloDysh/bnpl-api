package com.bnpl.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bnpl.adapter.out.persistence.entity.LoanEntity;

@Repository
public interface LoanJpaRepository extends JpaRepository<LoanEntity, Long> {
}