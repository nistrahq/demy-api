package com.nistra.demy.platform.accountingfinance.infrastructure.reporting;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.nistra.demy.platform.accountingfinance.domain.model.aggregates.Transaction;
import org.knowm.xchart.*;
import org.knowm.xchart.style.CategoryStyler;
import org.knowm.xchart.style.PieStyler;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PdfReportGenerator {
    public byte[] generateTransactionsReport(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return generateEmptyReport();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            var boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            var regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            Image logo = new Image(ImageDataFactory.create("src/main/resources/static/images/demy-combination-mark-original.png"))
                    .scaleToFit(100, 100)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(logo);

            Paragraph title = new Paragraph("Reporte de Transacciones")
                    .setFont(boldFont)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(title);

            Paragraph generatedAt = new Paragraph("Generado el: " +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setFont(regularFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(generatedAt);

            document.add(new Paragraph("\n"));

            float[] columnWidths = {85f, 75f, 85f, 80f, 110f, 150f};

            Table table = new Table(UnitValue.createPointArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            java.awt.Color lightBlue = new java.awt.Color(187, 222, 251);

            addStyledHeaderCell(table, "Fecha", lightBlue, boldFont);
            addStyledHeaderCell(table, "Tipo", lightBlue, boldFont);
            addStyledHeaderCell(table, "Categoría", lightBlue, boldFont);
            addStyledHeaderCell(table, "Método", lightBlue, boldFont);
            addStyledHeaderCell(table, "Monto", lightBlue, boldFont);
            addStyledHeaderCell(table, "Descripción", lightBlue, boldFont);

            Map<String, BigDecimal> totalsByCurrency = new HashMap<>();
            Map<String, BigDecimal> totalsByCategory = new HashMap<>();
            Map<String, BigDecimal> totalsOverTime = new HashMap<>();

            for (Transaction t : transactions) {
                String currency = t.getAmount().currency().toString();
                BigDecimal amount = t.getAmount().amount();
                String formattedAmount = String.format("%,.2f %s", amount, currency);
                String dateKey = t.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                table.addCell(createStyledCell(t.getTransactionDate().toString(), regularFont));
                table.addCell(createStyledCell(t.getTransactionType().toString(), regularFont));
                table.addCell(createStyledCell(t.getTransactionCategory().toString(), regularFont));
                table.addCell(createStyledCell(t.getTransactionMethod().toString(), regularFont));
                table.addCell(createStyledCell(formattedAmount, regularFont, TextAlignment.RIGHT));
                table.addCell(createStyledCell(
                        t.getDescription() != null ? t.getDescription() : "-", regularFont));

                totalsByCurrency.merge(currency, amount, BigDecimal::add);
                totalsByCategory.merge(t.getTransactionCategory().name(), amount, BigDecimal::add);
                totalsOverTime.merge(dateKey, t.getAmount().amount(), BigDecimal::add);
            }

            document.add(table);

            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Totales por moneda:")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT));

            for (var entry : totalsByCurrency.entrySet()) {
                document.add(new Paragraph(" - " + entry.getKey() + ": " + entry.getValue())
                        .setFont(regularFont)
                        .setFontSize(11)
                        .setMarginLeft(20));
            }

            document.add(new Paragraph("\n"));

            PieChart pieChart = new PieChartBuilder()
                    .width(450)
                    .height(300)
                    .title("Transacciones por Categoría")
                    .build();

            PieStyler pieStyle = pieChart.getStyler();
            pieStyle.setPlotBackgroundColor(java.awt.Color.WHITE);
            pieStyle.setChartBackgroundColor(java.awt.Color.WHITE);
            pieStyle.setLegendBackgroundColor(java.awt.Color.WHITE);
            pieStyle.setChartFontColor(java.awt.Color.DARK_GRAY);
            pieStyle.setSeriesColors(new java.awt.Color[]{
                    new java.awt.Color(93, 173, 226),
                    new java.awt.Color(241, 148, 138),
                    new java.awt.Color(130, 224, 170),
                    new java.awt.Color(247, 220, 111),
                    new java.awt.Color(165, 105, 189)
            });
            pieStyle.setLegendVisible(true);
            pieStyle.setPlotBorderVisible(false);
            pieStyle.setChartTitleFont(new java.awt.Font("Helvetica", java.awt.Font.BOLD, 14));
            pieStyle.setLegendFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 10));

            totalsByCategory.forEach(pieChart::addSeries);

            ByteArrayOutputStream pieOut = new ByteArrayOutputStream();
            BitmapEncoder.saveBitmap(pieChart, pieOut, BitmapEncoder.BitmapFormat.PNG);

            document.add(new Image(ImageDataFactory.create(pieOut.toByteArray()))
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setMarginTop(10)
                    .setMarginBottom(20));

            List<String> dates = totalsOverTime.keySet().stream().sorted().collect(Collectors.toList());
            List<BigDecimal> values = dates.stream().map(totalsOverTime::get).collect(Collectors.toList());

            CategoryChart timeChart = new CategoryChartBuilder()
                    .width(550)
                    .height(300)
                    .title("Evolución de Transacciones en el Tiempo")
                    .xAxisTitle("Fecha")
                    .yAxisTitle("Monto Total")
                    .build();

            CategoryStyler timeStyle = timeChart.getStyler();
            timeStyle.setPlotBackgroundColor(java.awt.Color.WHITE);
            timeStyle.setChartBackgroundColor(java.awt.Color.WHITE);
            timeStyle.setChartFontColor(java.awt.Color.DARK_GRAY);
            timeStyle.setLegendVisible(false);
            timeStyle.setLabelsVisible(true);
            timeStyle.setPlotGridVerticalLinesVisible(false);
            timeStyle.setPlotGridHorizontalLinesVisible(true);
            timeStyle.setPlotBorderVisible(false);
            timeStyle.setAxisTickLabelsFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 9));
            timeStyle.setSeriesColors(new java.awt.Color[]{
                    new java.awt.Color(52, 152, 219)
            });

            timeChart.addSeries("Transacciones", dates, values);

            ByteArrayOutputStream timeOut = new ByteArrayOutputStream();
            BitmapEncoder.saveBitmap(timeChart, timeOut, BitmapEncoder.BitmapFormat.PNG);

            document.add(new Image(ImageDataFactory.create(timeOut.toByteArray()))
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setMarginTop(10));

            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte PDF: " + e.getMessage(), e);
        }
    }

    private void addHeaderCell(Table table, String text) {
        try {
            var boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            Paragraph headerParagraph = new Paragraph(text)
                    .setFont(boldFont)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER);

            Cell headerCell = new Cell()
                    .add(headerParagraph)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);

            table.addHeaderCell(headerCell);

        } catch (Exception e) {
            throw new RuntimeException("Error creando celda de encabezado para PDF", e);
        }
    }

    private void addStyledHeaderCell(Table table, String text, java.awt.Color backgroundColor, com.itextpdf.kernel.font.PdfFont font) {
        Cell headerCell = new Cell()
                .add(new Paragraph(text)
                        .setFont(font)
                        .setFontSize(11)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(ColorConstants.BLACK))
                .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(
                        backgroundColor.getRed(),
                        backgroundColor.getGreen(),
                        backgroundColor.getBlue()))
                .simulateBold()
                .setPadding(5);

        table.addHeaderCell(headerCell);
    }

    private Cell createStyledCell(String text, com.itextpdf.kernel.font.PdfFont font) {
        return createStyledCell(text, font, TextAlignment.LEFT);
    }

    private Cell createStyledCell(String text, com.itextpdf.kernel.font.PdfFont font, TextAlignment alignment) {
        return new Cell()
                .add(new Paragraph(text)
                        .setFont(font)
                        .setFontSize(10)
                        .setFontColor(ColorConstants.BLACK)
                        .setTextAlignment(alignment))
                .setPadding(4)
                .setBackgroundColor(ColorConstants.WHITE);
    }

    private byte[] generateEmptyReport() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            document.add(new Paragraph("No se encontraron transacciones para los filtros seleccionados."));
        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte vacío", e);
        }
        return outputStream.toByteArray();
    }
}
