package fr.koumare.comptease.controllers;

import fr.koumare.comptease.dao.ClientDao;
import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.User;
import fr.koumare.comptease.service.ClientService;
import fr.koumare.comptease.service.impl.ClientServiceImpl;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
    private Button annulerRecherche;

    @FXML
    private ComboBox<String> choixTP;

    @FXML
    private Button close;

    @FXML
    private TableColumn<?, ?> noteUserc;

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
    private TableColumn<Client, String> nomc;

    @FXML
    private TableColumn<Client, String> prenomc;

    @FXML
    private TableColumn<Client, String> contactc;

    @FXML
    private TextField searchBarre;

    @FXML
    private TableColumn<Client, Long> soldec;

    @FXML
    private TableColumn<Client, Void> detailc;

    @FXML
    private TableView<Client> tableClient;
    private ClientService clientService = new ClientServiceImpl();

    ObservableList<Client> listAllClients= FXCollections.observableArrayList(clientService.getAllClients());
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initialisation de  ClientController");
        detailc.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(null));
        detailc.setCellFactory(param-> new TableCell<Client, Void>() {
            final Button btn = new Button("Voir");
            {btn.setOnAction(event->{
                Client client = getTableView().getItems().get(getIndex());
                logger.info("Affichage des détails du client : {}", client.getFirstName());
            });}
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        affiche(listAllClients);

    }

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
    private void affiche(ObservableList<Client> list) {
        logger.info("Affichage de la liste des clients");
        idc.setCellValueFactory(new PropertyValueFactory<>("Idc"));
        nomc.setCellValueFactory(new PropertyValueFactory<>("LastName"));
        prenomc.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
        soldec.setCellValueFactory(new PropertyValueFactory<>("Solde"));
        noteUserc.setCellValueFactory(new PropertyValueFactory<>("Adresse"));
        contactc.setCellValueFactory(new PropertyValueFactory<>("Contact"));
        tableClient.setItems(list);
        //mofication de la ligne selectionnée
        tableClient.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        if (newSelection != null) {
            formInitial_h.setVisible(false);
            form_modif.setVisible(true);
            form_add.setVisible(false);
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
        if(clientService.addClient(nom, prenom, adresse, contact,1L,solde)){
            logger.info("Client ajouté : {}", nom +" "+ prenom);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Client ajouté avec succès.");

            //recuperer le client ajouté
            Optional<Client> addedClient = clientService.findByNames(nom, prenom);
            if(addedClient.isPresent()) {
                Client newClient = addedClient.get();
                logger.info("Client ajouté : {}", newClient.getFirstName());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Client ajouté avec succès : " + newClient.getFirstName() +" " + newClient.getLastName());
                listAllClients=FXCollections.observableArrayList(clientService.getAllClients());
                affiche(listAllClients);
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
                listAllClients=FXCollections.observableArrayList(clientService.getAllClients());
                affiche(listAllClients);
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
    @FXML
        private void SupprClient(ActionEvent event) {
            Client client = tableClient.getSelectionModel().getSelectedItem();
            if (client == null) {
                logger.warn("Aucun client sélectionné pour la suppression");
                showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez sélectionner un client à supprimer.");
                return;
            }
            logger.info("Suppression d'un client : {}", client.getFirstName());

            // Vérification de l'existence du client avant la suppression
            if(clientService.deleteClient(client.getIdc())){
                logger.info("Client supprimé : {}", client.getFirstName());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Client supprimé avec succès.");
                //verifier si le client a été supprimé
                Optional<Client> deletedClient = clientService.findById(client.getIdc());
                if(deletedClient.isEmpty()) {
                    logger.info("Client supprimé : {}", client.getFirstName());
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Client supprimé avec succès : " + client.getFirstName() +" " + client.getLastName());
                    listAllClients=FXCollections.observableArrayList(clientService.getAllClients());
                    affiche(listAllClients);
                } else {
                    logger.error("Erreur lors de la récupération du client supprimé : {}", client.getFirstName());
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération du client supprimé.");
                }
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


    //une recherche par mot clé
    @FXML
    private void rechercheClient(ActionEvent event) {
        String keyword = searchBarre.getText();
        logger.info("Recherche d'un client : {}", keyword);
        if (keyword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez entrer un mot clé pour la recherche.");
            return;
        }
        ObservableList<Client> list = FXCollections.observableArrayList(clientService.findByKeyword(keyword));
        idc.setCellValueFactory(new PropertyValueFactory<>("Idc"));
        nomc.setCellValueFactory(new PropertyValueFactory<>("LastName"));
        prenomc.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
        soldec.setCellValueFactory(new PropertyValueFactory<>("Solde"));
        noteUserc.setCellValueFactory(new PropertyValueFactory<>("Adresse"));
        detailc.setCellValueFactory(new PropertyValueFactory<>("Contact"));
        tableClient.setItems(list);
        if (list.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Avertissement", "Aucun client trouvé avec ce mot clé.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Clients trouvés : " + list.size());
            affiche(list);
            annulerRecherche.setVisible(true);
        }

    }

    //annuler la recherche
    @FXML
    private void annulerRecherche(ActionEvent event) {
        logger.info("Annulation de la recherche");
        searchBarre.clear();
        listAllClients=FXCollections.observableArrayList(clientService.getAllClients());
        affiche(listAllClients);
        annulerRecherche.setVisible(false);
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

    boolean isSortedById = true;
    boolean isSortedByName = false;
    boolean isSortedByFirstName = false;
    //trier par id
    private void trierParId() {
        logger.info("Tri par ID");
        ObservableList<Client> sortedList ;//= FXCollections.observableArrayList(clientService.sortById());
        if(isSortedById){
            logger.info("Tri des clients par ID decroissant");
            sortedList = FXCollections.observableArrayList(clientService.sortByIdDesc());
            isSortedById = false;
        }
        else{
            logger.info("Tri des clients par ID croissant");
            sortedList = FXCollections.observableArrayList(clientService.sortById());
            isSortedById = true;
        }
        affiche(sortedList);
    }

    //trier par nom
    private void trierParNom() {
        logger.info("Tri par Nom");
        ObservableList<Client> sortedList ;
        if(isSortedByName){
            logger.info("Tri des clients par nom decroissant");
            sortedList = FXCollections.observableArrayList(clientService.sortByNameDesc());
            isSortedByName = false;
        }
        else{
            logger.info("Tri des clients par nom croissant");
            sortedList = FXCollections.observableArrayList(clientService.sortByName());
            isSortedByName = true;
        }
        affiche(sortedList);
    }

    //trier par prenom
    private void trierParPrenom() {
        logger.info("Tri par Prenom");
        ObservableList<Client> sortedList ;
        if(isSortedByFirstName){
            logger.info("Tri des clients par prenom decroissant");
            sortedList = FXCollections.observableArrayList(clientService.sortByFirstNameDesc());
            isSortedByFirstName = false;
        }
        else{
            logger.info("Tri des clients par prenom croissant");
            sortedList = FXCollections.observableArrayList(clientService.sortByFirstName());
            isSortedByFirstName = true;
        }
        affiche(sortedList);
    }

}