package fr.koumare.comptease.controllers;

import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.model.RapportFinancier;
import fr.koumare.comptease.model.enumarated.TypeInvoice;
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
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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

        List<Invoice> factures = factureService.getAllInvoices();

        double revenus = factures.stream()
                .filter(f -> f.getType() == TypeInvoice.INCOMING)
                .mapToDouble(Invoice::getPrice)
                .sum();

        double depenses = factures.stream()
                .filter(f -> f.getType() == TypeInvoice.OUTGOING)
                .mapToDouble(Invoice::getPrice)
                .sum();

        double benefice = revenus - depenses;



        long totalFactures = factures.size();
        long payees = factures.stream().filter(f -> f.getStatus() == PAID).count();
        long impayees = factures.stream().filter(f -> f.getStatus() == UNPAID).count();
        double tauxPaiement = totalFactures > 0 ? (double) payees / totalFactures * 100 : 0;
        double moyenne = payees > 0 ? revenus / payees : 0;

        double objectif = 50000.0;
        double ratioObjectif = Math.min(revenus / objectif, 1.0);

        revenuLabel.setText(String.format("%.2f €", revenus));
        depenseLabel.setText(String.format("%.2f €", depenses));
        beneficeLabel.setText(String.format("%.2f €", benefice));

        factureTotalLabel.setText(String.valueOf(totalFactures));
        facturePayeeLabel.setText(String.valueOf(payees));
        factureImpayeeLabel.setText(String.valueOf(impayees));
        tauxPaiementLabel.setText(String.format("%.0f%%", tauxPaiement));
        moyenneFactureLabel.setText(String.format("%.2f €", moyenne));

        objectifLabel.setText(String.format("Objectif : %.0f €", objectif));
        objectifProgress.setProgress(ratioObjectif);

        updateBarChart(factures);
    }


    private void updateBarChart(List<Invoice> factures) {
        Map<Integer, Double> beneficeParMois = factures.stream()
                .collect(Collectors.groupingBy(
                        f -> f.getDate().atZone(ZoneId.systemDefault()).getMonthValue(),
                        Collectors.summingDouble(f -> f.getStatus() == PAID ? f.getPrice() : -f.getPrice())
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        beneficeParMois.forEach((mois, montant) -> {
            String moisNom = java.time.Month.of(mois).name().substring(0, 3); // ex: JAN, FEB
            series.getData().add(new XYChart.Data<>(moisNom, montant));
        });

        barChart.getData().clear();
        barChart.getData().add(series);
        barChart.setLegendVisible(false);
    }


    @FXML
    public void handleExportPdf() {
        rapportService.exporterRapportPdf();
    }

}
