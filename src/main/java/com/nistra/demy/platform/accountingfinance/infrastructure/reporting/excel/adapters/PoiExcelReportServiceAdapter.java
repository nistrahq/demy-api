package com.nistra.demy.platform.accountingfinance.infrastructure.reporting.excel.adapters;

import com.nistra.demy.platform.accountingfinance.domain.model.aggregates.Transaction;
import com.nistra.demy.platform.accountingfinance.infrastructure.reporting.excel.PoiExcelReportService;
import com.nistra.demy.platform.accountingfinance.infrastructure.reporting.excel.builders.ExcelTableBuilder;
import com.nistra.demy.platform.accountingfinance.infrastructure.reporting.excel.builders.ExcelWorkbookBuilder;
import com.nistra.demy.platform.accountingfinance.infrastructure.reporting.excel.formatters.TransactionExcelDataFormatter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PoiExcelReportServiceAdapter implements PoiExcelReportService {

    private final ExcelWorkbookBuilder workbookBuilder;
    private final ExcelTableBuilder tableBuilder;
    private final TransactionExcelDataFormatter dataFormatter;

    public PoiExcelReportServiceAdapter(
            ExcelWorkbookBuilder workbookBuilder,
            ExcelTableBuilder tableBuilder,
            TransactionExcelDataFormatter dataFormatter
    ) {
        this.workbookBuilder = workbookBuilder;
        this.tableBuilder = tableBuilder;
        this.dataFormatter = dataFormatter;
    }

    @Override
    public byte[] generateTransactionsExcelReport(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return generateEmptyReport();
        }

        try (ExcelWorkbookBuilder.WorkbookContext context = workbookBuilder.createWorkbook("Transacciones")) {
            Workbook workbook = context.getWorkbook();
            Sheet sheet = context.getSheet();

            // Create styles
            CellStyle headerStyle = workbookBuilder.createHeaderStyle(workbook);

            // Add headers
            tableBuilder.addHeaders(sheet, headerStyle);

            // Add transaction rows
            int lastRowIdx = tableBuilder.addTransactionRows(
                    sheet,
                    transactions,
                    this::mapTransactionToRow
            );

            // Add total row
            BigDecimal total = dataFormatter.calculateTotalAmount(transactions);
            tableBuilder.addTotalRow(sheet, lastRowIdx, total, headerStyle);

            // Auto-size columns
            workbookBuilder.autoSizeColumns(sheet, tableBuilder.getHeaderCount());

            return workbookBuilder.writeToBytes(workbook);

        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte Excel de transacciones: " + e.getMessage(), e);
        }
    }

    private void mapTransactionToRow(Transaction transaction, Row row) {
        row.createCell(0).setCellValue(
                dataFormatter.formatDate(transaction.getTransactionDate())
        );
        row.createCell(1).setCellValue(
                transaction.getTransactionType().toString()
        );
        row.createCell(2).setCellValue(
                transaction.getTransactionCategory().toString()
        );
        row.createCell(3).setCellValue(
                transaction.getTransactionMethod().toString()
        );
        row.createCell(4).setCellValue(
                dataFormatter.formatAmount(transaction.getAmount().amount())
        );
        row.createCell(5).setCellValue(
                dataFormatter.formatDescription(transaction.getDescription())
        );
    }

    private byte[] generateEmptyReport() {
        try (ExcelWorkbookBuilder.WorkbookContext context = workbookBuilder.createWorkbook("Sin Datos")) {
            Sheet sheet = context.getSheet();
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("No se encontraron transacciones para los filtros seleccionados.");
            return workbookBuilder.writeToBytes(context.getWorkbook());
        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte vacío", e);
        }
    }
}
