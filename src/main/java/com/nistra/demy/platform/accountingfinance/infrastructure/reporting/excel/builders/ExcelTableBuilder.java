package com.nistra.demy.platform.accountingfinance.infrastructure.reporting.excel.builders;

import com.nistra.demy.platform.accountingfinance.domain.model.aggregates.Transaction;
import com.nistra.demy.platform.shared.domain.model.valueobjects.Money;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Map;

@Component
public class ExcelTableBuilder {

    private static final String[] TRANSACTION_HEADERS = {
            "Fecha", "Tipo", "Categoría", "Método", "Monto", "Descripción"
    };

    public void addHeaders(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < TRANSACTION_HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(TRANSACTION_HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    public int addTransactionRows(
            Sheet sheet,
            List<Transaction> transactions,
            TransactionRowMapper rowMapper
    ) {
        int rowIdx = 1;
        for (Transaction transaction : transactions) {
            Row row = sheet.createRow(rowIdx++);
            rowMapper.mapTransactionToRow(transaction, row);
        }
        return rowIdx;
    }

    public int addTotalsByCurrencyTable(
            Sheet sheet,
            int startRowIdx,
            Map<Currency, Money> totalsByCurrency,
            CellStyle headerStyle,
            Map<Currency, CellStyle> currencyStyles
    ) {
        int currentRow = startRowIdx + 1;

        Row titleRow = sheet.createRow(currentRow++);
        Cell titleCell = titleRow.createCell(3);
        titleCell.setCellValue("Totales por Moneda:");
        titleCell.setCellStyle(headerStyle);

        Row headerRow = sheet.createRow(currentRow++);

        Cell currencyHeaderCell = headerRow.createCell(3);
        currencyHeaderCell.setCellValue("Moneda");
        currencyHeaderCell.setCellStyle(headerStyle);

        Cell amountHeaderCell = headerRow.createCell(4);
        amountHeaderCell.setCellValue("Total");
        amountHeaderCell.setCellStyle(headerStyle);

        final int[] rowCounter = {currentRow};

        totalsByCurrency.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Currency::getCurrencyCode)))
                .forEach(entry -> {
                    Currency currency = entry.getKey();
                    Money total = entry.getValue();

                    Row totalRow = sheet.createRow(rowCounter[0]++);

                    Cell currencyCell = totalRow.createCell(3);
                    currencyCell.setCellValue(currency.getCurrencyCode() + " - " + currency.getDisplayName());

                    Cell totalCell = totalRow.createCell(4);
                    totalCell.setCellValue(total.amount().doubleValue());
                    totalCell.setCellStyle(currencyStyles.get(currency));
                });

        return rowCounter[0];
    }

    public int getHeaderCount() {
        return TRANSACTION_HEADERS.length;
    }

    @FunctionalInterface
    public interface TransactionRowMapper {
        void mapTransactionToRow(Transaction transaction, Row row);
    }
}

