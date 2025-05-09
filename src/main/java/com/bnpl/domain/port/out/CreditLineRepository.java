package com.bnpl.domain.port.out;

import java.util.Optional;
import com.bnpl.domain.model.CreditLine;

public interface CreditLineRepository {
    CreditLine save(CreditLine creditLine);
    Optional<CreditLine> findByClientId(Long clientId);
    void update(CreditLine creditLine);
}