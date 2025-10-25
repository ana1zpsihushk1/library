package library.Additions;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class DocumentsS {
    public Path saveText(Path folder, String fileNameNoExt, String text, String format) throws IOException {
        Files.createDirectories(folder);
        String fmt = format.trim().toLowerCase();
        return switch (fmt) {
            case "txt" -> writeTxt(folder.resolve(fileNameNoExt + ".txt"), text);
            case "pdf" -> writePdf(folder.resolve(fileNameNoExt + ".pdf"), text);
            default -> throw new IllegalArgumentException("Wrong format: " + format);
        };
    }

    private Path writeTxt(Path path, String text) throws IOException {
        Files.writeString(path, text, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return path;
    }

    private Path writePdf(Path path, String text) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(doc, page);
            PDType0Font font = PDType0Font.load(doc, DocumentsS.class.getResourceAsStream("/font/DejaVuSans.ttf"));
            cs.beginText();
            cs.setFont(font, 11);
            cs.setLeading(14);
            cs.newLineAtOffset(50, 780);

            // простой перенос строк по \n
            for (String line : text.split("\r?\n")) {
                cs.showText(line);
                cs.newLine();
            }
            cs.endText();
            cs.close();

            doc.save(path.toFile());
        }
        return path;
    }
}
