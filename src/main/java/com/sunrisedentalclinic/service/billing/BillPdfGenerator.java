package com.sunrisedentalclinic.service.billing;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.sunrisedentalclinic.config.ClinicSettings;
import com.sunrisedentalclinic.dto.BillResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

/** Renders a {@link BillResponse} as a printable PDF receipt. */
@Component
public class BillPdfGenerator {

    private static final DateTimeFormatter ISSUED_AT_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    public byte[] generate(BillResponse bill) {
        Document document = new Document(PageSize.A5);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);

            document.add(new Paragraph(ClinicSettings.getInstance().getClinicName(), titleFont));
            document.add(new Paragraph("Bill Receipt", subtitleFont));
            document.add(Chunk.NEWLINE);

            addRow(document, labelFont, valueFont, "Appointment number", bill.appointmentNumber());
            addRow(document, labelFont, valueFont, "Patient name", bill.patientName());
            addRow(document, labelFont, valueFont, "Treatment", bill.treatmentName());
            addRow(document, labelFont, valueFont, "Consultation fee", format(bill.consultationFee()));
            addRow(document, labelFont, valueFont, "Treatment cost", format(bill.treatmentCost()));
            addRow(document, labelFont, valueFont, "Discount", format(bill.discountAmount()));
            addRow(document, labelFont, valueFont, "Issued at", bill.issuedAt().format(ISSUED_AT_FORMAT));

            document.add(Chunk.NEWLINE);
            Paragraph total = new Paragraph("Total: " + format(bill.totalCost()), totalFont);
            document.add(total);

            document.close();
        } catch (DocumentException ex) {
            throw new IllegalStateException("Failed to generate bill PDF for " + bill.appointmentNumber(), ex);
        }
        return out.toByteArray();
    }

    private void addRow(Document document, Font labelFont, Font valueFont, String label, String value)
            throws DocumentException {
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Chunk(label + ": ", labelFont));
        paragraph.add(new Chunk(value, valueFont));
        paragraph.setSpacingAfter(6f);
        document.add(paragraph);
    }

    private String format(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
