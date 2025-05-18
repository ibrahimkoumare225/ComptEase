package fr.koumare.comptease.controllers;

import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.service.ClientService;
import fr.koumare.comptease.service.impl.ClientServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModifClientFormController {
    private static final Logger logger = LoggerFactory.getLogger(ModifClientFormController.class);
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
    private TextField soldeField;
    @FXML
    private TextField siretField;
    @FXML
    private TextField ribField;
    @FXML
    private TextArea noteField;
    @FXML
    private TextField idField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;


    public void setParentController(ClientController controller) {
        this.parentController = controller;
    }
    public void setClientData(Client client) {
        nomField.setText(client.getLastName());
        prenomField.setText(client.getFirstName());
        adresseField.setText(client.getAdresse());
        contactField.setText(client.getContact());
        siretField.setText(client.getSiret());
        ribField.setText(client.getRib());
        noteField.setText(client.getNote());
        idField.setText(String.valueOf(client.getIdc()));
        soldeField.setText(String.valueOf(client.getSolde()));
    }

    @FXML
    private void handleSave() {
        // Récupérer l'ID du client à partir de la sélection dans la liste
        Long id = Long.parseLong(idField.getText());
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String adresse = adresseField.getText();
        String contact = contactField.getText();
        String siret = siretField.getText();
        String rib = ribField.getText();
        String note = noteField.getText();
        Double solde = Double.parseDouble(soldeField.getText()); ;

        logger.info("Tentative de modification du client : {} {}", nom, prenom);

        if(clientService.updateClient(id,nom, prenom, adresse, contact, solde,note, siret, rib)) {
            logger.info("Client modifié : {}", nom +" "+ prenom);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Client modifié avec succès.");

            if(clientService.updateClientBalance(id)){
                logger.info("Solde du client modifié : {}", nom +" "+ prenom);
            } else {
                logger.error("Erreur lors de la modification du solde du client : {}", nom+" " + prenom);
            }
            //recuperer le client modifié
            Optional<Client> updatedClient = clientService.findByNames(nom, prenom);
            if(updatedClient.isPresent()) {
                Client newClient = updatedClient.get();
                logger.info("Client modifié : {}", newClient.getFirstName());
                if (parentController != null) {
                    parentController.refreshClientList();
                }
                closeWindow();
                
            } else {
                logger.error("Erreur lors de la récupération du client modifié : {}", nom+" " + prenom);
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération du client modifié.");
            }
        } else {
            logger.error("Erreur lors de la modification du client : {}", nom+" " + prenom);
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