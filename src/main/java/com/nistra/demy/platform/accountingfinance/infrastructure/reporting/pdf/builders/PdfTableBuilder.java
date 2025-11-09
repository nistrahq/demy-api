package com.nistra.demy.platform.accountingfinance.infrastructure.reporting.pdf.builders;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.nistra.demy.platform.accountingfinance.domain.model.aggregates.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class PdfTableBuilder {

    private static final float[] TRANSACTION_TABLE_COLUMN_WIDTHS = {70f, 60f, 70f, 65f, 80f, 120f};
    private static final java.awt.Color HEADER_BACKGROUND_COLOR = new java.awt.Color(187, 222, 251);

    public Table buildTransactionsTable(List<Transaction> transactions) {
        Table table = createTableStructure();
        addHeaders(table);
        addTransactionRows(table, transactions);
        return table;
    }

    public Table buildCurrencySummaryTable(Map<String, BigDecimal> totalsByCurrency) {
        Table table = new Table(UnitValue.createPointArray(new float[]{120f, 120f}));
        table.setWidth(UnitValue.createPercentValue(35));
        table.setMarginTop(10);
        table.setMarginBottom(10);

        // Header
        addStyledSummaryHeaderCell(table, "Moneda");
        addStyledSummaryHeaderCell(table, "Total");

        // Data rows
        totalsByCurrency.forEach((currency, total) -> {
            table.addCell(createStyledSummaryCell(currency, TextAlignment.LEFT));
            table.addCell(createStyledSummaryCell(String.format("%,.2f", total), TextAlignment.RIGHT));
        });

        return table;
    }

    private PdfFont createBoldFont() {
        try {
            return PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear fuente bold", e);
        }
    }

    private PdfFont createRegularFont() {
        try {
            return PdfFontFactory.createFont(StandardFonts.HELVETICA);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear fuente regular", e);
        }
    }

    private Table createTableStructure() {
        Table table = new Table(UnitValue.createPointArray(TRANSACTION_TABLE_COLUMN_WIDTHS));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setKeepTogether(false);
        return table;
    }

    private void addHeaders(Table table) {
        addStyledHeaderCell(table, "Fecha");
        addStyledHeaderCell(table, "Tipo");
        addStyledHeaderCell(table, "Categoría");
        addStyledHeaderCell(table, "Método");
        addStyledHeaderCell(table, "Monto");
        addStyledHeaderCell(table, "Descripción");
    }

    private void addTransactionRows(Table table, List<Transaction> transactions) {
        transactions.forEach(transaction -> {
            String currency = transaction.getAmount().currency().toString();
            String formattedAmount = String.format("%,.2f %s",
                    transaction.getAmount().amount(), currency);

            table.addCell(createStyledCell(transaction.getTransactionDate().toString(), TextAlignment.LEFT));
            table.addCell(createStyledCell(transaction.getTransactionType().toString(), TextAlignment.LEFT));
            table.addCell(createStyledCell(transaction.getTransactionCategory().toString(), TextAlignment.LEFT));
            table.addCell(createStyledCell(transaction.getTransactionMethod().toString(), TextAlignment.LEFT));
            table.addCell(createStyledCell(formattedAmount, TextAlignment.RIGHT));
            table.addCell(createStyledCell(
                    transaction.getDescription() != null ? transaction.getDescription() : "-",
                    TextAlignment.LEFT));
        });
    }

    private void addStyledHeaderCell(Table table, String text) {
        Cell headerCell = new Cell()
                .add(new Paragraph(text)
                        .setFont(createBoldFont())
                        .setFontSize(9)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(ColorConstants.BLACK))
                .setBackgroundColor(new DeviceRgb(
                        HEADER_BACKGROUND_COLOR.getRed(),
                        HEADER_BACKGROUND_COLOR.getGreen(),
                        HEADER_BACKGROUND_COLOR.getBlue()))
                .simulateBold()
                .setPadding(3);

        table.addHeaderCell(headerCell);
    }

    private Cell createStyledCell(String text, TextAlignment alignment) {
        return new Cell()
                .add(new Paragraph(text)
                        .setFont(createRegularFont())
                        .setFontSize(8)
                        .setFontColor(ColorConstants.BLACK)
                        .setTextAlignment(alignment))
                .setPadding(3)
                .setBackgroundColor(ColorConstants.WHITE);
    }

    private void addStyledSummaryHeaderCell(Table table, String text) {
        Cell headerCell = new Cell()
                .add(new Paragraph(text)
                        .setFont(createBoldFont())
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(ColorConstants.BLACK))
                .setBackgroundColor(new DeviceRgb(
                        HEADER_BACKGROUND_COLOR.getRed(),
                        HEADER_BACKGROUND_COLOR.getGreen(),
                        HEADER_BACKGROUND_COLOR.getBlue()))
                .setPadding(5);

        table.addHeaderCell(headerCell);
    }

    private Cell createStyledSummaryCell(String text, TextAlignment alignment) {
        return new Cell()
                .add(new Paragraph(text)
                        .setFont(createRegularFont())
                        .setFontSize(9)
                        .setFontColor(ColorConstants.BLACK)
                        .setTextAlignment(alignment))
                .setPadding(5)
                .setBackgroundColor(ColorConstants.WHITE);
    }
}

