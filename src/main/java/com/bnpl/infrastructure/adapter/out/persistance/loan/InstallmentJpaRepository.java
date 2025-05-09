package com.bnpl.infrastructure.adapter.out.persistance.loan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InstallmentJpaRepository extends JpaRepository<InstallmentEntity, UUID> {
}