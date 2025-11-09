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
        documentBuilder.addTitle(document, "Reporte de Transacciones");
        documentBuilder.addGeneratedDate(document);
        documentBuilder.addSpacing(document);
    }

    private void addTransactionsTable(com.itextpdf.layout.Document document, List<Transaction> transactions) {
        Table table = tableBuilder.buildTransactionsTable(transactions);
        document.add(table);
        documentBuilder.addSpacing(document);
    }

    private void addSummarySection(com.itextpdf.layout.Document document, List<Transaction> transactions) {
        Map<String, BigDecimal> totalsByCurrency = dataFormatter.calculateTotalsByCurrency(transactions);

        documentBuilder.addSectionTitle(document, "Totales por moneda:");

        totalsByCurrency.forEach((currency, total) ->
            documentBuilder.addBulletPoint(document, currency + ": " + total)
        );

        documentBuilder.addSpacing(document);
    }

    private void addChartsSection(com.itextpdf.layout.Document document, List<Transaction> transactions) {
        document.add(new com.itextpdf.layout.element.AreaBreak());

        documentBuilder.addSectionTitle(document, "Análisis Gráfico");
        documentBuilder.addSpacing(document);

        Map<String, BigDecimal> expensesByCategory = dataFormatter.calculateExpenseTotalsByCategory(transactions);
        if (!expensesByCategory.isEmpty()) {
            byte[] pieChartBytes = chartGenerator.generatePieChart(
                    expensesByCategory,
                    "Gastos por Categoría"
            );
            documentBuilder.addImage(document, pieChartBytes, 5, 10);
        }

        Map<String, BigDecimal> incomesOverTime = dataFormatter.calculateIncomeTotalsOverTime(transactions);
        if (!incomesOverTime.isEmpty()) {
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

