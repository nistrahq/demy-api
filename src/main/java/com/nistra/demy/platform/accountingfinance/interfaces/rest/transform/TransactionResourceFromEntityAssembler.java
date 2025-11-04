package com.nistra.demy.platform.accountingfinance.interfaces.rest.transform;

import com.nistra.demy.platform.accountingfinance.domain.model.aggregates.Transaction;
import com.nistra.demy.platform.accountingfinance.interfaces.rest.resources.TransactionResource;

public class TransactionResourceFromEntityAssembler {
    public static TransactionResource toResourceFromEntity(Transaction entity) {
        return new TransactionResource(
                entity.getId(),
                entity.getTransactionType().toString(),
                entity.getTransactionCategory().toString(),
                entity.getTransactionMethod().toString(),
                entity.getAmount().amount(),
                entity.getAmount().currency().getCurrencyCode(),
                entity.getDescription(),
                entity.getTransactionDate().toString()
        );
    }
}
