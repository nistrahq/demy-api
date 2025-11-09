package com.nistra.demy.platform.accountingfinance.application.internal.commandservices;

import com.nistra.demy.platform.accountingfinance.application.internal.outboundservices.excel.ExcelReportingService;
import com.nistra.demy.platform.accountingfinance.application.internal.outboundservices.pdf.PdfReportingService;
import com.nistra.demy.platform.accountingfinance.domain.model.queries.GetAllTransactionsQuery;
import com.nistra.demy.platform.accountingfinance.domain.model.valueobjects.TransactionCategory;
import com.nistra.demy.platform.accountingfinance.domain.model.valueobjects.TransactionMethod;
import com.nistra.demy.platform.accountingfinance.domain.model.valueobjects.TransactionType;
import com.nistra.demy.platform.accountingfinance.domain.services.ReportCommandService;
import com.nistra.demy.platform.accountingfinance.domain.services.TransactionQueryService;
import org.springframework.stereotype.Service;

@Service
public class ReportCommandServiceImpl implements ReportCommandService {

    private final TransactionQueryService transactionQueryService;
    private final PdfReportingService pdfReportingService;
    private final ExcelReportingService excelReportingService;

    public ReportCommandServiceImpl(
            TransactionQueryService transactionQueryService,
            PdfReportingService pdfReportingService,
            ExcelReportingService excelReportingService
    ) {
        this.transactionQueryService = transactionQueryService;
        this.pdfReportingService = pdfReportingService;
        this.excelReportingService = excelReportingService;
    }

    @Override
    public byte[] generateTransactionsPdfReport(
            TransactionCategory category,
            TransactionMethod method,
            TransactionType type
    ) {
        var query = new GetAllTransactionsQuery(category, method, type);
        var transactions = transactionQueryService.handle(query);
        return pdfReportingService.generateTransactionsPdfReport(transactions);
    }

    @Override
    public byte[] generateTransactionsExcelReport(
            TransactionCategory category,
            TransactionMethod method,
            TransactionType type
    ) {
        var query = new GetAllTransactionsQuery(category, method, type);
        var transactions = transactionQueryService.handle(query);
        return excelReportingService.generateTransactionsExcelReport(transactions);
    }
}
