package com.nistra.demy.platform.accountingfinance.domain.services;

import com.nistra.demy.platform.accountingfinance.domain.model.aggregates.Transaction;
import com.nistra.demy.platform.accountingfinance.domain.model.queries.GetAllTransactionsQuery;

import java.util.List;

public interface TransactionQueryService {
    List<Transaction> handle(GetAllTransactionsQuery query);
}
