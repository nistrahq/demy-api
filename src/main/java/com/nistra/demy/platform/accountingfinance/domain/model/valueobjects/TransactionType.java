package com.nistra.demy.platform.accountingfinance.domain.model.valueobjects;

import com.nistra.demy.platform.accountingfinance.domain.exceptions.InvalidTransactionTypeException;

public enum TransactionType {
    INCOME,
    EXPENSE;

    public static TransactionType fromString(String type) {
        String normalized = type.replace("-", "_").replace(" ", "_");
        for (TransactionType tt : TransactionType.values()) {
            if (tt.name().equalsIgnoreCase(normalized)) {
                return tt;
            }
        }
        throw new InvalidTransactionTypeException(type);
    }
}
