package com.example.mkalinova.app.parts.repo;

import com.example.mkalinova.app.parts.data.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {
    List<Part> findAllByDeletedAtNull();

    Optional<Part> findByName(String name);
}
