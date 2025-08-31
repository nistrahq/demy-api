package com.nistra.demy.platform.shared.domain.model.valueobjects;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

/**
 * Value object representing the unique identifier of an academy in the shared domain.
 * <p>
 * Encapsulates the academy ID ensuring it is not null and greater than zero.
 * Used as an embeddable in the persistence layer.
 *
 * @author Salim Ramirez
 * @see IllegalArgumentException
 * @since 1.0.0
 */
@Embeddable
public record AcademyId(
        @Column(nullable = false)
        @NotNull
        Long academyId
) {
    /**
     * Default constructor required by JPA.
     * Initializes the academy ID with a default value of 0.
     */
    public AcademyId() {
        this(0L);
    }

    /**
     * Constructs an AcademyId with the specified ID.
     *
     * @param academyId the unique identifier for the academy
     * @throws IllegalArgumentException if academyId is null or less than or equal to zero
     */
    public AcademyId {
        if (academyId == null || academyId <= 0)
            throw new IllegalArgumentException("Academy ID cannot be null or less than or equal to zero");
    }
}
