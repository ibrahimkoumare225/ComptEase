package fr.koumare.comptease.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalDate;

import fr.koumare.comptease.model.Company;
import fr.koumare.comptease.model.User;
import fr.koumare.comptease.service.impl.CompanyServiceImpl;
import fr.koumare.comptease.service.impl.UserServiceImpl;

public class InfosController implements Initializable {

    @FXML private TextField companyNameField;
    @FXML private ChoiceBox<String> legalFormChoice;
    @FXML private ChoiceBox<String> taxRegimeChoice;
    @FXML private ChoiceBox<String> professionChoice;
    @FXML private TextField siretField;
    @FXML private TextField ribField;
    @FXML private TextField capitalSocialField;
    @FXML private TextField tvaNumberField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;

    private CompanyServiceImpl companyService = new CompanyServiceImpl();
    private UserServiceImpl userService = new UserServiceImpl();

    public void setCurrentUser(User user) {
        userService.setCurrentUser(user);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialisation des choix pour les ChoiceBox
        legalFormChoice.setItems(FXCollections.observableArrayList(
            "SARL", "SAS", "SA", "EURL", "SASU", "Auto-entrepreneur", "SCI"
        ));

        taxRegimeChoice.setItems(FXCollections.observableArrayList(
            "Régime réel normal", "Régime réel simplifié", "Micro-entreprise", "Franchise en base de TVA"
        ));

        professionChoice.setItems(FXCollections.observableArrayList(
            "Commerce", "Artisanat", "Profession libérale", "Services", "Industrie", "Agriculture"
        ));
    }

    @FXML
    private void handleContinue() {
        // Récupération des valeurs
        String companyName = companyNameField.getText();
        String legalForm = legalFormChoice.getValue();
        String taxRegime = taxRegimeChoice.getValue();
        String profession = professionChoice.getValue();
        String siret = siretField.getText();
        String rib = ribField.getText();
        String capitalSocialStr = capitalSocialField.getText();
        String tvaNumber = tvaNumberField.getText();
        String address = addressField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();

        // Validation des champs obligatoires
        if (companyName.isEmpty() || legalForm == null || taxRegime == null || 
            profession == null || siret.isEmpty() || rib.isEmpty() || 
            capitalSocialStr.isEmpty() || tvaNumber.isEmpty() || 
            address.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        // Conversion du capital social
        Double capitalSocial;
        try {
            capitalSocial = Double.parseDouble(capitalSocialStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le capital social doit être un nombre valide.");
            return;
        }

        // Récupération de l'utilisateur connecté
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun utilisateur connecté.");
            return;
        }

        // Création de l'entreprise
        Company company = new Company(
            companyName, legalForm, taxRegime, profession,
            "Vente de marchandises", // Valeur par défaut pour salesNature
            LocalDate.now(), // Date de création
            null, // Date de clôture
            currentUser,
            siret, rib, address, phone, email,
            capitalSocial, tvaNumber
        );

        // Sauvegarde de l'entreprise
        if (companyService.saveCompany(company)) {
            try {
                // Chargement de la page suivante
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/koumare/comptease/fxml/infos2.fxml"));
                Parent root = loader.load();
                Infos2Controller infos2Controller = loader.getController();
                infos2Controller.setCurrentUser(currentUser); // Passer l'utilisateur au contrôleur
                Scene scene = new Scene(root);
                Stage stage = (Stage) companyNameField.getScene().getWindow();
                stage.setScene(scene);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la page suivante : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la sauvegarde des informations de l'entreprise.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}