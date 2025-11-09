package com.nistra.demy.platform.accountingfinance.infrastructure.reporting.excel.builders;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExcelWorkbookBuilder {

    private final Map<String, CellStyle> currencyStyleCache = new HashMap<>();

    public WorkbookContext createWorkbook(String sheetName) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);
        currencyStyleCache.clear(); // Limpiar cache para nuevo workbook
        return new WorkbookContext(workbook, sheet);
    }

    public CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        return headerStyle;
    }

    public CellStyle createTextStyle(Workbook workbook) {
        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setAlignment(HorizontalAlignment.LEFT);
        return textStyle;
    }

    public CellStyle createNumberStyle(Workbook workbook) {
        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.setAlignment(HorizontalAlignment.RIGHT);
        return numberStyle;
    }

    public CellStyle createCurrencyStyle(Workbook workbook, Currency currency) {
        String cacheKey = workbook.hashCode() + "_" + currency.getCurrencyCode();

        if (currencyStyleCache.containsKey(cacheKey)) {
            return currencyStyleCache.get(cacheKey);
        }

        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setAlignment(HorizontalAlignment.RIGHT);

        applyCurrencyFormat(currencyStyle, workbook, currency);

        currencyStyleCache.put(cacheKey, currencyStyle);
        return currencyStyle;
    }

    public CellStyle createBoldCurrencyStyle(Workbook workbook, Currency currency) {
        CellStyle boldCurrencyStyle = workbook.createCellStyle();
        boldCurrencyStyle.setAlignment(HorizontalAlignment.RIGHT);

        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldCurrencyStyle.setFont(boldFont);

        applyCurrencyFormat(boldCurrencyStyle, workbook, currency);

        return boldCurrencyStyle;
    }

    private void applyCurrencyFormat(CellStyle style, Workbook workbook, Currency currency) {
        DataFormat format = workbook.createDataFormat();
        String formatString = getAccountingFormatString(currency);
        style.setDataFormat(format.getFormat(formatString));
    }

    private String getAccountingFormatString(Currency currency) {
        return switch (currency.getCurrencyCode()) {
            case "USD" -> "_-$* #,##0.00_-;-$* #,##0.00_-;_-$* \"-\"??_-;_-@_-";
            case "PEN" -> "_-\"S/\"* #,##0.00_-;-\"S/\"* #,##0.00_-;_-\"S/\"* \"-\"??_-;_-@_-";
            case "EUR" -> "_-[$€-407]* #,##0.00_-;-[$€-407]* #,##0.00_-;_-[$€-407]* \"-\"??_-;_-@_-";
            default -> "_-\"" + currency.getSymbol() + "\"* #,##0.00_-;-\"" + currency.getSymbol() + "\"* #,##0.00_-;_-\"" + currency.getSymbol() + "\"* \"-\"??_-;_-@_-";
        };
    }

    public void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }

        int tipoColumnWidth = sheet.getColumnWidth(1);
        sheet.setColumnWidth(1, (int) (tipoColumnWidth * 1.3));

        int categoriaColumnWidth = sheet.getColumnWidth(2);
        sheet.setColumnWidth(2, (int) (categoriaColumnWidth * 1.2));
    }

    public byte[] writeToBytes(Workbook workbook) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al escribir workbook a bytes", e);
        }
    }

    public static class WorkbookContext implements AutoCloseable {
        private final Workbook workbook;
        private final Sheet sheet;

        public WorkbookContext(Workbook workbook, Sheet sheet) {
            this.workbook = workbook;
            this.sheet = sheet;
        }

        public Workbook getWorkbook() {
            return workbook;
        }

        public Sheet getSheet() {
            return sheet;
        }

        @Override
        public void close() {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("Error al cerrar workbook", e);
            }
        }
    }
}

