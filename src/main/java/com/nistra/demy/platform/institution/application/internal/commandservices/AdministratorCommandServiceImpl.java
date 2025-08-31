package com.nistra.demy.platform.institution.application.internal.commandservices;

import com.nistra.demy.platform.institution.domain.exceptions.AdministratorAlreadyExistsException;
import com.nistra.demy.platform.institution.domain.model.aggregates.Administrator;
import com.nistra.demy.platform.institution.domain.model.commands.RegisterAdministratorCommand;
import com.nistra.demy.platform.institution.domain.model.valueobjects.AdministratorId;
import com.nistra.demy.platform.institution.domain.services.AdministratorCommandService;
import com.nistra.demy.platform.institution.infrastructure.persistence.jpa.repositories.AcademyRepository;
import com.nistra.demy.platform.institution.infrastructure.persistence.jpa.repositories.AdministratorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AdministratorCommandServiceImpl implements AdministratorCommandService {

    private final AcademyRepository academyRepository;
    private final AdministratorRepository administratorRepository;

    public AdministratorCommandServiceImpl(AcademyRepository academyRepository, AdministratorRepository administratorRepository) {
        this.academyRepository = academyRepository;
        this.administratorRepository = administratorRepository;
    }

    @Override
    @Transactional
    public Optional<Administrator> handle(RegisterAdministratorCommand command) {
        if (administratorRepository.existsByDniNumber(command.dniNumber()))
            throw new AdministratorAlreadyExistsException(command.dniNumber());
        try {
            var administrator = new Administrator(command);
            administrator.registerAdministrator(command.academyId().academyId(), command.userId().userId());
            administratorRepository.save(administrator);
            var academy = academyRepository.findById(command.academyId().academyId())
                    .orElseThrow(() -> new IllegalArgumentException("No academy found with id " + command.academyId().academyId()));
            academy.assignAdministrator(new AdministratorId(administrator.getId()));
            academyRepository.save(academy);
            return Optional.of(administrator);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register administrator: %s".formatted(e.getMessage()));
        }
    }
}