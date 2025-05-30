package fr.koumare.comptease.controllers;

import fr.koumare.comptease.model.Company;
import fr.koumare.comptease.model.User;
import fr.koumare.comptease.service.impl.CompanyServiceImpl;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Infos2Controller implements Initializable {

    @FXML
    private ChoiceBox<String> salesNatureChoice;

    @FXML
    private DatePicker creationDatePicker;

    @FXML
    private DatePicker closingDatePicker;

    private User currentUser; // Utilisateur connecté
    private CompanyServiceImpl companyService = new CompanyServiceImpl();

    // Méthode pour définir l'utilisateur connecté
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    private void handleContinue(ActionEvent event) {
        String salesNature = salesNatureChoice.getValue();
        java.time.LocalDate creationDate = creationDatePicker.getValue();
        java.time.LocalDate closingDate = closingDatePicker.getValue();

        if (salesNature == null || creationDate == null || closingDate == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez remplir tous les champs.");
            return;
        }

        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Utilisateur non connecté.");
            return;
        }

        Company company = companyService.findCompanyByUser(currentUser);
        if (company == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune entreprise trouvée pour cet utilisateur.");
            return;
        }

        company.setSalesNature(salesNature);
        company.setCreationDate(creationDate);
        company.setClosingDate(closingDate);

        if (companyService.saveCompany(company)) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Informations enregistrées !");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/koumare/comptease/fxml/dashboard.fxml"));
                Scene scene = new Scene(loader.load(), 1300, 720);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Tableau de bord");
                stage.show();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger Dashboard.fxml : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la sauvegarde des informations.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser la ChoiceBox avec des options
        salesNatureChoice.setItems(FXCollections.observableArrayList(
                "Vente de marchandises", "Prestation de services", "Mixte", "Exportation"
        ));
    }
}