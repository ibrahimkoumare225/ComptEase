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
    private ClientService clientService = new ClientServiceImpl();
    private ClientController parentController;

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

    public void setParentController(ClientController controller) {
        this.parentController = controller;
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
        Long idUser = 1L;

        logger.info("Tentative d'ajout d'un nouveau client : {} {}", nom, prenom);

        if (clientService.addClient(nom, prenom, adresse, contact, idUser, solde, note, siret, rib)) {
            logger.info("Client ajouté avec succès : {} {}", nom, prenom);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Client ajouté avec succès.");
            if (parentController != null) {
                parentController.refreshClientList();
            }
            closeWindow();
        } else {
            logger.error("Erreur lors de l'ajout du client : {} {}", nom, prenom);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du client.");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 