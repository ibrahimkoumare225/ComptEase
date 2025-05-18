package fr.koumare.comptease.controllers;

import fr.koumare.comptease.service.ClientService;
import fr.koumare.comptease.service.impl.ClientServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddClientFormController {

    private static final Logger logger = LoggerFactory.getLogger(AddClientFormController.class);

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField adresseField;

    @FXML
    private TextField contactField;

    @FXML
    private TextField siretField;

    @FXML
    private TextField ribField;

    @FXML
    private TextArea noteField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private ClientService clientService = new ClientServiceImpl();
    private ClientController parentController;

    public void setParentController(ClientController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleSave() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String adresse = adresseField.getText();
        String contact = contactField.getText();
        String siret = siretField.getText();
        String rib = ribField.getText();
        String note = noteField.getText();
        Double solde = 0.0;
        Long idu = 1L; // Assuming a default user ID; adjust as needed

        logger.info("Ajout d'un client : {} {} (Adresse: {}, Contact: {}, SIRET: {}, RIB: {}, Note: {})",
                nom, prenom, adresse, contact, siret, rib, note);

        if (clientService.addClient(nom, prenom, adresse, contact, idu, solde, note, siret, rib)) {
            logger.info("Client ajouté avec succès : {} {}", nom, prenom);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Client ajouté avec succès.");
            parentController.refreshClientList();
            closeWindow();
        } else {
            logger.error("Erreur lors de l'ajout du client : {} {}", nom, prenom);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du client.");
        }
    }

    @FXML
    private void handleCancel() {
        logger.info("Annulation de l'ajout du client");
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        logger.info("Affichage d'une alerte : {} - {}", title, message);
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}