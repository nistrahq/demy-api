package com.nistra.demy.platform.accountingfinance.domain.model.valueobjects;

import com.nistra.demy.platform.accountingfinance.domain.exceptions.InvalidTransactionCategoryException;

public enum TransactionCategory {
    STUDENT_ENROLLMENT,
    STUDENT_MONTHLY_FEE,
    STUDENT_ONE_TIME_PAYMENT,
    TEACHER_SALARY,
    OFFICE_SUPPLIES,
    OTHER;

    public static TransactionCategory fromString(String category) {
        String normalized = category.replace("-", "_").replace(" ", "_");
        for (TransactionCategory tc : TransactionCategory.values()) {
            if (tc.name().equalsIgnoreCase(normalized)) {
                return tc;
            }
        }
        throw new InvalidTransactionCategoryException(category);
    }
}
