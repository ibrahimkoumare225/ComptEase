package fr.koumare.comptease.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public abstract class BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @FXML
    protected Button logoutButton;

    protected void navigateTo(String fxmlPath, String title) {
        try {
            logger.debug("Attempting to load FXML: {}", fxmlPath);
            URL resourceUrl = getClass().getResource(fxmlPath);
            if (resourceUrl == null) {
                logger.error("FXML resource not found: {}", fxmlPath);
                showAlert("Erreur de navigation", "Le fichier FXML est introuvable : " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Scene scene = new Scene(loader.load(), 1300, 720);
            Stage stage;
            if (logoutButton != null && logoutButton.getScene() != null) {
                stage = (Stage) logoutButton.getScene().getWindow();
            } else {
                logger.warn("logoutButton is null or not in a scene. Creating new stage.");
                stage = new Stage();
            }
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            logger.error("Erreur lors de la navigation vers {} : {}", fxmlPath, e.getMessage(), e);
            showAlert("Erreur de navigation", "Impossible de charger la page : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    protected void handleNavigateToDashboard() {
        logger.debug("Dashboard button clicked");
        logger.info("Navigating to Dashboard");
        navigateTo("/fr/koumare/comptease/fxml/dashboard.fxml", "Dashboard");
    }

    @FXML
    protected void handleNavigateToRapportFinancier() {
        logger.debug("Rapport Financier button clicked");
        logger.info("Navigating to Rapport Financier");
        navigateTo("/fr/koumare/comptease/fxml/rapportFinancier.fxml", "Rapport Financier");
    }

    @FXML
    protected void handleNavigateToTransaction() {
        logger.debug("Transaction button clicked");
        logger.info("Navigating to Transaction");
        navigateTo("/fr/koumare/comptease/fxml/transaction.fxml", "Transaction");
    }

    @FXML
    protected void handleNavigateToObligationFiscale() {
        logger.debug("Obligation Fiscales button clicked");
        logger.info("Navigating to Obligation Fiscales");
        navigateTo("/fr/koumare/comptease/fxml/obligationFiscale.fxml", "Obligation Fiscales");
    }

    @FXML
    protected void handleNavigateToClient() {
        logger.debug("Clients button clicked");
        logger.info("Navigating to Clients");
        navigateTo("/fr/koumare/comptease/fxml/clientPage.fxml", "Clients");
    }

    @FXML
    protected void handleNavigateToFactures() {
        logger.debug("Factures button clicked");
        logger.info("Navigating to Factures");
        navigateTo("/fr/koumare/comptease/fxml/factures.fxml", "Factures");
    }

    @FXML
    protected void handleNavigateToParametre() {
        logger.debug("Paramètres button clicked");
        logger.info("Navigating to Paramètres");
        navigateTo("/fr/koumare/comptease/fxml/parametre.fxml", "Paramètres");
    }

    @FXML
    protected void handleLogout() {
        logger.debug("Déconnexion button clicked");
        logger.info("Navigating to Connexion");
        navigateTo("/fr/koumare/comptease/fxml/auth.fxml", "Connexion");
    }
}