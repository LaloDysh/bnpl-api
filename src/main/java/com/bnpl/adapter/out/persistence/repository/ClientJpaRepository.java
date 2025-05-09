package com.bnpl.adapter.out.persistence.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bnpl.adapter.out.persistence.entity.ClientEntity;

@Repository
public interface ClientJpaRepository extends JpaRepository<ClientEntity, Long> {
}
