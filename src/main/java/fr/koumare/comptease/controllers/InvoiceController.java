package fr.koumare.comptease.controllers;

import com.sun.javafx.collections.ElementObservableListDecorator;
import fr.koumare.comptease.model.Article;
import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.model.enumarated.StatusInvoice;
import fr.koumare.comptease.service.ClientService;
import fr.koumare.comptease.service.impl.ClientServiceImpl;
//import fr.koumare.comptease.service.impl.DevisServiceImpl;
import fr.koumare.comptease.service.impl.FactureServiceImpl;
import javafx.beans.property.ReadOnlyObjectWrapper;
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

public class InvoiceController extends BaseController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

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
//    private final DevisServiceImpl devisService = new DevisServiceImpl();
    private final FactureServiceImpl factureService = new FactureServiceImpl();

    private ObservableList<Article> articlesList = FXCollections.observableArrayList();
    private ObservableList<Article> articleSelected = FXCollections.observableArrayList();

    private ObservableList<Client> clientsList;
    Client clientSelected;
    private ObservableList<Invoice> invoicesList = FXCollections.observableArrayList();

    // private ObservableList<Invoice> invoicesList = FXCollections.observableArrayList(factureService.getAllFactures());
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
        // si la liste des artcicles changent on remet le prix ajour
        articlesList.addListener((javafx.beans.Observable observable) -> updateTotalPrice());

        //showFacturesInTable(invoicesList);
        /*loadFactures();
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
        invoiceActionsColumn.setCellFactory(param -> new TableCell<Invoice, Void>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setOnAction(event -> {
                    Invoice invoice = getTableRow().getItem();
                    if (invoice != null) {
                        deleteInvoice(invoice);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || getTableRow().getItem() == null ? null : deleteButton);
            }
        });
        invoicesTable.setItems(invoicesList);*/
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

//    @FXML
//    public void createDevis(ActionEvent event) {
//        try {
//            Client client = clientComboBox.getValue();
//            if (client == null) {
//                showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner un client.");
//                return;
//            }
//
//            if (articlesList.isEmpty()) {
//                showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez ajouter au moins un article.");
//                return;
//            }
//
//            String description = descriptionField.getText();
//            if (description.isEmpty()) {
//                description = "Devis généré le " + Instant.now();
//            }
//
//            Devis devis = new Devis(
//                    null,
//                    description,
//                    Instant.now(),
//                    StatusDevis.REJECTED,
//                    client,
//                    new ArrayList<>(articlesList)
//            );
//            Devis savedDevis = devisService.createDevis(devis);
//            if (savedDevis == null) {
//                logger.error("Échec de la création du devis");
//                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la création du devis. Veuillez vérifier les logs pour plus de détails.");
//                return;
//            }
//            showAlert(Alert.AlertType.INFORMATION, "Succès", "Devis créé avec succès ! ID : " + savedDevis.getId());
//
//            // on reinitialise le formulaire
//            resetForm();
//        } catch (IllegalStateException e) {
//            logger.error("Erreur lors de la création du devis : {}", e.getMessage());
//            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la création du devis : " + e.getMessage());
//        }
//    }

    @FXML
    public void createInvoice(ActionEvent event) {
        try {
            Client client = clientComboBox.getValue();
            if (client == null) {
                showAlert(AlertType.WARNING, "Avertissement", "Veuillez sélectionner un client.");
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
                    client.getFirstName() + " " + client.getLastName(), description, status, type);

            int totalQuantity = articlesList.stream().mapToInt(Article::getQuantite).sum();
            double totalPrice = articlesList.stream()
                    .mapToDouble(article -> article.getPrice() * article.getQuantite())
                    .sum();

            // Sauvegarder les articles dans la base de données
            List<Article> persistentArticles = new ArrayList<>();
            for (Article article : articlesList) {
                if (factureService.enregistrerArticle(article)) {
                    persistentArticles.add(article); // L'article a maintenant un ID valide
                    logger.info("Article sauvegardé avec succès dans la BD : ID={}", article.getId());
                } else {
                    logger.error("Erreur lors de la sauvegarde de l'article : {}", article);
                    showAlert(AlertType.ERROR, "Erreur", "Échec de la sauvegarde d'un article.");
                    return;
                }
            }

            // Créer la facture avec les articles persistants
            if (factureService.addInvoice(description, Instant.now(), status, client.getIdc(), persistentArticles, type, totalQuantity)) {
                // Mettre à jour les quantités des articles selon le type de facture
                for (Article article : persistentArticles) {
                    int newQuantity;
                    if (type.equals("INCOMING")) {
                        newQuantity = article.getQuantite() - article.getQuantite();
                        if (newQuantity < 0) {
                            showAlert(AlertType.WARNING, "Avertissement", "Impossible !! Vous n'avez pas assez d'articles.");
                            return;
                        } else if (newQuantity == 0) {
                            showAlert(AlertType.WARNING, "Avertissement", "Vous n'avez plus de stock pour l'article " + article.getDescription() + ".");
                            return;
                        }
                        factureService.updateArticle(article.getId(), article.getDescription(), Arrays.asList("Default Category"), newQuantity, article.getPrice());
                    } else if (type.equals("OUTGOING")) {
                        newQuantity = article.getQuantite() + article.getQuantite();
                        factureService.updateArticle(article.getId(), article.getDescription(), Arrays.asList("Default Category"), newQuantity, article.getPrice());
                    }
                }

                // Mettre à jour le solde du client
                if (clientService.updateClientBalance(client.getIdc())) {
                    logger.info("Le solde du client a été mis à jour avec succès : {}", client.getIdc());
                } else {
                    logger.error("Erreur lors de la mise à jour du solde du client : {}", client.getIdc());
                }

                articlesList.clear();
                showArticlesInTable(articlesList);
                loadFactures(); // Rafraîchir la table des factures
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

    @FXML
    /*public void createInvoice(ActionEvent event) {
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
            Double prix=Double.parseDouble(articlePrice.getText()) *Integer.parseInt(quantityField.getText());
            Instant date = Instant.now();
            
            if(factureService.createInvoice(prix, description, date, getStatusInvoice(), client.getIdc(), articleSelected, getTypeInvoice(), Integer.parseInt(quantityField.getText()))){
                Article article = articleSelected.get(0);
                logger.info("Facture créée avec succès : {}", article.getDescription());
                int newQuantity ;
                
                if( getTypeInvoice().equals("INCOMING")){
                    newQuantity= article.getQuantite() - Integer.parseInt(quantityField.getText());
                    if(newQuantity< 0){
                        showAlert(Alert.AlertType.WARNING, "Avertissement", "Impossible!! Vous n'avez pas assez d'articles.");
                        return;
                    }
                    else if(newQuantity == 0){
                            showAlert(Alert.AlertType.WARNING, "Avertissement", "Vous n'avez plus de stock pour l'article "+articleDescription+" .");
                            return;
                    }
                    factureService.updateArticle(article.getId(),articleDescription.getText(),Arrays.asList("Default Category"), newQuantity, article.getPrice());
                }
                else if( getTypeInvoice().equals("OUTGOING")){
                    newQuantity= article.getQuantite() + Integer.parseInt(quantityField.getText());
                    factureService.updateArticle(article.getId(),articleDescription.getText(),Arrays.asList("Default Category"), newQuantity, article.getPrice());
                }
                //modifier le solde du client
                if(clientService.updateClientBalance(client.getIdc())){
                    logger.info("Le solde du client a été mis à jour avec succès : {}", client.getIdc());
                }
                else{
                    logger.error("Erreur lors de la mise à jour du solde du client : {}", client.getIdc());
                }
                articlesList= FXCollections.observableArrayList(factureService.getAllArticles());
                showArticlesInTable(articlesList);
                articleSelected.clear();
                showAlert(AlertType.CONFIRMATION, "Succes", "facture créee avec succès");
            }
            else{
                logger.error("Erreur lors de la création de la facture : {}", articleSelected.get(0).getDescription());
            }
            Invoice invoice = new Invoice(
                    0.0,
                    description,
                    Instant.now(),
                    StatusInvoice.UNPAID,
                    client,
                    new ArrayList<>(articlesList) // articles (conversion ObservableList -> List),
                    "INCOMING",


            );

            Invoice savedInvoice = factureService.createInvoice(invoice);
            if (savedInvoice == null) {
                logger.error("Échec de la création de la facture");
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la création de la facture. Veuillez vérifier les logs pour plus de détails.");
                return;
            }
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Facture créée avec succès ! ID : " + savedInvoice.getId());


            resetForm();
            showArticlesInTable(articlesList);
        } catch (IllegalStateException e) {
            logger.error("Erreur lors de la création de la facture : {}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la création de la facture : " + e.getMessage());
        }
    }*/





//     private void loadFactures() {
//        try {
//            List<Invoice> invoices = factureService.getAllFactures();
//            invoicesList.setAll(invoices);
//            logger.info("Chargement de {} factures", invoices.size());
//        } catch (Exception e) {
//            logger.error("Erreur lors du chargement des factures : {}", e.getMessage());
//            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des factures : " + e.getMessage());
//        }
//    }

//    private void deleteInvoice(Invoice facture) {
//        try {
//            factureService.deleteDocumentById(facture.getId());
//            invoicesList.remove(facture);
//            showAlert(Alert.AlertType.INFORMATION, "Succès", "Facture supprimée avec succès !");
//        } catch (Exception e) {
//            logger.error("Erreur lors de la suppression de la facture ID {} : {}", facture.getId(), e.getMessage());
//            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression de la facture : " + e.getMessage());
//        }
//    }*/

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

    //afficher les factures dans la table
    private void showFacturesInTable(ObservableList<Invoice> invoices) {
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
        invoicesTable.setItems(invoices);
        
    }

    private void loadFactures() {
        try {
            List<Invoice> invoices = factureService.getAllFactures();
            invoicesList.setAll(invoices);
            logger.info("Chargement de {} factures", invoices.size());
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des factures : {}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des factures : " + e.getMessage());
        }
    }



}