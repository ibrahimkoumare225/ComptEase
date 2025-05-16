package fr.koumare.comptease.controllers;

import fr.koumare.comptease.model.*;
import fr.koumare.comptease.model.enumarated.StatusDevis;
import fr.koumare.comptease.model.enumarated.StatusInvoice;
import fr.koumare.comptease.service.ClientService;
import fr.koumare.comptease.service.impl.ClientServiceImpl;
import fr.koumare.comptease.service.impl.DevisServiceImpl;
import fr.koumare.comptease.service.impl.FactureServiceImpl;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/*public class FacturesController extends BaseController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(FacturesController.class);

    @FXML
    private BorderPane chartContainer;

    @FXML
    private AnchorPane createForm;

    @FXML
    private ComboBox<Client> clientComboBox;

    @FXML
    private TextField descriptionField;

    @FXML
    private TableView<Article> articlesTable;

    @FXML
    private TableColumn<Article, String> articleDescriptionColumn;

    @FXML
    private TableColumn<Article, BigDecimal> articlePriceColumn;

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
    private TableView<Facture> invoicesTable;

    @FXML
    private TableColumn<Facture, Long> invoiceIdColumn;

    @FXML
    private TableColumn<Facture, Client> invoiceClientColumn;

    @FXML
    private TableColumn<Facture, Instant> invoiceDateColumn;

    @FXML
    private TableColumn<Facture, StatusInvoice> invoiceStatusColumn;

    @FXML
    private TableColumn<Facture, Double> invoiceTotalColumn;

    @FXML
    private TableColumn<Facture, Void> invoiceActionsColumn;

    private final ClientService clientService = new ClientServiceImpl();
    private final DevisServiceImpl devisService = new DevisServiceImpl();
    private final FactureServiceImpl factureService = new FactureServiceImpl();

    private ObservableList<Article> articlesList = FXCollections.observableArrayList();
    private ObservableList<Client> clientsList;
    private ObservableList<Facture> invoicesList = FXCollections.observableArrayList();
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
        articlesTable.setItems(articlesList);

        // si la liste des artcicles changent on remet le prix ajour
        articlesList.addListener((javafx.beans.Observable observable) -> updateTotalPrice());

        loadFactures();
        invoiceIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        invoiceClientColumn.setCellValueFactory(new PropertyValueFactory<>("client"));
        invoiceClientColumn.setCellFactory(column -> new TableCell<Facture, Client>() {
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
        invoiceActionsColumn.setCellFactory(param -> new TableCell<Facture, Void>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setOnAction(event -> {
                    Facture facture = getTableRow().getItem();
                    if (facture != null) {
                        deleteInvoice(facture);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || getTableRow().getItem() == null ? null : deleteButton);
            }
        });
        invoicesTable.setItems(invoicesList);
    }

    @FXML
    public void addArticle(ActionEvent event) {
        try {
            String description = articleDescription.getText();
            if (description.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "La description de l'article est requise.");
                return;
            }

            BigDecimal price;
            try {
                price = new BigDecimal(articlePrice.getText());
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Avertissement", "Le prix doit être supérieur à 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "Le prix doit être un nombre valide.");
                return;
            }

            Long quantity;
            try {
                quantity = Long.parseLong(articleQuantity.getText());
                if (quantity <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Avertissement", "La quantité doit être supérieure à 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "La quantité doit être un nombre entier valide.");
                return;
            }

            Article article = new Article(description, Arrays.asList("Default Category"), quantity, price);
            articlesList.add(article);
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
    public void createDevis(ActionEvent event) {
        try {
            Client client = clientComboBox.getValue();
            if (client == null) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner un client.");
                return;
            }

            if (articlesList.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez ajouter au moins un article.");
                return;
            }

            String description = descriptionField.getText();
            if (description.isEmpty()) {
                description = "Devis généré le " + Instant.now();
            }

            Devis devis = new Devis(
                    null,
                    description,
                    Instant.now(),
                    StatusDevis.REJECTED,
                    client,
                    new ArrayList<>(articlesList)
            );
            Devis savedDevis = devisService.createDevis(devis);
            if (savedDevis == null) {
                logger.error("Échec de la création du devis");
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la création du devis. Veuillez vérifier les logs pour plus de détails.");
                return;
            }
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Devis créé avec succès ! ID : " + savedDevis.getId());

            // on reinitialise le formulaire
            resetForm();
        } catch (IllegalStateException e) {
            logger.error("Erreur lors de la création du devis : {}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la création du devis : " + e.getMessage());
        }
    }

    @FXML
    public void createInvoice(ActionEvent event) {
        try {
            Client client = clientComboBox.getValue();
            if (client == null) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner un client.");
                return;
            }

            if (articlesList.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez ajouter au moins un article.");
                return;
            }

            String description = descriptionField.getText();
            if (description.isEmpty()) {
                description = "Facture générée le " + Instant.now();
            }

            Invoice facture = new Invoice(
                    0.0,
                    description,
                    Instant.now(),
                    StatusInvoice.UNPAID,
                    client,
                    new ArrayList<>(articlesList) // articles (conversion ObservableList -> List)
            );

            Invoice savedFacture = factureService.createInvoice(facture);
            if (savedFacture == null) {
                logger.error("Échec de la création de la facture");
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la création de la facture. Veuillez vérifier les logs pour plus de détails.");
                return;
            }
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Facture créée avec succès ! ID : " + savedFacture.getId());

            // Réinitialiser le formulaire
            resetForm();
        } catch (IllegalStateException e) {
            logger.error("Erreur lors de la création de la facture : {}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la création de la facture : " + e.getMessage());
        }
    }

    private void loadFactures() {
        try {
            List<Invoice> factures = factureService.getAllFactures();
            invoicesList.setAll((Facture) factures);
            logger.info("Chargement de {} factures", factures.size());
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des factures : {}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des factures : " + e.getMessage());
        }
    }

    private void deleteInvoice(Facture facture) {
        try {
            factureService.deleteDocumentById(facture.getId());
            invoicesList.remove(facture);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Facture supprimée avec succès !");
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la facture ID {} : {}", facture.getId(), e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression de la facture : " + e.getMessage());
        }
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
        articlesList.clear();
        totalPriceLabel.setText("0.0");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        logger.info("Affichage d'une alerte : {} - {}", title, message);
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}*/