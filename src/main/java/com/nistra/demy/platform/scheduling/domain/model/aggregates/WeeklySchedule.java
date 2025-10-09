package com.nistra.demy.platform.scheduling.domain.model.aggregates;

import com.nistra.demy.platform.scheduling.domain.model.commands.CreateWeeklyScheduleCommand;
import com.nistra.demy.platform.scheduling.domain.model.entities.Schedule;
import com.nistra.demy.platform.scheduling.domain.model.valueobjects.DayOfWeek;
import com.nistra.demy.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class WeeklySchedule extends AuditableAbstractAggregateRoot<WeeklySchedule> {

    private String name;

    @OneToMany(mappedBy = "weeklySchedule", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Schedule> schedules;

    /**
     * Default constructor
     */
    public WeeklySchedule() {
        this.name = "";
        this.schedules = new ArrayList<>();
    }

    /**
     * Constructor with name
     * @param name Weekly schedule name
     */
    public WeeklySchedule(String name) {
        this.name = name;
        this.schedules = new ArrayList<>();
    }

    /**
     * Constructor with command
     * @param command Create weekly schedule command
     */
    public WeeklySchedule(CreateWeeklyScheduleCommand command) {
        this.name = command.name();
        this.schedules = new ArrayList<>();
    }

    /**
     * Update name
     * @param name New name
     */
    public void updateName(String name) {
        this.name = name;
    }

    /**
     * Add schedule with parameters.
     *
     * @param startTime Start time of the schedule
     * @param endTime End time of the schedule
     * @param dayOfWeek Day of the week for the schedule
     * @param courseId ID of the course
     * @param classroomId ID of the classroom
     * @param teacherId ID of the teacher
     */
    public void addSchedule(String startTime, String endTime, DayOfWeek dayOfWeek, Long courseId, Long classroomId, Long teacherId) {
        var schedule = new Schedule(startTime, endTime, dayOfWeek, courseId, classroomId, teacherId);

        schedule.setWeeklySchedule(this);
        schedules.add(schedule);
    }

    /**
     * Remove a schedule from the list by its ID.
     *
     * @param scheduleId ID of the schedule to remove
     */
    public void removeSchedule(Long scheduleId) {
        schedules.removeIf(s -> s.getId().equals(scheduleId));
    }

}