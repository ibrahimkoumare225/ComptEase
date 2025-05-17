package fr.koumare.comptease.controllers;

import fr.koumare.comptease.model.Article;
import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.model.enumarated.StatusInvoice;
import fr.koumare.comptease.service.ClientService;
import fr.koumare.comptease.service.impl.ClientServiceImpl;
//import fr.koumare.comptease.service.impl.DevisServiceImpl;
import fr.koumare.comptease.service.impl.FactureServiceImpl;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class InvoiceController extends BaseController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    @FXML
    private BorderPane chartContainer;

    @FXML
    private AnchorPane createForm;

    @FXML
    private ComboBox<Client> clientComboBox;

    @FXML
    private Button modifFacture;

    @FXML
    private Button annulerModif;

    @FXML
    private TextField descriptionField;

    @FXML
    private TableView<Article> articlesTable;

    @FXML
    private TableColumn<Article, String> articleDescriptionColumn;

    @FXML
    private TableColumn<Article, Double> articlePriceColumn;

    @FXML
    private TableColumn<Article, Long> articleQuantityColumn;

    @FXML
    private TableColumn<Article, Double> articleTotalColumn;

    @FXML
    private TableColumn<Article, Void> articleActionsColumn;

    @FXML
    private TextField articleDescription;

    @FXML
    private TextField articlePrice;

    @FXML
    private TextField articleQuantity;

    @FXML
    private Button addArticleButton;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Button createDevisButton;

    @FXML
    private Button createInvoiceButton;

    @FXML
    private ComboBox<String> invoiceTypeComboBox;

    @FXML
    private ComboBox<String> invoiceStatusComboBox;


    @FXML
    private TableView<Invoice> invoicesTable;

    @FXML
    private TableColumn<Invoice, Long> invoiceIdColumn;

    @FXML
    private TableColumn<Invoice, Client> invoiceClientColumn;

    @FXML
    private TableColumn<Invoice, Instant> invoiceDateColumn;

    @FXML
    private TableColumn<Invoice, StatusInvoice> invoiceStatusColumn;

    @FXML
    private TableColumn<Invoice, Double> invoiceTotalColumn;

    @FXML
    private TableColumn<Invoice, Void> invoiceActionsColumn;

    private final ClientService clientService = new ClientServiceImpl();

    private final FactureServiceImpl factureService = new FactureServiceImpl();

    private ObservableList<Article> articlesList = FXCollections.observableArrayList();
    private ObservableList<Article> articleSelected = FXCollections.observableArrayList();

    private ObservableList<Client> clientsList;
    Client clientSelected;
    private ObservableList<Invoice> invoicesList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initialisation de FacturesController");

        // la liste des clients
        clientsList = FXCollections.observableArrayList(clientService.getAllClients());
        clientComboBox.setItems(clientsList);
        clientComboBox.setCellFactory(param -> new ListCell<Client>() {
            @Override
            protected void updateItem(Client client, boolean empty) {
                super.updateItem(client, empty);
                if (empty || client == null) {
                    setText(null);
                } else {
                    setText(client.getFirstName() + " " + client.getLastName());
                }
            }
        });
        clientComboBox.setButtonCell(new ListCell<Client>() {
            @Override
            protected void updateItem(Client client, boolean empty) {
                super.updateItem(client, empty);
                if (empty || client == null) {
                    setText(null);
                } else {
                    setText(client.getFirstName() + " " + client.getLastName());
                }
            }
        });

        invoiceTypeComboBox.getItems().addAll("Entrante", "Sortante");
        invoiceStatusComboBox.getItems().addAll("Payée", "Non payée");

        // on configure la table des articles
        articleDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        articlePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        articleQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        articleTotalColumn.setCellValueFactory(cellData -> {
            Article article = cellData.getValue();
            double total = article.getPrice().doubleValue() * article.getQuantite();
            return new ReadOnlyObjectWrapper<>(total);
        });

        articleActionsColumn.setCellFactory(param -> new TableCell<Article, Void>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setOnAction(event -> {
                    Article article = getTableRow().getItem();
                    articlesList.remove(article);
                    updateTotalPrice();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });
        showArticlesInTable(articlesList);

        invoiceIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        invoiceClientColumn.setCellValueFactory(new PropertyValueFactory<>("client"));
        invoiceClientColumn.setCellFactory(column -> new TableCell<Invoice, Client>() {
            @Override
            protected void updateItem(Client client, boolean empty) {
                super.updateItem(client, empty);
                if (empty || client == null) {
                    setText(null);
                } else {
                    setText(client.getFirstName() + " " + client.getLastName());
                }
            }
        });
        invoiceDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        invoiceStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        invoiceTotalColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Invoice, String> invoiceArticlesColumn = new TableColumn<>("Articles");
        invoiceArticlesColumn.setCellValueFactory(cellData -> {
            Invoice invoice = cellData.getValue();
            List<Article> articles = invoice.getArticles();
            if (articles != null && !articles.isEmpty()) {
                String articlesList = articles.stream()
                        .map(Article::getDescription)
                        .collect(Collectors.joining(", "));
                return new SimpleStringProperty(articlesList);
            }
            return new SimpleStringProperty("");
        });
        invoicesTable.getColumns().add(invoiceArticlesColumn);


        invoiceActionsColumn.setCellFactory(param -> new TableCell<Invoice, Void>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setOnAction(event -> deleteInvoice(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || getTableRow().getItem() == null ? null : deleteButton);
            }
        });
        invoicesTable.setItems(invoicesList);
        loadFactures();
    }


    private void deleteInvoice(Invoice invoice) {
        try {
            if (invoice != null && factureService.deleteInvoice(invoice.getId())) {

                Client client = invoice.getClient();
                if (client != null) {
                    clientService.updateClientBalance(client.getIdc());
                    logger.info("Solde du client ID {} mis à jour après suppression de la facture", client.getIdc());
                } else {
                    logger.info("Facture ID {} n'a pas de client associé, pas de mise à jour du solde", invoice.getId());
                }

                invoicesList.remove(invoice);
                loadFactures(); // Rafraîchir la table après suppression
                showAlert(AlertType.INFORMATION, "Succès", "Facture supprimée avec succès !");
            } else {
                showAlert(AlertType.ERROR, "Erreur", "Échec de la suppression de la facture.");
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la facture ID {} : {}", invoice.getId(), e.getMessage(), e);
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la suppression de la facture : " + e.getMessage());
        }
    }
    //recuperer le type de la facture
    private String getTypeInvoice() {
        String selectedType = invoiceTypeComboBox.getValue();
        if (selectedType == null) {
            return null;
        }
        switch (selectedType) {
            case "Entrante":
                return "INCOMING";
            case "Sortante":
                return "OUTGOING";
            
            default:
                return null;
        }
    }
    //recuperer le type de la facture pour le comboBox
    private String getTypeInvoiceFR(String type) {
        switch (type) {
            case "INCOMING":
                return "Entrante";
            case "OUTGOING":
                return "Sortante";
            
            default:
                return null;
        }
    }
    //recuperer le status de la facture
    private String getStatusInvoice() {
        String selectedStatus = invoiceStatusComboBox.getValue();
        if (selectedStatus == null) {
            return null;
        }
        switch (selectedStatus) {
            case "Payée":
                return "PAID";
            case "Non payée":
                return "UNPAID";
            default:
                return null;
        }
    }
    //recuperer le status de la facture pour le comboBox
    private String getStatusInvoiceFR(String status) {
        switch (status) {
            case "PAID":
                return "Payée";
            case "UNPAID":
                return "Non payée";
            default:
                return null;
        }
    }
    @FXML
    public void addArticle(ActionEvent event) {
        try {
            String description = articleDescription.getText();
            if (description.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "La description de l'article est requise.");
                return;
            }

            Double price;
            try {
                price = Double.parseDouble(articlePrice.getText());
                if (price <= 0.0) {
                    showAlert(Alert.AlertType.WARNING, "Avertissement", "Le prix doit être supérieur à 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "Le prix doit être un nombre valide.");
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(articleQuantity.getText());
                if (quantity <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Avertissement", "La quantité doit être supérieure à 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "La quantité doit être un nombre entier valide.");
                return;
            }

            Article article = new Article(description, Arrays.asList("Default Category"), quantity, price);
            articlesList.add(article); // Ajouter uniquement à articlesList, sans sauvegarde dans la BD
            logger.info("Article ajouté à la liste temporaire : {}", article);
            showArticlesInTable(articlesList);

            articleDescription.clear();
            articlePrice.clear();
            articleQuantity.clear();
            updateTotalPrice();
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout d'un article : {}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de l'article : " + e.getMessage());
        }
    }



    @FXML
    public void createInvoice(ActionEvent event) {
        try {
            Client client = clientComboBox.getValue();
            if (getTypeInvoice() != null && getTypeInvoice().equals("OUTGOING")) {
                client = null; //on force le client a etre a null pour les facture sortante
            } else if (client == null) {
                showAlert(AlertType.WARNING, "Avertissement", "Veuillez sélectionner un client pour une facture entrante.");
                return;
            }

            if (articlesList.isEmpty()) {
                showAlert(AlertType.WARNING, "Avertissement", "Veuillez ajouter au moins un article.");
                return;
            }

            String description = descriptionField.getText();
            if (description.isEmpty()) {
                description = "Facture générée le " + Instant.now();
            }

            String status = getStatusInvoice();
            String type = getTypeInvoice();
            if (status == null || type == null) {
                showAlert(AlertType.WARNING, "Avertissement", "Veuillez sélectionner un statut et un type de facture.");
                return;
            }

            logger.info("Création d'une facture : client={}, description={}, status={}, type={}",
                    client != null ? client.getFirstName() + " " + client.getLastName() : "null", description, status, type);

            int totalQuantity = articlesList.stream().mapToInt(Article::getQuantite).sum();
            double totalPrice = articlesList.stream()
                    .mapToDouble(article -> article.getPrice() * article.getQuantite())
                    .sum();


            List<Article> persistentArticles = new ArrayList<>();
            for (Article article : articlesList) {
                if (factureService.enregistrerArticle(article)) {
                    persistentArticles.add(article);
                    logger.info("Article sauvegardé avec succès dans la BD : ID={}", article.getId());
                } else {
                    logger.error("Erreur lors de la sauvegarde de l'article : {}", article);
                    showAlert(AlertType.ERROR, "Erreur", "Échec de la sauvegarde d'un article.");
                    return;
                }
            }


            if (factureService.addInvoice(description, Instant.now(), status, client != null ? client.getIdc() : null, persistentArticles, type, totalQuantity)) {
                if (client != null) {
                    for (Article article : persistentArticles) {
                        int newQuantity;
                        if (type.equals("OUTGOING")) {
                            newQuantity = article.getQuantite() - article.getQuantite();
                            if (newQuantity < 0) {
                                showAlert(AlertType.WARNING, "Avertissement", "Impossible !! Vous n'avez pas assez d'articles.");
                                return;
                            } else if (newQuantity == 0) {
                                showAlert(AlertType.WARNING, "Avertissement", "Vous n'avez plus de stock pour l'article " + article.getDescription() + ".");
                                return;
                            }
                            factureService.updateArticle(article.getId(), article.getDescription(), Arrays.asList("Default Category"), newQuantity, article.getPrice());
                        } else if (type.equals("INCOMING")) {
                            newQuantity = article.getQuantite() + article.getQuantite();
                            factureService.updateArticle(article.getId(), article.getDescription(), Arrays.asList("Default Category"), newQuantity, article.getPrice());
                        }
                    }
                }

                //mise a jour du solde du client
                if (client != null) {
                if (clientService.updateClientBalance(client.getIdc())) {
                    logger.info("Le solde du client a été mis à jour avec succès : {}", client.getIdc());
                } else {
                    logger.error("Erreur lors de la mise à jour du solde du client : {}", client.getIdc());
                }
                }

                articlesList.clear();
                showArticlesInTable(articlesList);
                loadFactures();
                showAlert(AlertType.CONFIRMATION, "Succès", "Facture créée avec succès");
            } else {
                showAlert(AlertType.ERROR, "Erreur", "Échec de la création de la facture.");
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la facture : {}", e.getMessage(), e);
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la création de la facture : " + e.getMessage());
        }
        resetForm();
        showArticlesInTable(articlesList);
    }

    private void updateTotalPrice() {
        double total = articlesList.stream()
                .mapToDouble(article -> article.getPrice().doubleValue() * article.getQuantite())
                .sum();
        totalPriceLabel.setText(String.format("%.2f", total));
    }

    private void resetForm() {
        clientComboBox.getSelectionModel().clearSelection();
        descriptionField.clear();
        invoiceTypeComboBox.getSelectionModel().clearSelection();
        invoiceStatusComboBox.getSelectionModel().clearSelection();

    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        logger.info("Affichage d'une alerte : {} - {}", title, message);
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //afficher les articles dans la table
    private void showArticlesInTable(ObservableList<Article> articles) {
        articleDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        articlePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        articleQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        articleTotalColumn.setCellValueFactory(cellData -> {
            Article article = cellData.getValue();
            double total = article.getPrice().doubleValue() * article.getQuantite();
            return new ReadOnlyObjectWrapper<>(total);
        });
        articlesTable.setItems(articles);
        updateTotalPrice();
        articlesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        if (newSelection != null) {
            articleDescription.setText(newSelection.getDescription());
            articlePrice.setText(String.valueOf(newSelection.getPrice()));
            articleQuantity.setText(String.valueOf(newSelection.getQuantite()));
            articleSelected.add(newSelection);
        } else {
            articleDescription.clear();
            articlePrice.clear();
            articleQuantity.clear();
        }
        });
    }


    private void loadFactures() {
        try {
            List<Invoice> invoices = factureService.getAllInvoices();
            invoicesList.setAll(invoices);
            logger.info("Chargement de {} factures", invoices.size());
            invoicesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    modifFacture.setVisible(true);
                    annulerModif.setVisible(true);
                    Invoice selectedInvoice = newSelection;
                    descriptionField.setText(selectedInvoice.getDescription());
                    invoiceTypeComboBox.setValue(getTypeInvoiceFR(selectedInvoice.getType().toString()));
                    invoiceStatusComboBox.setValue(getStatusInvoiceFR(selectedInvoice.getStatus().toString()));
                    articleDescription.setText(selectedInvoice.getArticles().get(0).getDescription());
                    articlePrice.setText(String.valueOf(selectedInvoice.getArticles().get(0).getPrice()));
                    articleQuantity.setText(String.valueOf(selectedInvoice.getArticles().get(0).getQuantite()));
                    Client selectedClient = selectedInvoice.getClient();
                    if(selectedClient == null){
                        clientComboBox.setValue(null);
                    }else{
                        for (Client client : clientsList) {
                            if (client.getIdc().equals(selectedClient.getIdc())) {
                                clientComboBox.setValue(client);
                                break;
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des factures : {}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des factures : " + e.getMessage());
        }
    }

    @FXML
    private void updateFacture(ActionEvent event) {
        try {
            Long selectedInvoiceId = invoicesTable.getSelectionModel().getSelectedItem().getId();
            String descriptionFacture = descriptionField.getText();
            String statut = getStatusInvoice();
            String type = getTypeInvoice();
            String descriptionArticle = articleDescription.getText();
            Double price = Double.parseDouble(articlePrice.getText());
            int quantity = Integer.parseInt(articleQuantity.getText());
            Article article = new Article(descriptionArticle, Arrays.asList("Default Category"), quantity, price);
            ObservableList<Article> articlesListe = FXCollections.observableArrayList();
            articlesListe.add(article);

            Client client = clientComboBox.getValue();
            if (client == null && type.equals("INCOMING")) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner un client.");
                return;
            }
            if (descriptionFacture.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez entrer une description.");
                return;
            }
            Boolean update=factureService.updateInvoice(
                    invoicesTable.getSelectionModel().getSelectedItem().getId(),
                    descriptionFacture,
                    Instant.now(),
                    statut,
                    client != null ? client.getIdc() : null,
                    articlesListe,
                    type,
                    quantity
            );
            if(update){
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Facture mise à jour avec succès !");
                resetForm();
                modifFacture.setVisible(false);
                annulerModif.setVisible(false);
                loadFactures();
            }
            else{
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour de la facture.");
            }



        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la facture : {}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour de la facture : " + e.getMessage());
        }

    
    }

    @FXML
    private void cancelUpdate(ActionEvent event) {
        resetForm();
        modifFacture.setVisible(false);
        annulerModif.setVisible(false);
        articleDescription.clear();
        articlePrice.clear();
        articleQuantity.clear();

    }



}