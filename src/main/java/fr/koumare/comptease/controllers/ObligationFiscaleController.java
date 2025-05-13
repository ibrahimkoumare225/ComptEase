package fr.koumare.comptease.controllers;



import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import fr.koumare.comptease.dao.ObligationFiscaleDao;

import fr.koumare.comptease.model.ObligationFiscale;
import fr.koumare.comptease.model.User;
import fr.koumare.comptease.service.impl.ObligationFiscaleServiceImpl;;

public class ObligationFiscaleController {

    @FXML private TextField amountField;
    @FXML private DatePicker echeancePicker;
    @FXML private ComboBox<String> typeImpot;
    private ObligationFiscale obligationEnCours;


    @FXML private TableView<ObligationFiscale> obligationTable;
    @FXML private TableColumn<ObligationFiscale, Long> idColumn;
    @FXML private TableColumn<ObligationFiscale, Double> montantColumn;
    @FXML private TableColumn<ObligationFiscale, String> typeColumn;
    @FXML private TableColumn<ObligationFiscale, Instant> echeanceColumn;

    private final ObligationFiscaleServiceImpl service = new ObligationFiscaleServiceImpl();   
    private final ObligationFiscaleDao obligationFiscaleDao = new ObligationFiscaleDao();
    //private final UserDao userDao = new UserDao();

    @FXML
    public void initialize() {

        if(typeImpot != null)
        {
            typeImpot.getItems().addAll("TVA", "IR", "IS");

            // pour ne permettre que des doubles
            amountField.setTextFormatter(new TextFormatter<>(change -> {
             String newText = change.getControlNewText();
             if (newText.matches("\\d*(\\.\\d*)?")) { // Seulement les chiffres et le séparateur décimal
            return change;
        }
            return null; // Rejeter la modification
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
            ouvrirVueListe(); // redirection

        } catch (NumberFormatException e) {
            showAlert("Montant invalide !");
        } catch (Exception e) {
            showAlert("Erreur : " + e.getMessage());
            System.out.println("Voici le bug"+e.getMessage());
            System.err.println("Voici le bug"+e.getMessage());
            e.printStackTrace();
        }
    }



        private void ouvrirVueListe() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/koumare/comptease/fxml/liste_obligation.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) amountField.getScene().getWindow();
        stage.setScene(scene);
    }

    private void chargerObligations() {
        List<ObligationFiscale> obligations = obligationFiscaleDao.getAllObligationFiscale();
        ObservableList<ObligationFiscale> observableList = FXCollections.observableArrayList(obligations);
        obligationTable.setItems(observableList);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    @FXML
    private void handleSupprimer() {
    ObligationFiscale selected = obligationTable.getSelectionModel().getSelectedItem();
    if (selected != null) {
        obligationFiscaleDao.deleteObligationFiscale(selected.getId());
        chargerObligations(); // recharge la page
    } else {
        showAlert("Sélectionnez une obligation à supprimer.");
    }
}

@FXML
private void handleModifier() {
    // Récupérer l'obligation sélectionnée
    ObligationFiscale selected = obligationTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
        showAlert("Veuillez sélectionner une obligation à modifier.");
        return;
    }

 
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/koumare/comptease/fxml/auth.fxml"));
        Parent root = loader.load();

        // Récupérer le contrôleur 
        ObligationFiscaleController controller = loader.getController();

        // passage l'obligation à modifier au contrôleur
        controller.prefillFormulaire(selected);

        Stage stage = (Stage) obligationTable.getScene().getWindow();
        stage.setScene(new Scene(root));
    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Erreur lors du chargement du formulaire.");
    }
}



public void prefillFormulaire(ObligationFiscale obligation) {
    // Préremplir les champs
    amountField.setText(String.valueOf(obligation.getAmount()));
    echeancePicker.setValue(obligation.getDateEchance().atZone(ZoneId.systemDefault()).toLocalDate());
    typeImpot.setValue(obligation.getTypeImpot());

    // mise à jour de l'obligation
    this.obligationEnCours = obligation;
}

}
