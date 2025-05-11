package fr.koumare.comptease.controllers;

import fr.koumare.comptease.dao.ClientDao;
import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.User;
import fr.koumare.comptease.service.ClientService;
import fr.koumare.comptease.service.impl.ClientServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;




public class ClientController extends BaseController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initialisation de  ClientController");
        affiche();
    }

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
    User user = new User();
    @FXML
    private BorderPane chartContainer;
    @FXML
    private Label clientLabel;
    @FXML
    private TextField addContact;

    @FXML
    private Button addExecuter;

    @FXML
    private Button addEffacer;

    @FXML
    private Button addRetour;

    @FXML
    private TextField addNom;

    @FXML
    private TextField addPrenom;

    @FXML
    private TextField addSolde;

    @FXML
    private TextField addAdresse;

    @FXML
    private TextField addId;

    @FXML
    private ComboBox<?> addStatut;

    @FXML
    private Button ajouter;

    @FXML
    private Button chercher;

    @FXML
    private ComboBox<?> choixTP;

    @FXML
    private Button close;

    @FXML
    private TableColumn<?, ?> contactc;

    @FXML
    private AnchorPane formInitial_h;


    @FXML
    private AnchorPane form_add;

    @FXML
    private AnchorPane form_initial;

    @FXML
    private AnchorPane form_modif;

    @FXML
    private TableColumn<Client, Integer> idc;

    @FXML
    private AnchorPane main_form;

    @FXML
    private Button minimize;

    @FXML
    private TextField modifContact;

    @FXML
    private Button modifExecuter;

    @FXML
    private Label modifId;

    @FXML
    private TextField modifNom;

    @FXML
    private TextField modifPrenom;

    @FXML
    private TextField modifSolde;

    @FXML
    private TextField modifAdresse;

    @FXML
    private Button modifRetour;

    @FXML
    private Button modifier;

    @FXML
    private TableColumn<Client, String> nomc;

    @FXML
    private TableColumn<Client, String> prenomc;

    @FXML
    private TextField searchBarre;

    @FXML
    private TableColumn<Client, Long> soldec;

    @FXML
    private TableColumn<Client, String> adressec;

    @FXML
    private TableView<Client> tableClient;
    private ClientService clientService = new ClientServiceImpl();

    @FXML
    public void close(){
        System.exit(0);
    }
    public void minimize(){
        Stage stage= (Stage)main_form.getScene().getWindow();
        stage.setIconified(true);
    }
    public void switchForm(ActionEvent event){
        if(event.getSource()==ajouter){
            formInitial_h.setVisible(false);
            form_modif.setVisible(false);
            form_add.setVisible(true);
        }
        else if(event.getSource()==modifier){
            formInitial_h.setVisible(false);
            form_modif.setVisible(true);
            form_add.setVisible(false);
        }
        else if(event.getSource()==addRetour){
            formInitial_h.setVisible(true);
            form_modif.setVisible(false);
            form_add.setVisible(false);
        }
        else if(event.getSource()==modifRetour){
            formInitial_h.setVisible(true);
            form_modif.setVisible(false);
            form_add.setVisible(false);
        }
        
        /*else if(event.getSource()==modifExecuter){
            formInitial_h.setVisible(true);
            form_modif.setVisible(false);
            form_add.setVisible(false);
        }
        else if(event.getSource()==addExecuter){
            formInitial_h.setVisible(true);
            form_modif.setVisible(false);
            form_add.setVisible(false);
        }*/
    }

    // Affiche la liste des clients dans le tableau
    @FXML
    private void affiche() {
        logger.info("Affichage de la liste des clients");
        ObservableList<Client> list = FXCollections.observableArrayList(clientService.getAllClients());
        idc.setCellValueFactory(new PropertyValueFactory<>("Idc"));
        nomc.setCellValueFactory(new PropertyValueFactory<>("LastName"));
        prenomc.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
        adressec.setCellValueFactory(new PropertyValueFactory<>("Adresse"));
        soldec.setCellValueFactory(new PropertyValueFactory<>("Solde"));
        contactc.setCellValueFactory(new PropertyValueFactory<>("Contact"));
        tableClient.setItems(list);

        tableClient.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        if (newSelection != null) {
            remplirFormulaireModif(newSelection);
        }
        });

    }
    private void remplirFormulaireModif(Client client) {
        modifId.setText(String.valueOf(client.getIdc())); 
        modifNom.setText(client.getLastName());
        modifPrenom.setText(client.getFirstName());
        modifAdresse.setText(client.getAdresse());
        modifContact.setText(client.getContact());
        modifSolde.setText(String.valueOf(client.getSolde()));
    }


    // Ajoute un client
    @FXML
    private void AjoutClient(ActionEvent event) {
        String contact=addContact.getText();
        String prenom=addPrenom.getText();
        String nom=addNom.getText();
        String adresse=addAdresse.getText();
        Long solde=Long.parseLong(addSolde.getText());
        logger.info("Ajout d'un client : {}", nom +" "+ prenom+ " "+ adresse + " "+ contact + " "+ solde);
        if(clientService.addClient(nom, prenom, adresse, contact,3L,solde)){
            logger.info("Client ajouté : {}", nom +" "+ prenom);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Client ajouté avec succès.");

            //recuperer le client ajouté
            Optional<Client> addedClient = clientService.findByNames(nom, prenom);
            if(addedClient.isPresent()) {
                Client newClient = addedClient.get();
                logger.info("Client ajouté : {}", newClient.getFirstName());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Client ajouté avec succès : " + newClient.getFirstName() +" " + newClient.getLastName());
                affiche();
                EffacerChamps(event);
            } else {
                logger.error("Erreur lors de la récupération du client ajouté : {}", nom+" " + prenom);
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération du client ajouté.");
            }
        } else {
            logger.error("Erreur lors de l'ajout du client : {}", nom+" " + prenom);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du client.");
        }
    }

    // Modifie un client
    @FXML
    private void ModifClient(ActionEvent event) {
        String nom=modifNom.getText();
        String prenom=modifPrenom.getText();
        String adresse=modifAdresse.getText();
        String contact=modifContact.getText();
        Long solde= Long.parseLong(modifSolde.getText());
        Long id=Long.parseLong(modifId.getText());
    
        logger.info("Modification d'un client : {}", nom +" "+ prenom);

        if(clientService.updateClient(id,nom, prenom, adresse, contact, solde)){
            logger.info("Client modifié : {}", nom +" "+ prenom);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Client modifié avec succès.");
            //recuperer le client modifié
            Optional<Client> updatedClient = clientService.findByNames(nom, prenom);
            if(updatedClient.isPresent()) {
                Client newClient = updatedClient.get();
                logger.info("Client modifié : {}", newClient.getFirstName());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Client modifié avec succès : " + newClient.getFirstName() +" " + newClient.getLastName());
                affiche();
            } else {
                logger.error("Erreur lors de la récupération du client modifié : {}", nom+" " + prenom);
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération du client modifié.");
            }
        } else {
            logger.error("Erreur lors de la modification du client : {}", nom+" " + prenom);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification du client.");
        }
    }

    // Supprime un client
    /*@FXML
        private void SupprClient(ActionEvent event) {
        
        logger.info("Suppression d'un client : {}", client.getFirstName());

        // Vérification de l'existence du client avant la suppression
        if(clientService.deleteClient(client.getIdc())){
            logger.info("Client supprimé : {}", client.getFirstName());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Client supprimé avec succès.");
        } else {
            logger.error("Erreur lors de la suppression du client : {}", client.getFirstName());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression du client.");
        }
    }*/
    private void showAlert(Alert.AlertType type, String title, String message) {
        logger.info("Affichage d'une alerte : {} - {}", title, message);
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    
    //vider les champs
    @FXML
    private void EffacerChamps(ActionEvent event) {
        logger.info("Effacement des champs");
        addContact.clear();
        addNom.clear();
        addPrenom.clear();
        addAdresse.clear();
        addSolde.clear();
        modifNom.setText("");
        modifPrenom.setText("");
        modifAdresse.setText("");
        modifContact.setText("");
        modifSolde.setText("");

    }

}