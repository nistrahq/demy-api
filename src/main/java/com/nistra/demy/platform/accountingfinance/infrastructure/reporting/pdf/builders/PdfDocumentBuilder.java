package com.nistra.demy.platform.accountingfinance.infrastructure.reporting.pdf.builders;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class PdfDocumentBuilder {

    private static final String LOGO_PATH = "src/main/resources/static/images/demy-combination-mark-original.png";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

    private PdfFont createItalicFont() {
        try {
            return PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear fuente itálica", e);
        }
    }

    public DocumentContext createDocument(ByteArrayOutputStream outputStream) {
        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            return new DocumentContext(writer, pdf, document);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear documento PDF", e);
        }
    }

    public void addLogo(Document document) {
        try {
            Image logo = new Image(ImageDataFactory.create(LOGO_PATH))
                    .scaleToFit(80, 80)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(logo);
        } catch (Exception e) {
            throw new RuntimeException("Error al agregar logo al PDF", e);
        }
    }

    public void addTitle(Document document, String titleText) {
        Paragraph title = new Paragraph(titleText)
                .setFont(createBoldFont())
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(title);
    }

    public void addDescription(Document document, String descriptionText) {
        Paragraph description = new Paragraph(descriptionText)
                .setFont(createItalicFont())
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(description);
    }

    public void addSubtitle(Document document, String subtitleText) {
        Paragraph subtitle = new Paragraph(subtitleText)
                .setFont(createBoldFont())
                .setFontSize(14)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(5)
                .setMarginTop(10);
        document.add(subtitle);
    }

    public void addSectionDescription(Document document, String text) {
        Paragraph sectionDesc = new Paragraph(text)
                .setFont(createItalicFont())
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginBottom(10);
        document.add(sectionDesc);
    }

    public void addGeneratedDate(Document document) {
        Paragraph generatedAt = new Paragraph("Generado el: " +
                LocalDate.now().format(DATE_FORMATTER))
                .setFont(createRegularFont())
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(generatedAt);
    }

    public void addSpacing(Document document) {
        document.add(new Paragraph("\n"));
    }

    public void addSectionTitle(Document document, String text) {
        document.add(new Paragraph(text)
                .setFont(createBoldFont())
                .setFontSize(12)
                .setTextAlignment(TextAlignment.LEFT));
    }

    public void addBulletPoint(Document document, String text) {
        document.add(new Paragraph(" - " + text)
                .setFont(createRegularFont())
                .setFontSize(11)
                .setMarginLeft(20));
    }

    public void addImage(Document document, byte[] imageBytes, float marginTop, float marginBottom) {
        try {
            document.add(new Image(ImageDataFactory.create(imageBytes))
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setMarginTop(marginTop)
                    .setMarginBottom(marginBottom));
        } catch (Exception e) {
            throw new RuntimeException("Error al agregar imagen al PDF", e);
        }
    }

    public static class DocumentContext implements AutoCloseable {
        private final PdfWriter writer;
        private final PdfDocument pdf;
        private final Document document;

        public DocumentContext(PdfWriter writer, PdfDocument pdf, Document document) {
            this.writer = writer;
            this.pdf = pdf;
            this.document = document;
        }

        public Document getDocument() {
            return document;
        }

        @Override
        public void close() {
            try {
                if (document != null) {
                    document.close();
                }
            } catch (Exception e) {
                throw new RuntimeException("Error al cerrar documento PDF", e);
            }
        }
    }
}

