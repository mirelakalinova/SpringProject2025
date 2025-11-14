package com.example.mkalinova.app.company.repo;

import com.example.mkalinova.app.company.data.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByName(String name);

    Optional<Company> findByUic(int uic);

    List<Company> findByClientIsNull();

    List<Company> findByClientId(Long id);


    List<Company> findAllByClientId(Long id);
}
