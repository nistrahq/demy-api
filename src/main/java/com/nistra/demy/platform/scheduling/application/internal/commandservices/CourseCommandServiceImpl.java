package com.nistra.demy.platform.scheduling.application.internal.commandservices;

import com.nistra.demy.platform.scheduling.domain.model.aggregates.Course;
import com.nistra.demy.platform.scheduling.domain.model.commands.CreateCourseCommand;
import com.nistra.demy.platform.scheduling.domain.model.commands.DeleteCourseCommand;
import com.nistra.demy.platform.scheduling.domain.model.commands.UpdateCourseCommand;
import com.nistra.demy.platform.scheduling.domain.services.CourseCommandService;
import com.nistra.demy.platform.scheduling.infrastructure.persistence.jpa.repositories.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Course Command Service Implementation
 * <p>This class implements the course command service interface and provides the business logic for handling course commands such as create, update, and delete operations.</p>
 */
@Service
public class CourseCommandServiceImpl implements CourseCommandService {

    private final CourseRepository courseRepository;

    /**
     * Constructor that initializes the service with the required repository.
     * @param courseRepository The course repository.
     */
    public CourseCommandServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * This method is used to handle the creation of a new course.
     * @param command The create course command containing the course data.
     * @return The ID of the created course.
     * @throws IllegalArgumentException if a course with the same name already exists or if there's an error saving the course.
     * @see CreateCourseCommand
     * @see Course
     */
    @Override
    public Long handle(CreateCourseCommand command) {
        if (courseRepository.existsByName(command.name())) {
            throw new IllegalArgumentException("Course with code " + command.name() + " already exists");
        }

        var course = new Course(command);
        try {
            courseRepository.save(course);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error saving course: " + e.getMessage(), e);
        }
        return course.getId();
    }

    /**
     * This method is used to handle the update of an existing course.
     * @param command The update course command containing the updated course data.
     * @return An optional with the updated course if successful, otherwise an empty optional.
     * @throws IllegalArgumentException if a course with the same name already exists, if the course to update is not found, or if there's an error updating the course.
     * @see UpdateCourseCommand
     * @see Course
     */
    @Override
    public Optional<Course> handle(UpdateCourseCommand command) {

        if (courseRepository.existsByNameAndIdNot(command.name(), command.courseId())) {
            throw new IllegalArgumentException("Course with name " + command.name() + " already exists");
        }

        var result = courseRepository.findById(command.courseId());
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Course with id " + command.courseId() + " not found");
        }

        var courseToUpdate = result.get();
        try {
            var updatedCourse = courseRepository.save(courseToUpdate.updateCourse(command));
            return Optional.of(updatedCourse);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error updating course: " + e.getMessage());
        }

    }

    /**
     * This method is used to handle the deletion of an existing course.
     * @param command The delete course command containing the course ID to delete.
     * @throws IllegalArgumentException if the course to delete is not found or if there's an error deleting the course.
     * @see DeleteCourseCommand
     */
    @Override
    public void handle(DeleteCourseCommand command) {
        if (!courseRepository.existsById(command.courseId())) {
            throw new IllegalArgumentException("Course with id " + command.courseId() + " not found");
        }
        try {
            courseRepository.deleteById(command.courseId());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error deleting course: " + e.getMessage(), e);
        }

    }
}
