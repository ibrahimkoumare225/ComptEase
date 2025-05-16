package fr.koumare.comptease.controllers;

import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPTable;
import fr.koumare.comptease.model.Document;
import fr.koumare.comptease.model.Facture;
import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.model.RapportFinancier;
import fr.koumare.comptease.service.impl.FactureServiceImpl;
import fr.koumare.comptease.service.impl.RapportFinancierServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static fr.koumare.comptease.model.enumarated.StatusInvoice.PAID;
import static fr.koumare.comptease.model.enumarated.StatusInvoice.UNPAID;

public class RapportFinancierController extends BaseController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(RapportFinancierController.class);

    public Label revenuLabel;
    public Label depenseLabel;
    public Label beneficeLabel;
    public Label factureTotalLabel;
    public Label facturePayeeLabel;
    public Label factureImpayeeLabel;
    public Label tauxPaiementLabel;
    public Label moyenneFactureLabel;
    public ProgressBar objectifProgress;
    public Label objectifLabel;

    public BarChart<String, Number> barChart;
    public CategoryAxis xAxis;
    public NumberAxis yAxis;

    @FXML
    private BorderPane chartContainer;

    private final RapportFinancierServiceImpl rapportService = new RapportFinancierServiceImpl();
    private final FactureServiceImpl factureService = new FactureServiceImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initialisation de RapportFinancierController");
        List<RapportFinancier> rapports = rapportService.findAll();
        RapportFinancier dernier = rapports.isEmpty() ? null : rapports.get(rapports.size() - 1);

        double totalRevenus = rapports.stream().mapToDouble(RapportFinancier::getIncomeTotal).sum();
        double totalDepenses = rapports.stream().mapToDouble(RapportFinancier::getExpenseTotal).sum();
        double totalBenefices = rapports.stream().mapToDouble(RapportFinancier::getBenefice).sum();

        List<Invoice> factures = factureService.getAllFactures();

        long totalFactures = factures.size();
        long payees = factures.stream().filter(f -> f.getStatus() == PAID).count();
        long impayees = factures.stream().filter(f -> f.getStatus() == UNPAID).count();
        double tauxPaiement = totalFactures > 0 ? (double) payees / totalFactures * 100 : 0;
        double moyenne = payees > 0 ? totalRevenus / payees : 0;

        double objectif = 500000.0;
        double ratioObjectif = Math.min(totalRevenus / objectif, 1.0);

        revenuLabel.setText(String.format("%.2f €", totalRevenus));
        depenseLabel.setText(String.format("%.2f €", totalDepenses));
        beneficeLabel.setText(String.format("%.2f €", totalBenefices));

        factureTotalLabel.setText(String.valueOf(totalFactures));
        facturePayeeLabel.setText(String.valueOf(payees));
        factureImpayeeLabel.setText(String.valueOf(impayees));
        tauxPaiementLabel.setText(String.format("%.0f%%", tauxPaiement));
        moyenneFactureLabel.setText(String.format("%.2f €", moyenne));

        objectifLabel.setText(String.format("Objectif : %.0f €", objectif));
        objectifProgress.setProgress(ratioObjectif);

        updateBarChart();
    }

    private void updateBarChart() {
        var map = rapportService.getBeneficesParMois();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        map.forEach((mois, valeur) -> series.getData().add(new XYChart.Data<>(mois, valeur)));
        barChart.getData().clear();
        barChart.getData().add(series);
        barChart.setLegendVisible(false);
    }

    @FXML
    public void handleExportPdf() {
        rapportService.exporterRapportPdf();
    }

}
