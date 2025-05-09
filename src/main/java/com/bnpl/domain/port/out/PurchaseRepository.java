package com.bnpl.domain.port.out;

import com.bnpl.domain.model.Purchase;

public interface PurchaseRepository {
    Purchase save(Purchase purchase);
}