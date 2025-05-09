package com.bnpl.adapter.out.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bnpl.adapter.out.persistence.entity.CreditLineEntity;

@Repository
public interface CreditLineJpaRepository extends JpaRepository<CreditLineEntity, Long> {
    Optional<CreditLineEntity> findByClientId(Long clientId);
}