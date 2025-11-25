package com.example.mkalinova.app.client.repo;

import com.example.mkalinova.app.client.data.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findByPhone(String phoneNumber);

    List<Client> findAllByDeleteAdNull();


}
