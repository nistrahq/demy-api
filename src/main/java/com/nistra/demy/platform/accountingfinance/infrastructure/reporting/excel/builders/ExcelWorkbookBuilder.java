package com.nistra.demy.platform.accountingfinance.infrastructure.reporting.excel.builders;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ExcelWorkbookBuilder {

    public WorkbookContext createWorkbook(String sheetName) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);
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

    public void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
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

