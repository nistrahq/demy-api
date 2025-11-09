package com.nistra.demy.platform.accountingfinance.application.internal.outboundservices.excel;

import com.nistra.demy.platform.accountingfinance.domain.model.aggregates.Transaction;

import java.util.List;

public interface ExcelReportingService {
    byte[] generateTransactionsExcelReport(List<Transaction> transactions);
}
