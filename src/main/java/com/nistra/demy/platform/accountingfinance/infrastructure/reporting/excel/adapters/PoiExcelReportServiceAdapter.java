package com.nistra.demy.platform.accountingfinance.infrastructure.reporting.excel.adapters;

import com.nistra.demy.platform.accountingfinance.domain.model.aggregates.Transaction;
import com.nistra.demy.platform.accountingfinance.infrastructure.reporting.excel.PoiExcelReportService;
import com.nistra.demy.platform.accountingfinance.infrastructure.reporting.excel.builders.ExcelTableBuilder;
import com.nistra.demy.platform.accountingfinance.infrastructure.reporting.excel.builders.ExcelWorkbookBuilder;
import com.nistra.demy.platform.accountingfinance.infrastructure.reporting.excel.formatters.TransactionExcelDataFormatter;
import com.nistra.demy.platform.shared.domain.model.valueobjects.Money;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            var uniqueCurrencies = dataFormatter.getUniqueCurrencies(transactions);

            CellStyle headerStyle = workbookBuilder.createHeaderStyle(workbook);
            Map<Currency, CellStyle> currencyStyles = new HashMap<>();
            Map<Currency, CellStyle> boldCurrencyStyles = new HashMap<>();

            for (Currency currency : uniqueCurrencies) {
                currencyStyles.put(currency, workbookBuilder.createCurrencyStyle(workbook, currency));
                boldCurrencyStyles.put(currency, workbookBuilder.createBoldCurrencyStyle(workbook, currency));
            }

            tableBuilder.addHeaders(sheet, headerStyle);

            int lastRowIdx = addTransactionRowsWithMultipleCurrencies(
                    sheet,
                    transactions,
                    currencyStyles
            );

            Map<Currency, Money> totalsByCurrency = dataFormatter.calculateTotalsByCurrency(transactions);

            tableBuilder.addTotalsByCurrencyTable(
                    sheet,
                    lastRowIdx,
                    totalsByCurrency,
                    headerStyle,
                    boldCurrencyStyles
            );

            workbookBuilder.autoSizeColumns(sheet, tableBuilder.getHeaderCount());

            return workbookBuilder.writeToBytes(workbook);

        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte Excel de transacciones: " + e.getMessage(), e);
        }
    }

    private int addTransactionRowsWithMultipleCurrencies(
            Sheet sheet,
            List<Transaction> transactions,
            Map<Currency, CellStyle> currencyStyles
    ) {
        int rowIdx = 1;
        for (Transaction transaction : transactions) {
            Row row = sheet.createRow(rowIdx++);
            Currency currency = transaction.getAmount().currency();
            CellStyle currencyStyle = currencyStyles.get(currency);
            mapTransactionToRow(transaction, row, currencyStyle);
        }
        return rowIdx;
    }

    private void mapTransactionToRow(Transaction transaction, Row row, CellStyle currencyStyle) {
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

        Cell amountCell = row.createCell(4);
        amountCell.setCellValue(
                dataFormatter.getAmountValue(transaction.getAmount())
        );
        amountCell.setCellStyle(currencyStyle);

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
