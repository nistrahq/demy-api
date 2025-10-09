package com.nistra.demy.platform.scheduling.application.internal.commandservices;

import com.nistra.demy.platform.scheduling.domain.model.aggregates.Classroom;
import com.nistra.demy.platform.scheduling.domain.model.commands.CreateClassroomCommand;
import com.nistra.demy.platform.scheduling.domain.model.commands.DeleteClassroomCommand;
import com.nistra.demy.platform.scheduling.domain.model.commands.UpdateClassroomCommand;
import com.nistra.demy.platform.scheduling.domain.services.ClassroomCommandService;
import com.nistra.demy.platform.scheduling.infrastructure.persistence.jpa.repositories.ClassroomRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Classroom Command Service Implementation
 * <p>This class implements the classroom command service interface and provides the business logic for handling classroom commands such as create, update, and delete operations.</p>
 */
@Service
public class ClassroomCommandServiceImpl implements ClassroomCommandService {

    private final ClassroomRepository classroomRepository;

    /**
     * Constructor that initializes the service with the required repository.
     * @param classroomRepository The classroom repository.
     */
    public ClassroomCommandServiceImpl(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    /**
     * This method is used to handle the creation of a new classroom.
     * @param command The create classroom command containing the classroom data.
     * @return The ID of the created classroom.
     * @throws IllegalArgumentException if a classroom with the same code already exists or if there's an error saving the classroom.
     * @see CreateClassroomCommand
     * @see Classroom
     */
    @Override
    public Long handle(CreateClassroomCommand command) {
        if (classroomRepository.existsByCode(command.code())) {
            throw new IllegalArgumentException("Classroom with code " + command.code() + " already exists");
        }

        var classroom = new Classroom(command);
        try {
            classroomRepository.save(classroom);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error saving classroom: %s".formatted(e.getMessage()));
        }
        return classroom.getId();
    }

    /**
     * This method is used to handle the update of an existing classroom.
     * @param command The update classroom command containing the updated classroom data.
     * @return An optional with the updated classroom if successful, otherwise an empty optional.
     * @throws IllegalArgumentException if a classroom with the same code already exists, if the classroom to update is not found, or if there's an error updating the classroom.
     * @see UpdateClassroomCommand
     * @see Classroom
     */
    @Override
    public Optional<Classroom> handle(UpdateClassroomCommand command) {

        if (classroomRepository.existsByCodeAndIdNot(command.code(), command.classroomId())) {
            throw new IllegalArgumentException("Classroom with code " + command.code() + " already exists");
        }

        var result = classroomRepository.findById(command.classroomId());
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Classroom with id " + command.classroomId() + " not found");
        }

        var classroomToUpdate = result.get();
        try {
            var updatedClassroom = classroomRepository.save(classroomToUpdate.updateClassroom(command));
            return Optional.of(updatedClassroom);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating classroom: " + e.getMessage());
        }
    }

    /**
     * This method is used to handle the deletion of an existing classroom.
     * @param command The delete classroom command containing the classroom ID to delete.
     * @throws IllegalArgumentException if the classroom to delete is not found or if there's an error deleting the classroom.
     * @see DeleteClassroomCommand
     */
    @Override
    public void handle(DeleteClassroomCommand command) {
        if (!classroomRepository.existsById(command.classroomId())) {
            throw new IllegalArgumentException("Classroom with id " + command.classroomId() + " not found");
        }

        try {
            classroomRepository.deleteById(command.classroomId());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error deleting classroom: " + e.getMessage(), e);
        }
    }
}
