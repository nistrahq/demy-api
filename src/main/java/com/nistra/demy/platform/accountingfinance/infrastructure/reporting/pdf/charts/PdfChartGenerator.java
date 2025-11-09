package com.nistra.demy.platform.accountingfinance.infrastructure.reporting.pdf.charts;

import org.knowm.xchart.*;
import org.knowm.xchart.style.CategoryStyler;
import org.knowm.xchart.style.PieStyler;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PdfChartGenerator {

    private static final java.awt.Color[] PIE_CHART_COLORS = {
            new java.awt.Color(93, 173, 226),
            new java.awt.Color(241, 148, 138),
            new java.awt.Color(130, 224, 170),
            new java.awt.Color(247, 220, 111),
            new java.awt.Color(165, 105, 189)
    };

    private static final java.awt.Color LINE_CHART_COLOR = new java.awt.Color(52, 152, 219);

    public byte[] generatePieChart(Map<String, BigDecimal> data, String title) {
        PieChart pieChart = new PieChartBuilder()
                .width(400)
                .height(250)
                .title(title)
                .build();

        configurePieChartStyle(pieChart.getStyler());
        data.forEach(pieChart::addSeries);

        return convertPieChartToBytes(pieChart);
    }

    public byte[] generateTimeSeriesChart(Map<String, BigDecimal> data, String title, String xAxisTitle, String yAxisTitle) {
        List<String> sortedDates = data.keySet().stream().sorted().collect(Collectors.toList());
        List<BigDecimal> values = sortedDates.stream().map(data::get).collect(Collectors.toList());

        CategoryChart timeChart = new CategoryChartBuilder()
                .width(500)
                .height(250)
                .title(title)
                .xAxisTitle(xAxisTitle)
                .yAxisTitle(yAxisTitle)
                .build();

        configureCategoryChartStyle(timeChart.getStyler());
        timeChart.addSeries("Transacciones", sortedDates, values);

        return convertCategoryChartToBytes(timeChart);
    }

    private void configurePieChartStyle(PieStyler style) {
        style.setPlotBackgroundColor(java.awt.Color.WHITE);
        style.setChartBackgroundColor(java.awt.Color.WHITE);
        style.setLegendBackgroundColor(java.awt.Color.WHITE);
        style.setChartFontColor(java.awt.Color.DARK_GRAY);
        style.setSeriesColors(PIE_CHART_COLORS);
        style.setLegendVisible(true);
        style.setPlotBorderVisible(false);
        style.setChartTitleFont(new java.awt.Font("Helvetica", java.awt.Font.BOLD, 14));
        style.setLegendFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 10));
    }

    private void configureCategoryChartStyle(CategoryStyler style) {
        style.setPlotBackgroundColor(java.awt.Color.WHITE);
        style.setChartBackgroundColor(java.awt.Color.WHITE);
        style.setChartFontColor(java.awt.Color.DARK_GRAY);
        style.setLegendVisible(false);
        style.setLabelsVisible(true);
        style.setPlotGridVerticalLinesVisible(false);
        style.setPlotGridHorizontalLinesVisible(true);
        style.setPlotBorderVisible(false);
        style.setAxisTickLabelsFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 9));
        style.setSeriesColors(new java.awt.Color[]{LINE_CHART_COLOR});
    }

    private byte[] convertPieChartToBytes(PieChart chart) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BitmapEncoder.saveBitmap(chart, outputStream, BitmapEncoder.BitmapFormat.PNG);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir gráfico circular a bytes", e);
        }
    }

    private byte[] convertCategoryChartToBytes(CategoryChart chart) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BitmapEncoder.saveBitmap(chart, outputStream, BitmapEncoder.BitmapFormat.PNG);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir gráfico de categorías a bytes", e);
        }
    }
}

