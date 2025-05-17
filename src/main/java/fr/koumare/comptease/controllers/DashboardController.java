package fr.koumare.comptease.controllers;

import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.service.impl.FactureServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardController extends BaseController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private final FactureServiceImpl factureService = new FactureServiceImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initialisation de DashboardController");
        configureBarChart();
    }

    private void configureBarChart() {
        try {
            // Set the months on the CategoryAxis
            List<String> months = Arrays.stream(Month.values())
                    .map(month -> month.getDisplayName(TextStyle.FULL, Locale.getDefault()))
                    .collect(Collectors.toList());
            xAxis.setCategories(FXCollections.observableArrayList(months));
            xAxis.setLabel("Mois");
            yAxis.setLabel("Montant (€)");

            // Fetch all invoices
            List<Invoice> invoices = factureService.getAllInvoices();
            if (invoices == null || invoices.isEmpty()) {
                logger.warn("Aucune facture trouvée pour le graphique.");
                return;
            }

            // Filter invoices for the current year (2025)
            int currentYear = 2025;
            invoices = invoices.stream()
                    .filter(invoice -> invoice.getDate() != null &&
                            invoice.getDate().atZone(ZoneId.systemDefault()).getYear() == currentYear)
                    .collect(Collectors.toList());

            // Initialize data structures for incoming and outgoing invoices
            Map<Month, Double> incomingTotals = new EnumMap<>(Month.class);
            Map<Month, Double> outgoingTotals = new EnumMap<>(Month.class);
            for (Month month : Month.values()) {
                incomingTotals.put(month, 0.0);
                outgoingTotals.put(month, 0.0);
            }

            // Aggregate invoice totals by month
            for (Invoice invoice : invoices) {
                if (invoice.getDate() == null || invoice.getPrice() == null) {
                    logger.warn("Facture ID {} a une date ou un prix null", invoice.getId());
                    continue;
                }
                Month month = invoice.getDate().atZone(ZoneId.systemDefault()).getMonth();
                double price = invoice.getPrice();
                String type = invoice.getType().name(); // Convert enum to String
                if ("INCOMING".equals(type)) {
                    incomingTotals.merge(month, price, Double::sum);
                } else if ("OUTGOING".equals(type)) {
                    outgoingTotals.merge(month, price, Double::sum);
                }
            }

            // Create series for incoming invoices (green)
            XYChart.Series<String, Number> incomingSeries = new XYChart.Series<>();
            incomingSeries.setName("Factures Entrantes");
            for (Month month : Month.values()) {
                String monthName = month.getDisplayName(TextStyle.FULL, Locale.getDefault());
                incomingSeries.getData().add(new XYChart.Data<>(monthName, incomingTotals.get(month)));
            }

            // Create series for outgoing invoices (red)
            XYChart.Series<String, Number> outgoingSeries = new XYChart.Series<>();
            outgoingSeries.setName("Factures Sortantes");
            for (Month month : Month.values()) {
                String monthName = month.getDisplayName(TextStyle.FULL, Locale.getDefault());
                outgoingSeries.getData().add(new XYChart.Data<>(monthName, outgoingTotals.get(month)));
            }

            // Add series to the chart
            barChart.getData().addAll(incomingSeries, outgoingSeries);

            // Apply CSS styling to set bar colors
            barChart.lookupAll(".default-color0.chart-bar").forEach(node ->
                    node.setStyle("-fx-bar-fill: green;"));
            barChart.lookupAll(".default-color1.chart-bar").forEach(node ->
                    node.setStyle("-fx-bar-fill: red;"));

            logger.info("Graphique à barres configuré avec succès avec {} factures", invoices.size());
        } catch (Exception e) {
            logger.error("Erreur lors de la configuration du graphique à barres : {}", e.getMessage(), e);
        }
    }
}