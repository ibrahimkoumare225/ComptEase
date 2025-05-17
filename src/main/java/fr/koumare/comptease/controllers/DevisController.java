//package fr.koumare.comptease.controllers;
//
//import fr.koumare.comptease.model.Client;
//import fr.koumare.comptease.model.Devis;
//import fr.koumare.comptease.model.Invoice;
//import fr.koumare.comptease.model.enumarated.StatusDevis;
////import fr.koumare.comptease.service.impl.DevisServiceImpl;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.BorderPane;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.net.URL;
//import java.time.Instant;
//import java.util.ResourceBundle;
//
//public class DevisController extends BaseController implements Initializable {
//
//    private static final Logger logger = LoggerFactory.getLogger(DevisController.class);
//
//    @FXML
//    private BorderPane chartContainer;
//
//    @FXML
//    private AnchorPane devisListPane;
//
//    @FXML
//    private TableView<Devis> devisTable;
//
//    @FXML
//    private TableColumn<Devis, Long> devisIdColumn;
//
//    @FXML
//    private TableColumn<Devis, String> devisDescriptionColumn;
//
//    @FXML
//    private TableColumn<Devis, Instant> devisDateColumn;
//
//    @FXML
//    private TableColumn<Devis, Client> devisClientColumn;
//
//    @FXML
//    private TableColumn<Devis, StatusDevis> devisStatusColumn;
//
//    @FXML
//    private TableColumn<Devis, Void> devisActionsColumn;
//
//    private final DevisServiceImpl devisService = new DevisServiceImpl();
//    private ObservableList<Devis> devisList = FXCollections.observableArrayList();
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        logger.info("Initialisation de DevisController");
//
//        // la table des devis
//        devisIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        devisDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
//        devisDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
//        devisClientColumn.setCellValueFactory(new PropertyValueFactory<>("client"));
//        devisClientColumn.setCellFactory(column -> new TableCell<Devis, Client>() {
//            @Override
//            protected void updateItem(Client client, boolean empty) {
//                super.updateItem(client, empty);
//                if (empty || client == null) {
//                    setText(null);
//                } else {
//                    setText(client.getFirstName() + " " + client.getLastName());
//                }
//            }
//        });
//        devisStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
//
//        //  actions (boutons pour modifier le statut et générer une facture)
//        devisActionsColumn.setCellFactory(param -> new TableCell<Devis, Void>() {
//            private final Button toggleStatusButton = new Button("Modifier Statut");
//            private final Button generateInvoiceButton = new Button("Créer Facture");
//
//            {
//                toggleStatusButton.setOnAction(event -> {
//                    Devis devis = getTableRow().getItem();
//                    if (devis != null) {
//                        toggleDevisStatus(devis);
//                    }
//                });
//                generateInvoiceButton.setOnAction(event -> {
//                    Devis devis = getTableRow().getItem();
//                    if (devis != null) {
//                        generateInvoiceFromDevis(devis);
//                    }
//                });
//            }
//
//            @Override
//            protected void updateItem(Void item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty || getTableRow().getItem() == null) {
//                    setGraphic(null);
//                } else {
//                    Devis devis = getTableRow().getItem();
//                    // affiche le bouton "Créer Facture" uniquement si le devis est accepté et n'a pas de facture associée
//                    if (devis.getStatus() == StatusDevis.ACCEPTED && devis.getInvoice() == null) {
//                        setGraphic(new javafx.scene.layout.HBox(10, toggleStatusButton, generateInvoiceButton));
//                    } else {
//                        setGraphic(toggleStatusButton);
//                    }
//                }
//            }
//        });
//
//        // Charger les devis
//        loadDevis();
//        devisTable.setItems(devisList);
//    }
//
//    private void loadDevis() {
//        logger.info("Chargement des devis");
//        try {
//            devisList.setAll(devisService.getAllDevis());
//            logger.info("Devis chargés avec succès : {}", devisList.size());
//        } catch (Exception e) {
//            logger.error("Erreur lors du chargement des devis : {}", e.getMessage());
//            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des devis : " + e.getMessage());
//        }
//    }
//
//    private void toggleDevisStatus(Devis devis) {
//        try {
//            devisService.updateDevisStatus(devis.getId());
//            logger.info("Statut du devis ID : {} modifié avec succès", devis.getId());
//            loadDevis(); // rafraîchi la liste
//        } catch (IllegalArgumentException e) {
//            logger.error("Erreur lors de la modification du statut du devis ID : {} - {}", devis.getId(), e.getMessage());
//            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification du statut : " + e.getMessage());
//        }
//    }
//
//
//    private void generateInvoiceFromDevis(Devis devis) {
//        try {
//            Invoice invoice = devisService.createInvoiceFromDevis(devis);
//            if (invoice == null) {
//                logger.error("Échec de la création de la facture pour le devis ID : {}", devis.getId());
//                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la création de la facture. Veuillez vérifier les logs pour plus de détails.");
//                return;
//            }
//            logger.info("Facture créée avec succès pour le devis ID : {}", devis.getId());
//            showAlert(Alert.AlertType.INFORMATION, "Succès", "Facture créée avec succès ! ID : " + invoice.getId());
//            loadDevis(); // rafraîchi la liste
//        } catch (IllegalStateException e) {
//            logger.error("Erreur lors de la création de la facture pour le devis ID : {} - {}", devis.getId(), e.getMessage());
//            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la création de la facture : " + e.getMessage());
//        }
//    }
//
//    private void deleteDevis(Devis devis) {
//        try {
//            devisService.deleteDevis(devis.getId()); // Utilisation de la méthode de DocumentServiceImpl
//            loadDevis();
//            showAlert(Alert.AlertType.INFORMATION, "Succès", "Devis supprimé avec succès !");
//        } catch (Exception e) {
//            logger.error("Erreur lors de la suppression du devis ID {} : {}", devis.getId(), e.getMessage());
//            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression du devis : " + e.getMessage());
//        }
//    }
//
//    private void showAlert(Alert.AlertType type, String title, String message) {
//        logger.info("Affichage d'une alerte : {} - {}", title, message);
//        Alert alert = new Alert(type);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//}