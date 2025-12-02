package com.nistra.demy.platform.institution.infrastructure.persistence.jpa.repositories;

import com.nistra.demy.platform.institution.domain.model.aggregates.Administrator;
import com.nistra.demy.platform.shared.domain.model.valueobjects.DniNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {

    Optional<Administrator> findByDniNumber(DniNumber dniNumber);

    boolean existsByDniNumber(DniNumber dniNumber);
}
