package fr.koumare.comptease.controllers;

import fr.koumare.comptease.dao.CompanyDao;
import fr.koumare.comptease.model.Company;
import fr.koumare.comptease.model.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class InfosController implements Initializable {

    @FXML
    private TextField companyNameField;

    @FXML
    private ChoiceBox<String> legalFormChoice;

    @FXML
    private ChoiceBox<String> taxRegimeChoice;

    @FXML
    private ChoiceBox<String> professionChoice;

    private User currentUser; // Utilisateur connecté
    private CompanyDao companyDao = new CompanyDao();

    // Méthode pour définir l'utilisateur connecté
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    private void handleContinue(ActionEvent event) {
        String companyName = companyNameField.getText();
        String legalForm = legalFormChoice.getValue();
        String taxRegime = taxRegimeChoice.getValue();
        String profession = professionChoice.getValue();

        if (companyName.isEmpty() || legalForm == null || taxRegime == null || profession == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez remplir tous les champs.");
            return;
        }

        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Utilisateur non connecté.");
            return;
        }

        // Créer ou mettre à jour l'entité Company
        Company company = companyDao.findCompanyByUser(currentUser);
        if (company == null) {
            company = new Company();
            company.setUser(currentUser);
        }
        company.setCompanyName(companyName);
        company.setLegalForm(legalForm);
        company.setTaxRegime(taxRegime);
        company.setProfession(profession);

        // Enregistrer dans la base de données
        companyDao.saveCompany(company);
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Informations enregistrées !");

        // Redirection vers infos2.fxml
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/koumare/comptease/fxml/infos2.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            Infos2Controller infos2Controller = loader.getController();
            infos2Controller.setCurrentUser(currentUser); // Passer l'utilisateur
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Informations activité");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger infos2.fxml : " + e.getMessage());
            e.printStackTrace();
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
        // Initialiser les ChoiceBox avec des options
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
}