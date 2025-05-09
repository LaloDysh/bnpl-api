package com.bnpl.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.bnpl.adapter.out.persistence.entity.ClientEntity;
import com.bnpl.domain.model.Client;

@Component
public class ClientEntityMapper {
    
    public ClientEntity toEntity(Client domain) {
        return new ClientEntity(
                domain.getId(),
                domain.getName(),
                domain.getBirthDate()
        );
    }
    
    public Client toDomain(ClientEntity entity) {
        return new Client(
                entity.getId(),
                entity.getName(),
                entity.getBirthDate()
        );
    }
}
