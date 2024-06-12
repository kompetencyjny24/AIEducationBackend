package io.github.aieducationbackend.service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import io.github.aieducationbackend.dto.SubtaskDTO;
import io.github.aieducationbackend.dto.TaskDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Service
public class PdfService {

    public void export(HttpServletResponse response, TaskDTO taskDTO, boolean onlyContent) {
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas tworzenia pliku PDF");
        }

        document.open();

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell("Przedmiot: " + taskDTO.getSubject() +
                "\nDział: " + taskDTO.getSubjectSection() +
                "\nHobby: " + taskDTO.getHobby());
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell("Data: " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        document.add(table);

        for (int i = 0; i < taskDTO.getGeneratedTasks().size(); i++) {
            SubtaskDTO subtaskDTO = taskDTO.getGeneratedTasks().get(i);

            try {
                addSubtaskToDocument(document, subtaskDTO, onlyContent, i + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        document.close();
    }

    private void addSubtaskToDocument(Document document, SubtaskDTO subtaskDTO, boolean onlyContent, int counter) throws IOException {
        BaseFont bf = BaseFont.createFont("Roboto-Light.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        BaseFont bfBold = BaseFont.createFont("Roboto-Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        Font fontBold = new Font(bfBold);
        fontBold.setSize(21);
        fontBold.setColor(Color.BLACK);

        Font mediumFont = new Font(bf);
        mediumFont.setSize(14);
        mediumFont.setColor(Color.BLACK);

        Font smallFont = new Font(bf);
        smallFont.setSize(12);
        smallFont.setColor(Color.BLACK);

        Paragraph p = new Paragraph("Zadanie " + counter, fontBold);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);

        Paragraph content = new Paragraph(subtaskDTO.getContent() + "\n\n", mediumFont);
        content.setAlignment(Element.ALIGN_LEFT);
        document.add(content);

        if (!onlyContent) {
            p = new Paragraph("Podpowiedź 1: \n", smallFont);
            p.add(new Chunk(subtaskDTO.getHints().get(0) + "\n", smallFont));
            p.setAlignment(Paragraph.ALIGN_LEFT);
            document.add(p);

            p = new Paragraph("Podpowiedź 2: \n", smallFont);
            p.add(new Chunk(subtaskDTO.getHints().get(1) + "\n\n", smallFont));
            p.setAlignment(Paragraph.ALIGN_LEFT);
            document.add(p);

            p = new Paragraph("Odpowiedź: \n", mediumFont);
            p.add(new Chunk(subtaskDTO.getAnswer() + "\n\n\n", mediumFont));
            p.setAlignment(Paragraph.ALIGN_LEFT);
            document.add(p);
        }
    }
}
