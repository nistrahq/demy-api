package com.nistra.demy.platform.scheduling.interfaces.rest.transform;

import com.nistra.demy.platform.scheduling.domain.model.aggregates.WeeklySchedule;
import com.nistra.demy.platform.scheduling.interfaces.rest.resources.WeeklyScheduleResource;

import java.util.stream.Collectors;

public class WeeklyScheduleResourceFromEntityAssembler {
    /**
     * Converts a WeeklySchedule entity to a WeeklyScheduleResource
     * @param entity the WeeklySchedule entity
     * @return the WeeklyScheduleResource
     */
    public static WeeklyScheduleResource toResourceFromEntity(WeeklySchedule entity) {
        var schedules = entity.getSchedules().stream()
                .map(ScheduleResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return new WeeklyScheduleResource(
                entity.getId(),
                entity.getName(),
                schedules
        );
    }
}