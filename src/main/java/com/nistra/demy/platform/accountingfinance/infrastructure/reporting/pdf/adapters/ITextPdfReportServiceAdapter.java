package com.nistra.demy.platform.accountingfinance.infrastructure.reporting.pdf.adapters;

import com.itextpdf.layout.element.Table;
import com.nistra.demy.platform.accountingfinance.domain.model.aggregates.Transaction;
import com.nistra.demy.platform.accountingfinance.infrastructure.reporting.pdf.ITextPdfReportService;
import com.nistra.demy.platform.accountingfinance.infrastructure.reporting.pdf.builders.PdfDocumentBuilder;
import com.nistra.demy.platform.accountingfinance.infrastructure.reporting.pdf.builders.PdfTableBuilder;
import com.nistra.demy.platform.accountingfinance.infrastructure.reporting.pdf.charts.PdfChartGenerator;
import com.nistra.demy.platform.accountingfinance.infrastructure.reporting.pdf.formatters.TransactionPdfDataFormatter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ITextPdfReportServiceAdapter implements ITextPdfReportService {

    private final PdfDocumentBuilder documentBuilder;
    private final PdfTableBuilder tableBuilder;
    private final PdfChartGenerator chartGenerator;
    private final TransactionPdfDataFormatter dataFormatter;

    public ITextPdfReportServiceAdapter(
            PdfDocumentBuilder documentBuilder,
            PdfTableBuilder tableBuilder,
            PdfChartGenerator chartGenerator,
            TransactionPdfDataFormatter dataFormatter
    ) {
        this.documentBuilder = documentBuilder;
        this.tableBuilder = tableBuilder;
        this.chartGenerator = chartGenerator;
        this.dataFormatter = dataFormatter;
    }

    @Override
    public byte[] generateTransactionsPdfReport(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return generateEmptyReport();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PdfDocumentBuilder.DocumentContext context = documentBuilder.createDocument(outputStream)) {
            var document = context.getDocument();

            // Header section
            addReportHeader(document);

            // Transactions table
            addTransactionsTable(document, transactions);

            // Summary section
            addSummarySection(document, transactions);

            // Charts section
            addChartsSection(document, transactions);

        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte PDF de transacciones: " + e.getMessage(), e);
        }

        return outputStream.toByteArray();
    }

    private void addReportHeader(com.itextpdf.layout.Document document) {
        documentBuilder.addLogo(document);
        documentBuilder.addSpacing(document);
        documentBuilder.addTitle(document, "Reporte de Transacciones");
        documentBuilder.addDescription(document,
                "Este documento presenta un análisis detallado de las transacciones registradas, " +
                "incluyendo información general, totales por moneda y análisis gráfico de gastos e ingresos.");
        documentBuilder.addGeneratedDate(document);
        documentBuilder.addSpacing(document);
    }

    private void addTransactionsTable(com.itextpdf.layout.Document document, List<Transaction> transactions) {
        Table table = tableBuilder.buildTransactionsTable(transactions);
        document.add(table);
        documentBuilder.addSpacing(document);
    }

    private void addSummarySection(com.itextpdf.layout.Document document, List<Transaction> transactions) {
        documentBuilder.addSectionTitle(document, "Resumen Financiero");
        documentBuilder.addSectionDescription(document,
                "A continuación se presentan los totales acumulados de todas las transacciones agrupadas por moneda.");

        Map<String, BigDecimal> totalsByCurrency = dataFormatter.calculateTotalsByCurrency(transactions);
        Table summaryTable = tableBuilder.buildCurrencySummaryTable(totalsByCurrency);
        document.add(summaryTable);

        documentBuilder.addSpacing(document);
    }

    private void addChartsSection(com.itextpdf.layout.Document document, List<Transaction> transactions) {
        document.add(new com.itextpdf.layout.element.AreaBreak());

        documentBuilder.addSectionTitle(document, "Análisis Gráfico");
        documentBuilder.addSpacing(document);

        Map<String, BigDecimal> expensesByCategory = dataFormatter.calculateExpenseTotalsByCategory(transactions);
        if (!expensesByCategory.isEmpty()) {
            documentBuilder.addSubtitle(document, "Distribución de Gastos por Categoría");
            documentBuilder.addSectionDescription(document,
                    "El siguiente gráfico circular muestra la distribución porcentual de todos los gastos " +
                    "registrados, agrupados por categoría. Esto permite identificar las áreas de mayor inversión.");

            byte[] pieChartBytes = chartGenerator.generatePieChart(
                    expensesByCategory,
                    "Gastos por Categoría"
            );
            documentBuilder.addImage(document, pieChartBytes, 5, 10);
        }

        Map<String, BigDecimal> incomesOverTime = dataFormatter.calculateIncomeTotalsOverTime(transactions);
        if (!incomesOverTime.isEmpty()) {
            documentBuilder.addSubtitle(document, "Evolución Temporal de Ingresos");
            documentBuilder.addSectionDescription(document,
                    "El siguiente gráfico de barras presenta la evolución de los ingresos a lo largo del tiempo, " +
                    "permitiendo identificar tendencias y patrones en la generación de ingresos.");

            byte[] timeChartBytes = chartGenerator.generateTimeSeriesChart(
                    incomesOverTime,
                    "Evolución de Ingresos en el Tiempo",
                    "Fecha",
                    "Monto Total"
            );
            documentBuilder.addImage(document, timeChartBytes, 5, 5);
        }
    }

    private byte[] generateEmptyReport() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PdfDocumentBuilder.DocumentContext context = documentBuilder.createDocument(outputStream)) {
            var document = context.getDocument();
            document.add(new com.itextpdf.layout.element.Paragraph(
                    "No se encontraron transacciones para los filtros seleccionados."
            ));
        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte vacío", e);
        }
        return outputStream.toByteArray();
    }
}

