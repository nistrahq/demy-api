package com.nistra.demy.platform.scheduling.infrastructure.persistence.jpa.repositories;

import com.nistra.demy.platform.scheduling.domain.model.aggregates.WeeklySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link WeeklySchedule} entities.
 * Provides methods for performing CRUD operations and additional queries related to weekly schedules.
 *
 * Extends {@link JpaRepository} to inherit standard CRUD operations.
 */
@Repository
public interface WeeklyScheduleRepository extends JpaRepository<WeeklySchedule, Long> {

    /**
     * Finds a weekly schedule by its unique name.
     *
     * @param name The name of the weekly schedule.
     * @return An {@link Optional} containing the found weekly schedule, or an empty {@link Optional} if no schedule is found.
     */
    Optional<WeeklySchedule> findByName(String name);

    /**
     * Checks if a weekly schedule with the specified name already exists.
     *
     * @param name The name of the weekly schedule to check.
     * @return {@code true} if a weekly schedule with the given name exists, otherwise {@code false}.
     */
    boolean existsByName(String name);

    /**
     * Checks if a weekly schedule with the specified name exists, excluding a specific schedule by its ID.
     *
     * @param name The name of the weekly schedule to check.
     * @param id The ID of the weekly schedule to exclude from the check.
     * @return {@code true} if a weekly schedule with the given name exists and its ID is not the specified one, otherwise {@code false}.
     */
    boolean existsByNameAndIdNot(String name, Long id);
}