package utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import exceptions.ExportException;
import models.member;
import java.io.FileOutputStream;

public class PDFGenerator {
    private static final String destPath = "card_ph/";
    public static void generateMemberCard(member member) throws ExportException {
        Document document = new Document(PageSize.A6);
        
        try {
            PdfWriter.getInstance(document, new FileOutputStream(destPath + "carte_" + member.getNom()+ "_" + member.getPrenom() + ".pdf"));
            document.open();

            // fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.RED);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);

            // entete
            Paragraph title = new Paragraph("POWERHOUSE CLUB", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph("________________________________", labelFont));
            document.add(new Paragraph(" "));

            // les informations du membre
            document.add(createLine("ID MEMBRE : ", String.valueOf(member.getId()), labelFont, valueFont));
            document.add(createLine("NOM : ", member.getNom().toUpperCase(), labelFont, valueFont));
            document.add(createLine("PRÉNOM : ", member.getPrenom(), labelFont, valueFont));
            document.add(createLine("EMAIL : ", member.getEmail(), labelFont, valueFont));
            document.add(createLine("TÉL : ", member.getTelephone(), labelFont, valueFont));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("________________________________", labelFont));
            
            // footer
            Paragraph footer = new Paragraph("Document officiel - Accès Réservé", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            
        } catch (Exception e) {
            throw new ExportException("Impossible de créer la carte pour " + member.getNom(), e);
        }
    }

    private static Paragraph createLine(String label, String value, Font lFont, Font vFont) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label, lFont));
        p.add(new Chunk(value != null ? value : "N/A", vFont));
        return p;
    }
}