package fr.koumare.comptease.controllers;

import fr.koumare.comptease.dao.ClientDao;
import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.Company;
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

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;




public class ClientController extends BaseController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initialisation de  ClientController");
    }

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

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
    private Label modifContact;

    @FXML
    private Button modifExecuter;

    @FXML
    private TextField modifId;

    @FXML
    private Label modifNom;

    @FXML
    private Label modifPrenom;

    @FXML
    private Label modifSolde;

    @FXML
    private Label modifAdresse;


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
    private ClientDao cl=new ClientDao();
    private Connection connect;
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
        idc.setCellValueFactory(new PropertyValueFactory<>("idc"));
        nomc.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        prenomc.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        adressec.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        soldec.setCellValueFactory(new PropertyValueFactory<>("solde"));
        contactc.setCellValueFactory(new PropertyValueFactory<>("contact"));
        tableClient.setItems(list);
    }

    // Ajoute un client
    @FXML
    private void AjoutClient(ActionEvent event, Client client) {
        System.out.println("Ajout d'un client");
        if(clientService.addClient(client)){
            logger.info("Client ajouté : {}", client.getFirstName());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Client ajouté avec succès.");
        } else {
            logger.error("Erreur lors de l'ajout du client : {}", client.getFirstName());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du client.");
        }
    }

    // Modifie un client
    @FXML
    private void ModifClient(ActionEvent event,Client client) {
        if(clientService.updateClient(client)){
            logger.info("Client modifié : {}", client.getFirstName());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Client modifié avec succès.");
        } else {
            logger.error("Erreur lors de la modification du client : {}", client.getFirstName());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification du client.");
        }
    }

    // Supprime un client
    @FXML
    private void SupprClient(ActionEvent event,Client client) {
        if(clientService.deleteClient(client.getIdc())){
            logger.info("Client supprimé : {}", client.getFirstName());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Client supprimé avec succès.");
        } else {
            logger.error("Erreur lors de la suppression du client : {}", client.getFirstName());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression du client.");
        }
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