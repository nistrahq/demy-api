package com.nistra.demy.platform.attendance.domain.services;

import com.nistra.demy.platform.attendance.domain.model.aggregates.ClassAttendance;
import com.nistra.demy.platform.attendance.domain.model.commands.CreateClassAttendanceCommand;

import java.util.Optional;

/**
 * ClassSessionCommandService
 * Is the contract for the command service
 * @author diego
 */
public interface ClassAttendanceCommandService {
    /**
     * Handles the create class attendance command
     * @param command The create class attendance comman
     * @return the created class attendance
     */

    Optional<ClassAttendance> handle (CreateClassAttendanceCommand command);
}
