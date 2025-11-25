package com.example.mkalinova.app.company.repo;

import com.example.mkalinova.app.company.data.dto.CompanyListDto;
import com.example.mkalinova.app.company.data.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Company> findByName(String name);

    Optional<Company> findByUic(String uic);

    List<Company> findByClientIsNull();

    List<Company> findByClientId(UUID id);


    List<Company> findAllByClientId(UUID id);

    List<Company> findAllByDeletedAtNull();

    List<Company> findAllByClientIdAndDeletedAtNull(UUID id);
}
