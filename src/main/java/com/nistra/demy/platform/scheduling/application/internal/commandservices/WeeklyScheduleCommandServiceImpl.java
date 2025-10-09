package com.nistra.demy.platform.scheduling.application.internal.commandservices;

import com.nistra.demy.platform.scheduling.application.internal.outboundservices.acl.ExternalInstitutionService;
import com.nistra.demy.platform.scheduling.domain.model.aggregates.WeeklySchedule;
import com.nistra.demy.platform.scheduling.domain.model.commands.*;
import com.nistra.demy.platform.scheduling.domain.model.entities.Schedule;
import com.nistra.demy.platform.scheduling.domain.model.valueobjects.DayOfWeek;
import com.nistra.demy.platform.scheduling.domain.services.WeeklyScheduleCommandService;
import com.nistra.demy.platform.scheduling.infrastructure.persistence.jpa.repositories.ScheduleRepository;
import com.nistra.demy.platform.scheduling.infrastructure.persistence.jpa.repositories.WeeklyScheduleRepository;
import com.nistra.demy.platform.institution.domain.model.valueobjects.UserId;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Weekly Schedule Command Service Implementation
 * <p>This class implements the weekly schedule command service interface and provides the business logic for handling weekly schedule commands such as create, update, delete, and schedule management operations.</p>
 */
@Service
public class WeeklyScheduleCommandServiceImpl implements WeeklyScheduleCommandService {

    private final WeeklyScheduleRepository weeklyScheduleRepository;
    private final ExternalInstitutionService externalIamService;
    private final ScheduleRepository scheduleRepository;

    /**
     * Constructor that initializes the service with the required repositories and external services.
     * @param weeklyScheduleRepository The weekly schedule repository.
     * @param externalIamService The external IAM service for teacher validation.
     * @param scheduleRepository The schedule repository.
     */
    public WeeklyScheduleCommandServiceImpl(WeeklyScheduleRepository weeklyScheduleRepository, ExternalInstitutionService externalIamService, ScheduleRepository scheduleRepository) {
        this.weeklyScheduleRepository = weeklyScheduleRepository;
        this.externalIamService = externalIamService;
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * This method is used to handle the creation of a new weekly schedule.
     * @param command The create weekly schedule command containing the weekly schedule data.
     * @return The ID of the created weekly schedule.
     * @throws IllegalArgumentException if a weekly schedule with the same name already exists.
     * @see CreateWeeklyScheduleCommand
     * @see WeeklySchedule
     */
    @Override
    public Long handle(CreateWeeklyScheduleCommand command) {
        if (weeklyScheduleRepository.existsByName(command.name())) {
            throw new IllegalArgumentException("Weekly schedule with name " + command.name() + " already exists");
        }

        var weeklySchedule = new WeeklySchedule(command);
        weeklyScheduleRepository.save(weeklySchedule);
        return weeklySchedule.getId();
    }

    /**
     * This method is used to handle the update of a weekly schedule name.
     * @param command The update weekly schedule name command containing the new name data.
     * @return An optional with the updated weekly schedule if successful.
     * @throws IllegalArgumentException if the weekly schedule is not found or if a weekly schedule with the same name already exists.
     * @see UpdateWeeklyScheduleNameCommand
     * @see WeeklySchedule
     */
    @Override
    public Optional<WeeklySchedule> handle(UpdateWeeklyScheduleNameCommand command) {
        var weeklyScheduleOpt = weeklyScheduleRepository.findById(command.weeklyScheduleId());
        if (weeklyScheduleOpt.isEmpty()) {
            throw new IllegalArgumentException("Weekly schedule with id " + command.weeklyScheduleId() + " not found");
        }

        if (weeklyScheduleRepository.existsByNameAndIdNot(command.name(), command.weeklyScheduleId())) {
            throw new IllegalArgumentException("Weekly schedule with name " + command.name() + " already exists");
        }

        var weeklySchedule = weeklyScheduleOpt.get();
        weeklySchedule.updateName(command.name());
        weeklyScheduleRepository.save(weeklySchedule);
        return Optional.of(weeklySchedule);
    }

    /**
     * This method is used to handle adding a schedule to a weekly schedule.
     * @param command The add schedule to weekly command containing the schedule data.
     * @return An optional with the updated weekly schedule if successful.
     * @throws IllegalArgumentException if the weekly schedule is not found or if the teacher is not found.
     * @see AddScheduleToWeeklyCommand
     * @see WeeklySchedule
     * @see DayOfWeek
     */
    @Override
    public Optional<WeeklySchedule> handle(AddScheduleToWeeklyCommand command) {
        var weeklyScheduleOpt = weeklyScheduleRepository.findById(command.weeklyScheduleId());
        if (weeklyScheduleOpt.isEmpty()) {
            throw new IllegalArgumentException("Weekly schedule with id " + command.weeklyScheduleId() + " not found");
        }

        var weeklySchedule = weeklyScheduleOpt.get();
        var dayOfWeek = DayOfWeek.valueOf(command.dayOfWeek().toUpperCase());

        UserId teacherId = externalIamService
                .fetchTeacherIdByFullName(command.teacherFirstName(), command.teacherLastName())
                .orElseThrow(() -> new IllegalArgumentException("No teacher with that fullname was found"));

        weeklySchedule.addSchedule(
                command.startTime(),
                command.endTime(),
                dayOfWeek,
                command.courseId(),
                command.classroomId(),
                teacherId.userId()
        );

        weeklyScheduleRepository.save(weeklySchedule);
        return Optional.of(weeklySchedule);
    }

    /**
     * This method is used to handle removing a schedule from a weekly schedule.
     * @param command The remove schedule from weekly command containing the schedule ID to remove.
     * @return An optional with the updated weekly schedule if successful.
     * @throws IllegalArgumentException if the weekly schedule is not found.
     * @see RemoveScheduleFromWeeklyCommand
     * @see WeeklySchedule
     */
    @Override
    public Optional<WeeklySchedule> handle(RemoveScheduleFromWeeklyCommand command) {
        var weeklyScheduleOpt = weeklyScheduleRepository.findById(command.weeklyScheduleId());
        if (weeklyScheduleOpt.isEmpty()) {
            throw new IllegalArgumentException("Weekly schedule with id " + command.weeklyScheduleId() + " not found");
        }

        var weeklySchedule = weeklyScheduleOpt.get();
        weeklySchedule.removeSchedule(command.scheduleId());
        weeklyScheduleRepository.save(weeklySchedule);
        return Optional.of(weeklySchedule);
    }

    /**
     * This method is used to handle the deletion of an existing weekly schedule.
     * @param command The delete weekly schedule command containing the weekly schedule ID to delete.
     * @throws IllegalArgumentException if the weekly schedule to delete is not found or if there's an error deleting the weekly schedule.
     * @see DeleteWeeklyScheduleCommand
     */
    @Override
    public void handle(DeleteWeeklyScheduleCommand command) {
        if (!weeklyScheduleRepository.existsById(command.weeklyScheduleId())) {
            throw new IllegalArgumentException("WeeklySchedule with id " + command.weeklyScheduleId() + " not found");
        }
        try {
            weeklyScheduleRepository.deleteById(command.weeklyScheduleId());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error deleting weekly schedule: " + e.getMessage(), e);
        }

    }

    /**
     * This method is used to handle the update of an existing schedule.
     * @param command The update schedule command containing the updated schedule data.
     * @return An optional with the updated schedule if successful.
     * @throws IllegalArgumentException if the schedule to update is not found.
     * @see UpdateScheduleCommand
     * @see Schedule
     * @see DayOfWeek
     */
    @Override
    public Optional<Schedule> handle(UpdateScheduleCommand command) {
        var scheduleOpt = scheduleRepository.findById(command.scheduleId());
        if (scheduleOpt.isEmpty()) {
            throw new IllegalArgumentException("Schedule with id " + command.scheduleId() + " not found");
        }
        var schedule = scheduleOpt.get();
        schedule.updateSchedule(
            command.classroomId(),
            command.startTime(),
            command.endTime(),
            DayOfWeek.valueOf(command.dayOfWeek().toUpperCase())
        );
        scheduleRepository.save(schedule);
        return Optional.of(schedule);
    }
}
