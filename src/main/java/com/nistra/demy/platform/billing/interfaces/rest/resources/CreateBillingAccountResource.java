package com.nistra.demy.platform.billing.interfaces.rest.resources;

public record CreateBillingAccountResource(
        Long studentId
) {
    public CreateBillingAccountResource {
        if (studentId == null || studentId <= 0)
            throw new IllegalArgumentException("Student ID must be a positive non-null value");
    }
}
