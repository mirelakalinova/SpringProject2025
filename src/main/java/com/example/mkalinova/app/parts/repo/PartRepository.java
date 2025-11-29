package com.example.mkalinova.app.parts.repo;

import com.example.mkalinova.app.parts.data.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PartRepository extends JpaRepository<Part, UUID> {
	List<Part> findAllByDeletedAtNull();
	
	Optional<Part> findByName(String name);
}
