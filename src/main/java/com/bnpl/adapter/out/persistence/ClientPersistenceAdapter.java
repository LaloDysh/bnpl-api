package com.bnpl.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.bnpl.adapter.out.persistence.entity.ClientEntity;
import com.bnpl.adapter.out.persistence.mapper.ClientEntityMapper;
import com.bnpl.adapter.out.persistence.repository.ClientJpaRepository;
import com.bnpl.domain.model.Client;
import com.bnpl.domain.port.out.ClientRepository;

@Component
public class ClientPersistenceAdapter implements ClientRepository {
    
    private final ClientJpaRepository clientJpaRepository;
    private final ClientEntityMapper clientEntityMapper;
    
    public ClientPersistenceAdapter(ClientJpaRepository clientJpaRepository, ClientEntityMapper clientEntityMapper) {
        this.clientJpaRepository = clientJpaRepository;
        this.clientEntityMapper = clientEntityMapper;
    }
    
    @Override
    public Client save(Client client) {
        ClientEntity clientEntity = clientEntityMapper.toEntity(client);
        ClientEntity savedEntity = clientJpaRepository.save(clientEntity);
        return clientEntityMapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Client> findById(Long id) {
        return clientJpaRepository.findById(id)
                .map(clientEntityMapper::toDomain);
    }
}