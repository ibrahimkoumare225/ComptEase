package fr.koumare.comptease.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import fr.koumare.comptease.dao.ObligationFiscaleDao;
import fr.koumare.comptease.model.ObligationFiscale;
import fr.koumare.comptease.model.User;
import fr.koumare.comptease.service.impl.ObligationFiscaleServiceImpl;

public class ObligationFiscaleController extends BaseController implements Initializable {

    @FXML private TextField amountField;
    @FXML private DatePicker echeancePicker;
    @FXML private ComboBox<String> typeImpot;
    @FXML private TableView<ObligationFiscale> obligationTable;
    @FXML private TableColumn<ObligationFiscale, Long> idColumn;
    @FXML private TableColumn<ObligationFiscale, Double> montantColumn;
    @FXML private TableColumn<ObligationFiscale, String> typeColumn;
    @FXML private TableColumn<ObligationFiscale, Instant> echeanceColumn;

    private ObligationFiscale obligationEnCours;
    private final ObligationFiscaleServiceImpl service = new ObligationFiscaleServiceImpl();
    private final ObligationFiscaleDao obligationFiscaleDao = new ObligationFiscaleDao();

    @FXML
    public void initialize(URL location, ResourceBundle resources)
    {
        if (typeImpot != null)
        {
            typeImpot.getItems().addAll("CFP", "IR", "CS");

            amountField.setTextFormatter(new TextFormatter<>(change -> {
                String newText = change.getControlNewText();
                if (newText.matches("\\d*(\\.\\d*)?")) {
                    return change;
                }
                return null;
            }));
        }

        if (obligationTable != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            montantColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
            echeanceColumn.setCellValueFactory(new PropertyValueFactory<>("dateEchance"));
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("typeImpot"));
            chargerObligations();
        }
    }


    private void chargerObligations()
    {
        List<ObligationFiscale> obligations = obligationFiscaleDao.getAllObligationFiscale();
        ObservableList<ObligationFiscale> observableList = FXCollections.observableArrayList(obligations);
        obligationTable.setItems(observableList);
    }

    private void afficherModal(String titre, ObligationFiscale obligation)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/koumare/comptease/fxml/modal_obligation.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la modal
            ObligationFiscaleController modalController  = loader.getController();

            if (obligation != null)
            {
                modalController.prefillFormulaire(obligation);
            }


            //  fenêtre (stage) pour la modal
            Scene modalScene = new Scene(root);
            Stage modalStage = new Stage();
            modalStage.setTitle(titre);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setScene(modalScene);

            Scene mainScene = obligationTable.getScene();
            mainScene.getRoot().setEffect(new GaussianBlur(5));

            // desactivé le flou quand on ferme le modal
            modalStage.setOnHidden(event -> mainScene.getRoot().setEffect(null));

            modalStage.showAndWait();
            chargerObligations();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            showAlert("Erreur lors de l'affichage de la modal.");
        }
    }


    @FXML
    private void handleAjouter() {
        afficherModal("Ajouter une Obligation", null);
    }


    @FXML
    private void handleModifier() {
        ObligationFiscale selected = obligationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            afficherModal("Modifier une Obligation", selected);
        } else {
            showAlert("Sélectionnez une obligation à modifier.");
        }
    }


    @FXML
    private void handleSupprimer() {
        ObligationFiscale selected = obligationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            obligationFiscaleDao.deleteObligationFiscale(selected.getId());
            chargerObligations();
        } else {
            showAlert("Sélectionnez une obligation à supprimer.");
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void prefillFormulaire(ObligationFiscale obligation) {
        // Préremplir les champs
        amountField.setText(String.valueOf(obligation.getAmount()));
        echeancePicker.setValue(obligation.getDateEchance().atZone(ZoneId.systemDefault()).toLocalDate());
        typeImpot.setValue(obligation.getTypeImpot());
        this.obligationEnCours = obligation;
    }





    @FXML
    private void validerFormulaire() {
        try {
            double montant = Double.parseDouble(amountField.getText());
            LocalDate localDate = echeancePicker.getValue();
            Instant echeance = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            String type = typeImpot.getValue();


            User user = new User();
            user.setId(1L); // Simule un utilisateur existant

            if (obligationEnCours == null) {
                // Création
                service.enregistrerObligation(echeance, montant, type, user);
            } else {
                // Modification
                obligationEnCours.setAmount(montant);
                obligationEnCours.setDateEchance(echeance);
                obligationEnCours.setTypeImpot(type);
                obligationEnCours.setUser(user);
                obligationFiscaleDao.updateObligationFiscale(obligationEnCours);
            }

            showAlert("✅ Obligation enregistrée !");
            // fermer le modal
            Stage stage = (Stage) amountField.getScene().getWindow();
            stage.close();
        }
        catch (NumberFormatException e)
        {
            showAlert("Montant invalide !");
        }
        catch (Exception e)
        {
            showAlert("Erreur : " + e.getMessage());
            System.out.println("Voici le bug"+e.getMessage());
            System.err.println("Voici le bug"+e.getMessage());
            e.printStackTrace();
        }
    }


}
