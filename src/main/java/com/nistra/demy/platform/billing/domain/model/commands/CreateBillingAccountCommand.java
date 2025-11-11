package com.nistra.demy.platform.billing.domain.model.commands;

import com.nistra.demy.platform.shared.domain.model.valueobjects.StudentId;

public record CreateBillingAccountCommand(
        StudentId studentId
) {
    public CreateBillingAccountCommand {
        if (studentId == null)
            throw new IllegalArgumentException("Student ID cannot be null");
    }
}
