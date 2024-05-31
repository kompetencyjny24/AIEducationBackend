package io.github.aieducationbackend.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import io.github.aieducationbackend.dto.TaskDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.awt.*;


@Service
public class PdfService {

    public void export(HttpServletResponse response, TaskDTO taskDTO) {
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas tworzenia pliku PDF");
        }

        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(18);
        font.setColor(Color.BLACK);

        Paragraph p = new Paragraph("Tresc zadania", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);

        font.setSize(13);
        Paragraph content = new Paragraph(taskDTO.getContent(), font);
        content.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(content);
        document.add(new Paragraph());

        font.setSize(18);
        p = new Paragraph("Podpowiedz 1", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);

        font.setSize(13);
        content = new Paragraph(taskDTO.getHints().get(0), font);
        content.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(content);
        document.add(new Paragraph());

        font.setSize(18);
        p = new Paragraph("Podpowiedz 2", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);

        font.setSize(13);
        content = new Paragraph(taskDTO.getHints().get(1), font);
        content.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(content);
        document.add(new Paragraph());

        font.setSize(18);
        p = new Paragraph("Odpowiedz", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);

        font.setSize(13);
        content = new Paragraph(taskDTO.getAnswer(), font);
        content.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(content);
        document.add(new Paragraph());

        document.close();
    }
}
