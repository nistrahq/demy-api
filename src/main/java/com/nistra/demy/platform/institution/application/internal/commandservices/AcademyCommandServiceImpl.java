package com.nistra.demy.platform.institution.application.internal.commandservices;

import com.nistra.demy.platform.institution.domain.model.aggregates.Academy;
import com.nistra.demy.platform.institution.domain.model.commands.AssignAdministratorToAcademyCommand;
import com.nistra.demy.platform.institution.domain.model.commands.RegisterAcademyCommand;
import com.nistra.demy.platform.institution.domain.services.AcademyCommandService;
import com.nistra.demy.platform.institution.infrastructure.persistence.jpa.repositories.AcademyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the {@link AcademyCommandService} interface for handling academy registration commands.
 * <p>
 * This service is responsible for processing commands related to academy registration,
 * including validation and persistence of the academy entity.
 *
 * @author Salim Ramirez
 * @see AcademyCommandService
 * @see RegisterAcademyCommand
 * @since 1.0.0
 */
@Service
public class AcademyCommandServiceImpl implements AcademyCommandService {

    private final AcademyRepository academyRepository;

    /**
     * Constructs an instance of the class.
     *
     * @param academyRepository the repository used for academy persistence operations
     */
    public AcademyCommandServiceImpl(AcademyRepository academyRepository) {
        this.academyRepository = academyRepository;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method processes the provided command to create a new academy
     *
     * @param command the command containing the necessary data to register an academy
     * @return the identifier of the newly registered academy
     * @see RegisterAcademyCommand
     */
    @Override
    public Optional<Academy> handle(RegisterAcademyCommand command) {
        if (academyRepository.existsByEmailAddress(command.emailAddress()))
            throw new IllegalArgumentException("An academy with email %s already exists".formatted(command.emailAddress().email()));
        if (academyRepository.existsByRuc(command.ruc()))
            throw new IllegalArgumentException("An academy with RUC %s already exists".formatted(command.ruc().ruc()));
        var academy = new Academy(command);
        try {
            academyRepository.save(academy);
            return Optional.of(academy);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register academy: %s".formatted(e.getMessage()));
        }
    }

    @Override
    public void handle(AssignAdministratorToAcademyCommand command) {
        academyRepository.findById(command.academyId().academyId()).map(academy -> {
            academy.assignAdministrator(command.administratorId());
            academyRepository.save(academy);
            return academy;
        }).orElseThrow(() -> new IllegalArgumentException("No academy found with id " + command.academyId().academyId()));
    }
}
