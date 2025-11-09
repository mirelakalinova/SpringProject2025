package com.example.mkalinova.app.client.repo;

import com.example.mkalinova.app.client.data.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
