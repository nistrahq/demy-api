package com.nistra.demy.platform.accountingfinance.application.internal.outboundservices.pdf;

import com.nistra.demy.platform.accountingfinance.domain.model.aggregates.Transaction;

import java.util.List;

public interface PdfReportingService {
    byte[] generateTransactionsPdfReport(List<Transaction> transactions);
}
