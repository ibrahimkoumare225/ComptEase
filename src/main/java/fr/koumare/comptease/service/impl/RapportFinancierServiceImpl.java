package fr.koumare.comptease.service.impl;

import fr.koumare.comptease.dao.RapportFinancierDao;
import fr.koumare.comptease.model.RapportFinancier;
import fr.koumare.comptease.model.enumarated.StatusInvoice;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RapportFinancierServiceImpl {
    private final RapportFinancierDao rapportDao = new RapportFinancierDao();

    public List<RapportFinancier> findAll() {
        return rapportDao.getAllRapportFinanciers();
    }

    public Map<String, Double> getBeneficesParMois() {
        List<Object[]> result = rapportDao.getMontantsParMois();
        Map<String, Double> map = new LinkedHashMap<>();

        String[] mois = {"Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Aoû", "Sep", "Oct", "Nov", "Déc"};
        for (int i = 0; i < 12; i++) map.put(mois[i], 0.0);

        for (Object[] row : result) {
            int moisIndex = ((Integer) row[0]) - 1;
            double benefice = (Double) row[3];
            map.put(mois[moisIndex], benefice);
        }

        return map;
    }

    public void exporterRapportPdf() {
        List<RapportFinancier> rapports = findAll();
        Map<String, Double> beneficesParMois = getBeneficesParMois();

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("rapport_financier.pdf"));
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);

            document.add(new Paragraph("Rapport Financier Global", titleFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            table.addCell("Mois");
            table.addCell("Revenus (€)");
            table.addCell("Dépenses (€)");
            table.addCell("Bénéfice (€)");

            for (RapportFinancier rapport : rapports) {
                table.addCell(String.valueOf(rapport.getMonth()));
                table.addCell(String.format("%.2f", rapport.getIncomeTotal()));
                table.addCell(String.format("%.2f", rapport.getExpenseTotal()));
                table.addCell(String.format("%.2f", rapport.getBenefice()));
            }

            document.add(table);

            document.add(new Paragraph("Évolution mensuelle des bénéfices", sectionFont));
            document.add(new Paragraph(" "));

            PdfPTable chartTable = new PdfPTable(2);
            chartTable.setWidthPercentage(50);
            chartTable.setSpacingBefore(10f);

            chartTable.addCell("Mois");
            chartTable.addCell("Bénéfice (€)");

            for (Map.Entry<String, Double> entry : beneficesParMois.entrySet()) {
                chartTable.addCell(entry.getKey());
                chartTable.addCell(String.format("%.2f", entry.getValue()));
            }

            document.add(chartTable);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}
