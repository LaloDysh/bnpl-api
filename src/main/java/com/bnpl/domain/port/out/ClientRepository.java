package com.bnpl.domain.port.out;

import java.util.Optional;
import com.bnpl.domain.model.Client;

public interface ClientRepository {
    Client save(Client client);
    Optional<Client> findById(Long id);
}