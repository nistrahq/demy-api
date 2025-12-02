package com.nistra.demy.platform.enrollment.interfaces.rest.resources;

import com.nistra.demy.platform.shared.domain.model.valueobjects.AcademyId;

import java.time.LocalDate;

/**
 * Create academic period resource.
 */
public record CreateAcademicPeriodResource(String periodName, LocalDate startDate, LocalDate endDate, Boolean isActive) {

    /**
     * Validates the resource.
     * @throws IllegalArgumentException if any field is null or if the dates are invalid.
     */
    public CreateAcademicPeriodResource {
        if (periodName == null || periodName.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date is required");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

    }
}