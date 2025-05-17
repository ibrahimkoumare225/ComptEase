package fr.koumare.comptease.controllers;

import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.model.enumarated.StatusInvoice;
import fr.koumare.comptease.model.enumarated.TypeInvoice;
import fr.koumare.comptease.service.impl.ClientServiceImpl;
import fr.koumare.comptease.service.impl.FactureServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.text.DecimalFormat;
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

    @FXML
    private Label paidInvoicesLabel;

    @FXML
    private Label pendingInvoicesLabel;

    @FXML
    private Label expensesLabel;

    @FXML
    private Label turnoverLabel;

    @FXML
    private Label topClient1Label;

    @FXML
    private Label topClient2Label;

    @FXML
    private Label topClient3Label;

    private final FactureServiceImpl factureService = new FactureServiceImpl();
    private final ClientServiceImpl clientService = new ClientServiceImpl();
    private final DecimalFormat decimalFormat = new DecimalFormat("€ #,##0.00");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initialisation de DashboardController");
        configureBarChart();
        updateDashboardCards();
        updateTopClients();
    }

    private void configureBarChart() {
        try {
            List<String> months = Arrays.stream(Month.values())
                    .map(month -> month.getDisplayName(TextStyle.FULL, Locale.getDefault()))
                    .collect(Collectors.toList());
            xAxis.setCategories(FXCollections.observableArrayList(months));
            xAxis.setLabel("Mois");
            yAxis.setLabel("Montant (€)");

            List<Invoice> invoices = factureService.getAllInvoices();
            if (invoices == null || invoices.isEmpty()) {
                logger.warn("Aucune facture trouvée pour le graphique.");
                return;
            }

            int currentYear = 2025;
            invoices = invoices.stream()
                    .filter(invoice -> invoice.getDate() != null &&
                            invoice.getDate().atZone(ZoneId.systemDefault()).getYear() == currentYear)
                    .collect(Collectors.toList());

            Map<Month, Double> incomingTotals = new EnumMap<>(Month.class);
            Map<Month, Double> outgoingTotals = new EnumMap<>(Month.class);
            for (Month month : Month.values()) {
                incomingTotals.put(month, 0.0);
                outgoingTotals.put(month, 0.0);
            }

            for (Invoice invoice : invoices) {
                if (invoice.getDate() == null || invoice.getPrice() == null || invoice.getType() == null) {
                    logger.warn("Facture ID {} a une date, un prix ou un type null", invoice.getId());
                    continue;
                }
                Month month = invoice.getDate().atZone(ZoneId.systemDefault()).getMonth();
                double price = invoice.getPrice();
                String type = invoice.getType().name();
                if ("INCOMING".equals(type)) {
                    incomingTotals.merge(month, price, Double::sum);
                } else if ("OUTGOING".equals(type)) {
                    outgoingTotals.merge(month, price, Double::sum);
                }
            }

            XYChart.Series<String, Number> incomingSeries = new XYChart.Series<>();
            incomingSeries.setName("Factures Entrantes");
            for (Month month : Month.values()) {
                String monthName = month.getDisplayName(TextStyle.FULL, Locale.getDefault());
                incomingSeries.getData().add(new XYChart.Data<>(monthName, incomingTotals.get(month)));
            }

            XYChart.Series<String, Number> outgoingSeries = new XYChart.Series<>();
            outgoingSeries.setName("Factures Sortantes");
            for (Month month : Month.values()) {
                String monthName = month.getDisplayName(TextStyle.FULL, Locale.getDefault());
                outgoingSeries.getData().add(new XYChart.Data<>(monthName, outgoingTotals.get(month)));
            }

            barChart.getData().addAll(incomingSeries, outgoingSeries);

            barChart.lookupAll(".default-color0.chart-bar").forEach(node ->
                    node.setStyle("-fx-bar-fill: green;"));
            barChart.lookupAll(".default-color1.chart-bar").forEach(node ->
                    node.setStyle("-fx-bar-fill: red;"));

            logger.info("Graphique à barres configuré avec succès avec {} factures", invoices.size());
        } catch (Exception e) {
            logger.error("Erreur lors de la configuration du graphique à barres : {}", e.getMessage(), e);
        }
    }

    private void updateDashboardCards() {
        try {
            List<Invoice> invoices = factureService.getAllInvoices();
            if (invoices == null || invoices.isEmpty()) {
                logger.warn("Aucune facture trouvée pour les cartes du tableau de bord.");
                paidInvoicesLabel.setText("€ 0.00");
                pendingInvoicesLabel.setText("€ 0.00");
                expensesLabel.setText("€ 0.00");
                turnoverLabel.setText("€ 0.00");
                topClient1Label.setText("1. Aucun client");
                topClient2Label.setText("2. Aucun client");
                topClient3Label.setText("3. Aucun client");
                return;
            }

            int currentYear = 2025;
            invoices = invoices.stream()
                    .filter(invoice -> invoice.getDate() != null &&
                            invoice.getDate().atZone(ZoneId.systemDefault()).getYear() == currentYear)
                    .collect(Collectors.toList());

            double paidIncomingTotal = 0.0;
            double unpaidIncomingTotal = 0.0;
            double outgoingTotal = 0.0;

            for (Invoice invoice : invoices) {
                if (invoice.getPrice() == null || invoice.getType() == null || invoice.getStatus() == null) {
                    logger.warn("Facture ID {} a un prix, un type ou un statut null", invoice.getId());
                    continue;
                }
                double price = invoice.getPrice();
                TypeInvoice type = invoice.getType();
                StatusInvoice status = invoice.getStatus();

                if (type == TypeInvoice.INCOMING) {
                    if (status == StatusInvoice.PAID) {
                        paidIncomingTotal += price;
                    } else if (status == StatusInvoice.UNPAID) {
                        unpaidIncomingTotal += price;
                    }
                } else if (type == TypeInvoice.OUTGOING) {
                    outgoingTotal += price;
                }
            }

            double turnoverTotal = paidIncomingTotal + unpaidIncomingTotal;

            paidInvoicesLabel.setText(decimalFormat.format(paidIncomingTotal));
            pendingInvoicesLabel.setText(decimalFormat.format(unpaidIncomingTotal));
            expensesLabel.setText(decimalFormat.format(outgoingTotal));
            turnoverLabel.setText(decimalFormat.format(turnoverTotal));

            logger.info("Cartes du tableau de bord mises à jour : Chiffre d'affaires=€{}, Factures payées=€{}, Factures en attente=€{}, Dépenses=€{}",
                    turnoverTotal, paidIncomingTotal, unpaidIncomingTotal, outgoingTotal);

            updateTopClients();
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour des cartes du tableau de bord : {}", e.getMessage(), e);
            paidInvoicesLabel.setText("€ 0.00");
            pendingInvoicesLabel.setText("€ 0.00");
            expensesLabel.setText("€ 0.00");
            turnoverLabel.setText("€ 0.00");
            topClient1Label.setText("1. Aucun client");
            topClient2Label.setText("2. Aucun client");
            topClient3Label.setText("3. Aucun client");
        }
    }

    private void updateTopClients() {
        try {
            // Update balances for all clients
            List<Client> allClients = clientService.getAllClients();
            if (allClients != null && !allClients.isEmpty()) {
                logger.info("Updating balances for {} clients", allClients.size());
                for (Client client : allClients) {
                    clientService.updateClientBalance(client.getIdc());
                    logger.info("Updated balance for client ID {}: {}", client.getIdc(), client.getSolde());
                }
            } else {
                logger.warn("No clients found for balance update");
            }

            // Fetch top clients with highest balances
            List<Client> topClients = clientService.getClientsWithHighestBalance();
            logger.info("Nombre de clients récupérés par getClientsWithHighestBalance : {}", topClients.size());

            // Log client details for debugging
            if (!topClients.isEmpty()) {
                for (int i = 0; i < Math.min(topClients.size(), 3); i++) {
                    Client client = topClients.get(i);
                    logger.info("Client {}: ID={}, Nom={} {}, Solde={}",
                            i + 1, client.getIdc(), client.getFirstName(), client.getLastName(), client.getSolde());
                }
            } else {
                logger.warn("Aucun client retourné par getClientsWithHighestBalance, tentative avec getAllClients");
                // Fallback: Fetch all clients and sort manually
                topClients = clientService.getAllClients();
                if (topClients != null && !topClients.isEmpty()) {
                    topClients = topClients.stream()
                            .filter(client -> client.getSolde() != null)
                            .sorted((c1, c2) -> Double.compare(c2.getSolde() != null ? c2.getSolde() : 0.0,
                                    c1.getSolde() != null ? c1.getSolde() : 0.0))
                            .limit(3)
                            .collect(Collectors.toList());
                    logger.info("Clients récupérés via getAllClients après tri : {}", topClients.size());
                    // Log fallback client details
                    for (int i = 0; i < Math.min(topClients.size(), 3); i++) {
                        Client client = topClients.get(i);
                        logger.info("Fallback Client {}: ID={}, Nom={} {}, Solde={}",
                                i + 1, client.getIdc(), client.getFirstName(), client.getLastName(), client.getSolde());
                    }
                }
            }

            // Update labels for top 3 clients
            if (topClients.isEmpty()) {
                logger.warn("Aucun client disponible pour la section Clients principaux.");
                topClient1Label.setText("1. Aucun client");
                topClient2Label.setText("2. Aucun client");
                topClient3Label.setText("3. Aucun client");
                return;
            }

            if (topClients.size() >= 1) {
                Client client1 = topClients.get(0);
                topClient1Label.setText(String.format("1. %s %s - %s",
                        client1.getFirstName(), client1.getLastName(), decimalFormat.format(client1.getSolde())));
            } else {
                topClient1Label.setText("1. Aucun client");
            }

            if (topClients.size() >= 2) {
                Client client2 = topClients.get(1);
                topClient2Label.setText(String.format("2. %s %s - %s",
                        client2.getFirstName(), client2.getLastName(), decimalFormat.format(client2.getSolde())));
            } else {
                topClient2Label.setText("2. Aucun client");
            }

            if (topClients.size() >= 3) {
                Client client3 = topClients.get(2);
                topClient3Label.setText(String.format("3. %s %s - %s",
                        client3.getFirstName(), client3.getLastName(), decimalFormat.format(client3.getSolde())));
            } else {
                topClient3Label.setText("3. Aucun client");
            }

            logger.info("Section Clients principaux mise à jour avec {} clients", topClients.size());
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la section Clients principaux : {}", e.getMessage(), e);
            topClient1Label.setText("1. Aucun client");
            topClient2Label.setText("2. Aucun client");
            topClient3Label.setText("3. Aucun client");
        }
    }
}