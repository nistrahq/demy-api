package com.nistra.demy.platform.accountingfinance.infrastructure.reporting.excel.builders;

import com.nistra.demy.platform.accountingfinance.domain.model.aggregates.Transaction;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

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

    public void addTotalRow(Sheet sheet, int rowIdx, BigDecimal total, CellStyle headerStyle) {
        Row totalRow = sheet.createRow(rowIdx + 1);

        Cell totalLabelCell = totalRow.createCell(3);
        totalLabelCell.setCellValue("Total general:");
        totalLabelCell.setCellStyle(headerStyle);

        Cell totalValueCell = totalRow.createCell(4);
        totalValueCell.setCellValue(total.doubleValue());
        totalValueCell.setCellStyle(headerStyle);
    }

    public int getHeaderCount() {
        return TRANSACTION_HEADERS.length;
    }

    @FunctionalInterface
    public interface TransactionRowMapper {
        void mapTransactionToRow(Transaction transaction, Row row);
    }
}

