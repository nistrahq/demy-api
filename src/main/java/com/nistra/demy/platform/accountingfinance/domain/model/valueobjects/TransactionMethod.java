package com.nistra.demy.platform.accountingfinance.domain.model.valueobjects;

public enum TransactionMethod {
    CASH,
    CREDIT_CARD,
    DEBIT_CARD,
    BANK_TRANSFER,
    WALLET,
    OTHER;

    public static TransactionMethod fromString(String method) {
        String normalized = method.replace("-", "_").replace(" ", "_");
        for (TransactionMethod tm : TransactionMethod.values()) {
            if (tm.name().equalsIgnoreCase(normalized)) {
                return tm;
            }
        }
        throw new IllegalArgumentException("Unknown transaction method: " + method);
    }
}
