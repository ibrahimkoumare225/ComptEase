package fr.koumare.comptease.controllers;

import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.model.ObligationFiscale;
import fr.koumare.comptease.model.enumarated.StatusInvoice;
import fr.koumare.comptease.model.enumarated.TypeInvoice;
import fr.koumare.comptease.service.impl.ClientServiceImpl;
import fr.koumare.comptease.service.impl.FactureServiceImpl;
import fr.koumare.comptease.dao.ObligationFiscaleDao;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
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

    @FXML
    private Label tax1Label;

    @FXML
    private Label tax2Label;

    @FXML
    private Label tax3Label;

    @FXML
    private Label totalFiscLabel;

    @FXML
    private Label turnoverProfitLabel;

    @FXML
    private Label expensesProfitLabel;

    @FXML
    private Label taxProfitLabel;

    @FXML
    private Label restanteLabel;

    @FXML
    private DatePicker barChartDatePicker;

    @FXML
    private DatePicker clientsDatePicker;

    @FXML
    private DatePicker fiscaliteDatePicker;

    @FXML
    private DatePicker profitDatePicker;

    private final FactureServiceImpl factureService = new FactureServiceImpl();
    private final ClientServiceImpl clientService = new ClientServiceImpl();
    private final ObligationFiscaleDao obligationFiscaleDao = new ObligationFiscaleDao();
    private final DecimalFormat decimalFormat = new DecimalFormat("€ #,##0.00");

    private double turnoverTotal = 0.0;
    private double outgoingTotal = 0.0;
    private double totalFisc = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initialisation de DashboardController");
        setupDatePickers();
        configureBarChart();
        updateDashboardCards();
        updateTopClients();
        updateFiscaliteSection();
        updateProfitSection();
    }

    private void setupDatePickers() {
        // Set default to current year for bar chart
        barChartDatePicker.setValue(LocalDate.now());
        barChartDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            logger.info("Bar chart year changed to: {}", newValue != null ? newValue.getYear() : "null");
            configureBarChart();
            updateDashboardCards();
            updateProfitSection();
        });

        // Set default to current month for clients
        clientsDatePicker.setValue(LocalDate.now());
        clientsDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            logger.info("Clients month changed to: {}", newValue != null ? newValue.getMonthValue() + "/" + newValue.getYear() : "null");
            updateTopClients();
        });

        // Set default to current year for fiscalite
        fiscaliteDatePicker.setValue(LocalDate.now());
        fiscaliteDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            logger.info("Fiscalité year changed to: {}", newValue != null ? newValue.getYear() : "null");
            updateFiscaliteSection();
            updateProfitSection();
        });

        // Set default to current year for profit
        profitDatePicker.setValue(LocalDate.now());
        profitDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            logger.info("Profit year changed to: {}", newValue != null ? newValue.getYear() : "null");
            updateProfitSection();
        });
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
                barChart.getData().clear();
                return;
            }

            // Get selected year from DatePicker, default to current year
            int selectedYear = barChartDatePicker.getValue() != null
                    ? barChartDatePicker.getValue().getYear()
                    : LocalDate.now().getYear();
            logger.info("Configuring bar chart for year: {}", selectedYear);

            invoices = invoices.stream()
                    .filter(invoice -> invoice.getDate() != null &&
                            invoice.getDate().atZone(ZoneId.systemDefault()).getYear() == selectedYear)
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

            barChart.getData().clear();
            barChart.getData().addAll(incomingSeries, outgoingSeries);

            // Clear all default and inherited styles
            barChart.setStyle("-fx-bar-fill: transparent; -fx-background-color: transparent;");

            // Apply colors after chart rendering
            Platform.runLater(() -> {
                // Style bars (already correct: green for incoming, red for outgoing)
                logger.info("Applying green (#00FF00) to Factures Entrantes bars");
                incomingSeries.getData().forEach(data -> {
                    if (data.getNode() != null) {
                        data.getNode().setStyle("-fx-bar-fill: #00FF00 !important;");
                        logger.debug("Styled bar for month {} (Entrantes) with green", data.getXValue());
                    } else {
                        logger.warn("Node null for incoming data: {}", data.getXValue());
                    }
                });

                logger.info("Applying red (#FF0000) to Factures Sortantes bars");
                outgoingSeries.getData().forEach(data -> {
                    if (data.getNode() != null) {
                        data.getNode().setStyle("-fx-bar-fill: #FF0000 !important;");
                        logger.debug("Styled bar for month {} (Sortantes) with red", data.getXValue());
                    } else {
                        logger.warn("Node null for outgoing data: {}", data.getXValue());
                    }
                });

                // Style legend indicators
                logger.info("Styling legend indicators");
                barChart.lookupAll(".chart-legend-item").forEach(node -> {
                    Node labelNode = node.lookup(".text");
                    if (labelNode != null) {
                        String seriesName = labelNode.toString().toLowerCase();
                        Node symbol = node.lookup(".chart-legend-item-symbol");
                        if (symbol != null) {
                            if (seriesName.contains("entrantes")) {
                                symbol.setStyle("-fx-background-color: #00FF00 !important;");
                                logger.debug("Styled legend symbol for Factures Entrantes with green");
                            } else if (seriesName.contains("sortantes")) {
                                symbol.setStyle("-fx-background-color: #FF0000 !important;");
                                logger.debug("Styled legend symbol for Factures Sortantes with red");
                            } else {
                                logger.warn("Unknown series in legend: {}", seriesName);
                            }
                        } else {
                            logger.warn("Legend symbol not found for series: {}", seriesName);
                        }
                    } else {
                        logger.warn("Label not found in legend item: {}", node);
                    }
                });
            });

            logger.info("Graphique à barres configuré avec succès avec {} factures pour l'année {}", invoices.size(), selectedYear);
        } catch (Exception e) {
            logger.error("Erreur lors de la configuration du graphique à barres : {}", e.getMessage(), e);
            barChart.getData().clear();
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
                turnoverTotal = 0.0;
                outgoingTotal = 0.0;
                return;
            }

            // Filter by selected year
            int selectedYear = barChartDatePicker.getValue() != null
                    ? barChartDatePicker.getValue().getYear()
                    : LocalDate.now().getYear();
            invoices = invoices.stream()
                    .filter(invoice -> invoice.getDate() != null &&
                            invoice.getDate().atZone(ZoneId.systemDefault()).getYear() == selectedYear)
                    .collect(Collectors.toList());

            double paidIncomingTotal = 0.0;
            double unpaidIncomingTotal = 0.0;
            outgoingTotal = 0.0;

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

            turnoverTotal = paidIncomingTotal + unpaidIncomingTotal;

            paidInvoicesLabel.setText(decimalFormat.format(paidIncomingTotal));
            pendingInvoicesLabel.setText(decimalFormat.format(unpaidIncomingTotal));
            expensesLabel.setText(decimalFormat.format(outgoingTotal));
            turnoverLabel.setText(decimalFormat.format(turnoverTotal));

            logger.info("Cartes du tableau de bord mises à jour pour l'année {} : Chiffre d'affaires=€{}, Factures payées=€{}, Factures en attente=€{}, Dépenses=€{}",
                    selectedYear, turnoverTotal, paidIncomingTotal, unpaidIncomingTotal, outgoingTotal);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour des cartes du tableau de bord : {}", e.getMessage(), e);
            paidInvoicesLabel.setText("€ 0.00");
            pendingInvoicesLabel.setText("€ 0.00");
            expensesLabel.setText("€ 0.00");
            turnoverLabel.setText("€ 0.00");
            topClient1Label.setText("1. Aucun client");
            topClient2Label.setText("2. Aucun client");
            topClient3Label.setText("3. Aucun client");
            turnoverTotal = 0.0;
            outgoingTotal = 0.0;
        }
    }

    private void updateTopClients() {
        try {
            // Get selected year and month from DatePicker, default to current
            LocalDate selectedDate = clientsDatePicker.getValue() != null
                    ? clientsDatePicker.getValue()
                    : LocalDate.now();
            int selectedYear = selectedDate.getYear();
            int selectedMonth = selectedDate.getMonthValue();
            logger.info("Updating top clients for year {} and month {}", selectedYear, selectedMonth);

            // Fetch top clients for the selected month
            List<Client> topClients = clientService.getClientsWithHighestBalanceByMonth(selectedYear, selectedMonth);
            logger.info("Nombre de clients récupérés pour {} : {}", selectedYear + "-" + selectedMonth, topClients.size());

            // Log client details for debugging
            if (!topClients.isEmpty()) {
                for (int i = 0; i < Math.min(topClients.size(), 3); i++) {
                    Client client = topClients.get(i);
                    double balance = clientService.getClientInvoiceSumByMonth(client.getIdc(), selectedYear, selectedMonth);
                    logger.info("Client {}: ID={}, Nom={} {}, Solde={}",
                            i + 1, client.getIdc(), client.getFirstName(), client.getLastName(), balance);
                }
            } else {
                logger.warn("Aucun client retourné pour {} , tentative avec tous les clients", selectedYear + "-" + selectedMonth);
                // Fallback: Fetch all clients and filter invoices for the month
                List<Client> allClients = clientService.getAllClients();
                if (allClients != null && !allClients.isEmpty()) {
                    topClients = allClients.stream()
                            .map(client -> {
                                double balance = clientService.getClientInvoiceSumByMonth(client.getIdc(), selectedYear, selectedMonth);
                                client.setSolde(balance);
                                return client;
                            })
                            .filter(client -> client.getSolde() != null && client.getSolde() > 0)
                            .sorted((c1, c2) -> Double.compare(c2.getSolde(), c1.getSolde()))
                            .limit(3)
                            .collect(Collectors.toList());
                    logger.info("Clients récupérés via getAllClients après tri pour {} : {}", selectedYear + "-" + selectedMonth, topClients.size());
                    for (int i = 0; i < Math.min(topClients.size(), 3); i++) {
                        Client client = topClients.get(i);
                        logger.info("Fallback Client {}: ID={}, Nom={} {}, Solde={}",
                                i + 1, client.getIdc(), client.getFirstName(), client.getLastName(), client.getSolde());
                    }
                }
            }

            // Update labels for top 3 clients
            if (topClients.isEmpty()) {
                logger.warn("Aucun client disponible pour la section Clients principaux pour {}", selectedYear + "-" + selectedMonth);
                topClient1Label.setText("1. Aucun client");
                topClient2Label.setText("2. Aucun client");
                topClient3Label.setText("3. Aucun client");
                return;
            }

            if (topClients.size() >= 1) {
                Client client1 = topClients.get(0);
                double balance1 = clientService.getClientInvoiceSumByMonth(client1.getIdc(), selectedYear, selectedMonth);
                topClient1Label.setText(String.format("1. %s %s - %s",
                        client1.getFirstName(), client1.getLastName(), decimalFormat.format(balance1)));
            } else {
                topClient1Label.setText("1. Aucun client");
            }

            if (topClients.size() >= 2) {
                Client client2 = topClients.get(1);
                double balance2 = clientService.getClientInvoiceSumByMonth(client2.getIdc(), selectedYear, selectedMonth);
                topClient2Label.setText(String.format("2. %s %s - %s",
                        client2.getFirstName(), client2.getLastName(), decimalFormat.format(balance2)));
            } else {
                topClient2Label.setText("2. Aucun client");
            }

            if (topClients.size() >= 3) {
                Client client3 = topClients.get(2);
                double balance3 = clientService.getClientInvoiceSumByMonth(client3.getIdc(), selectedYear, selectedMonth);
                topClient3Label.setText(String.format("3. %s %s - %s",
                        client3.getFirstName(), client3.getLastName(), decimalFormat.format(balance3)));
            } else {
                topClient3Label.setText("3. Aucun client");
            }

            logger.info("Section Clients principaux mise à jour avec {} clients pour {}", topClients.size(), selectedYear + "-" + selectedMonth);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la section Clients principaux : {}", e.getMessage(), e);
            topClient1Label.setText("1. Aucun client");
            topClient2Label.setText("2. Aucun client");
            topClient3Label.setText("3. Aucun client");
        }
    }

    private void updateFiscaliteSection() {
        try {
            List<ObligationFiscale> obligations = obligationFiscaleDao.getAllObligationFiscale();
            if (obligations == null || obligations.isEmpty()) {
                logger.warn("Aucune obligation fiscale trouvée.");
                tax1Label.setText("1. Aucun impôt");
                tax2Label.setText("2. Aucun impôt");
                tax3Label.setText("3. Aucun impôt");
                totalFiscLabel.setText("Total: € 0.00");
                totalFisc = 0.0;
                return;
            }

            int selectedYear = fiscaliteDatePicker.getValue() != null
                    ? fiscaliteDatePicker.getValue().getYear()
                    : LocalDate.now().getYear();
            logger.info("Updating fiscalité section for year: {}", selectedYear);

            obligations = obligations.stream()
                    .filter(ob -> ob.getDateEchance() != null &&
                            ob.getDateEchance().atZone(ZoneId.systemDefault()).getYear() == selectedYear)
                    .collect(Collectors.toList());

            totalFisc = obligations.stream()
                    .mapToDouble(ObligationFiscale::getAmount)
                    .sum();

            // Update labels for up to 3 tax obligations
            if (obligations.isEmpty()) {
                tax1Label.setText("1. Aucun impôt");
                tax2Label.setText("2. Aucun impôt");
                tax3Label.setText("3. Aucun impôt");
            } else {
                if (obligations.size() >= 1) {
                    ObligationFiscale tax1 = obligations.get(0);
                    tax1Label.setText(String.format("1. %s - %s", tax1.getTypeImpot(), decimalFormat.format(tax1.getAmount())));
                } else {
                    tax1Label.setText("1. Aucun impôt");
                }

                if (obligations.size() >= 2) {
                    ObligationFiscale tax2 = obligations.get(1);
                    tax2Label.setText(String.format("2. %s - %s", tax2.getTypeImpot(), decimalFormat.format(tax2.getAmount())));
                } else {
                    tax2Label.setText("2. Aucun impôt");
                }

                if (obligations.size() >= 3) {
                    ObligationFiscale tax3 = obligations.get(2);
                    tax3Label.setText(String.format("3. %s - %s", tax3.getTypeImpot(), decimalFormat.format(tax3.getAmount())));
                } else {
                    tax3Label.setText("3. Aucun impôt");
                }
            }

            totalFiscLabel.setText(String.format("Total: %s", decimalFormat.format(totalFisc)));

            logger.info("Fiscalité section updated with {} obligations for year {}. Total: €{}", obligations.size(), selectedYear, totalFisc);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la section fiscalité : {}", e.getMessage(), e);
            tax1Label.setText("1. Aucun impôt");
            tax2Label.setText("2. Aucun impôt");
            tax3Label.setText("3. Aucun impôt");
            totalFiscLabel.setText("Total: € 0.00");
            totalFisc = 0.0;
        }
    }

    private void updateProfitSection() {
        try {
            int profitYear = profitDatePicker.getValue() != null
                    ? profitDatePicker.getValue().getYear()
                    : LocalDate.now().getYear();
            logger.info("Updating profit section for year: {}", profitYear);

            // Check if the years match for turnover, expenses, and taxes
            int barChartYear = barChartDatePicker.getValue() != null
                    ? barChartDatePicker.getValue().getYear()
                    : LocalDate.now().getYear();
            int fiscaliteYear = fiscaliteDatePicker.getValue() != null
                    ? fiscaliteDatePicker.getValue().getYear()
                    : LocalDate.now().getYear();

            // If the years don't match, recompute the values
            if (profitYear != barChartYear) {
                List<Invoice> invoices = factureService.getAllInvoices();
                if (invoices != null && !invoices.isEmpty()) {
                    invoices = invoices.stream()
                            .filter(invoice -> invoice.getDate() != null &&
                                    invoice.getDate().atZone(ZoneId.systemDefault()).getYear() == profitYear)
                            .collect(Collectors.toList());

                    double paidIncomingTotal = 0.0;
                    double unpaidIncomingTotal = 0.0;
                    outgoingTotal = 0.0;

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
                    turnoverTotal = paidIncomingTotal + unpaidIncomingTotal;
                } else {
                    turnoverTotal = 0.0;
                    outgoingTotal = 0.0;
                }
            }

            if (profitYear != fiscaliteYear) {
                List<ObligationFiscale> obligations = obligationFiscaleDao.getAllObligationFiscale();
                if (obligations != null && !obligations.isEmpty()) {
                    obligations = obligations.stream()
                            .filter(ob -> ob.getDateEchance() != null &&
                                    ob.getDateEchance().atZone(ZoneId.systemDefault()).getYear() == profitYear)
                            .collect(Collectors.toList());
                    totalFisc = obligations.stream()
                            .mapToDouble(ObligationFiscale::getAmount)
                            .sum();
                } else {
                    totalFisc = 0.0;
                }
            }

            double restante = turnoverTotal - outgoingTotal - totalFisc;
            turnoverProfitLabel.setText(decimalFormat.format(turnoverTotal));
            expensesProfitLabel.setText(decimalFormat.format(outgoingTotal));
            taxProfitLabel.setText(decimalFormat.format(totalFisc));
            restanteLabel.setText(decimalFormat.format(restante));

            logger.info("Profit section updated for year {}: Chiffre d'affaires=€{}, Dépenses=€{}, Impôt=€{}, Restante=€{}",
                    profitYear, turnoverTotal, outgoingTotal, totalFisc, restante);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la section profit : {}", e.getMessage(), e);
            turnoverProfitLabel.setText("€ 0.00");
            expensesProfitLabel.setText("€ 0.00");
            taxProfitLabel.setText("€ 0.00");
            restanteLabel.setText("€ 0.00");
        }
    }
}