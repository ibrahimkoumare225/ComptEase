package fr.koumare.comptease.controllers;

import fr.koumare.comptease.dao.ClientDao;
import fr.koumare.comptease.model.Article;
import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.User;
import fr.koumare.comptease.model.enumarated.StatusInvoice;
import fr.koumare.comptease.model.Invoice;
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
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import org.hibernate.mapping.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Hibernate;



import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
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
    private TextField addNote;

    @FXML
    private TextField addSiret;

    @FXML
    private TextField addRib;

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
    private AnchorPane add;

    @FXML
    private AnchorPane form_initial;

    @FXML
    private AnchorPane form_modif;  

    @FXML
    private AnchorPane form_add;

    @FXML
    private TableColumn<Client, Integer> idc;

    @FXML
    private AnchorPane main_form;

    @FXML
    private Button minimize;

    /*@FXML
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
    private Button modifRetour;*/


    @FXML
    private TableColumn<Client, String> nomc;

    @FXML
    private TableColumn<Client, String> prenomc;

    @FXML
    private TableColumn<Client, String> contactc;

    @FXML
    private TextField searchBarre;

    @FXML
    private TableColumn<Client, Double> soldec;

    @FXML
    private TableColumn<Client, Void> detailc;

    @FXML
    private TableView<Client> tableClient;

    @FXML
    private AnchorPane form_detailClient;

    @FXML
    private AnchorPane formInitial_hDetail;

    @FXML
    private TableView<Invoice> tableClientDetail;

    @FXML
    private TableColumn<Invoice, Long> idp;

    @FXML
    private TableColumn<Invoice, String> desp;

    @FXML
    private TableColumn<Invoice, LocalDate> datep;

    @FXML
    private TableColumn<Article, Integer> quantitep;

    @FXML
    private TableColumn<Invoice, Double> prixUp;

    @FXML
    private TableColumn<Invoice, Double> prixTp;

    @FXML
    private TableColumn<Invoice, StatusInvoice> Statutp;


    @FXML
    private Button chercherDetail;

    @FXML
    private Button annulerRechercheDetail;
    
    @FXML
    private TextField searchBarreDetail;

    @FXML 
    private Button RetourDetail;

    @FXML
    private AnchorPane form_modifDetail;

    @FXML
    private Button envoyerRappel;

    @FXML
    private Button modifRetourDetail;

    @FXML
    private Button voirDevis;

    @FXML
    private TextField modifNote;

    @FXML
    private TextField modifNoteClient;


    @FXML
    private Label modifStatut;

    @FXML
    private Label modifIdDetail;

    @FXML
    private Button modifNoteExecuter;


    

    private ClientService clientService = new ClientServiceImpl();

    ObservableList<Client> listAllClients= FXCollections.observableArrayList(clientService.getAllClients());
    ObservableList<Invoice> listAllInvoices;
    Invoice selectInvoice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initialisation de  ClientController");
        detailc.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(null));
        detailc.setCellFactory(param-> new TableCell<Client, Void>() {
            final Button btn = new Button("Voir");
            {
                btn.getStyleClass().add("btnVoirDetails");
                btn.setOnAction(event->{
                int index= getIndex();
                ObservableList<Client> items = getTableView().getItems();
                if(index >= 0  && index < items.size()){
                    Client client = items.get(index);
                    logger.info("Affichage des détails du client : {}", client.getFirstName());
                try {
                    listAllInvoices = FXCollections.observableArrayList(clientService.getClientDetails(client.getIdc()));
                    logger.info("Détails du client : {}", client.getIdc());
                    showClientDetails(client,listAllInvoices);


                } catch (IOException e) {
                    logger.error("Erreur lors de l'affichage des détails du client : {}", e.getMessage());
                }
                } 
                
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
    @FXML
    private void switchForm(ActionEvent event) {
        if(event.getSource()==ajouter){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/koumare/comptease/fxml/addClientForm.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = new Stage();
                stage.setTitle("Nouveau Client");
                stage.setScene(scene);
                
                AddClientFormController controller = loader.getController();
                controller.setParentController(this);
                
                stage.show();
            } catch (IOException e) {
                logger.error("Erreur lors de l'ouverture du formulaire d'ajout de client : {}", e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire d'ajout de client.");
            }
        }
        else if(event.getSource()==addRetour){
            formInitial_h.setVisible(true);
            form_modif.setVisible(false);
            form_add.setVisible(false);
        }
        /*else if(event.getSource()==modifRetour){
            formInitial_h.setVisible(true);
            form_modif.setVisible(false);
            form_add.setVisible(false);
        }*/
        else if(event.getSource()==RetourDetail){
            form_initial.setVisible(true);
            form_modifDetail.setVisible(false);
            form_detailClient.setVisible(false);
        }
        else if (event.getSource()==modifRetourDetail){
            form_detailClient.setVisible(true);
            formInitial_hDetail.setVisible(true);
            form_modifDetail.setVisible(false);
            form_initial.setVisible(false);
            tableClientDetail.getSelectionModel().clearSelection();

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
        noteUserc.setCellValueFactory(new PropertyValueFactory<>("Note"));
        contactc.setCellValueFactory(new PropertyValueFactory<>("Contact"));
        tableClient.setItems(list);
        //mofication de la ligne selectionnée
        tableClient.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        if (newSelection != null ) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/koumare/comptease/fxml/modifClientForm.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = new Stage();
                stage.setTitle("Votre Client");
                stage.setScene(scene);
                
                ModifClientFormController controller = loader.getController();
                controller.setParentController(this);
                controller.setClientData(newSelection);
                
                
                stage.show();
            } catch (IOException e) {
                logger.error("Erreur lors de l'ouverture du formulaire de modif de client : {}", e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire de modif du client.");
            }
        }
        });

    }
    


    // Ajoute un client
    //voir AddClientFormController

    // Modifie un client
    //voir ModifClientFormController

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
                    EffacerChamps(event);
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
        noteUserc.setCellValueFactory(new PropertyValueFactory<>("Note"));
        contactc.setCellValueFactory(new PropertyValueFactory<>("Contact"));

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
        addNote.clear();
        addSiret.clear();
        addRib.clear();
        
    }

   

    

    //afficher les details d'un client
    private void showClientDetails(Client client, ObservableList<Invoice> list) throws IOException {
        logger.info("Affichage des détails du client : {}");

        form_initial.setVisible(false);
        form_detailClient.setVisible(true);
        formInitial_hDetail.setVisible(true);
        form_modifDetail.setVisible(false);

        idp.setCellValueFactory(new PropertyValueFactory<>("id"));
        desp.setCellValueFactory(new PropertyValueFactory<>("descriptionArticle"));
        datep.setCellValueFactory(new PropertyValueFactory<>("date"));
        quantitep.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        prixUp.setCellValueFactory(cellData -> {
            Invoice invoice = cellData.getValue();
            double price = clientService.getArticlePrice(invoice.getId());
            return new ReadOnlyObjectWrapper<>(price);
        });
        prixTp.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        Statutp.setCellValueFactory(new PropertyValueFactory<>("status"));
        if (list.isEmpty()) {
            tableClientDetail.setItems(FXCollections.observableArrayList()); // vider la table
            showAlert(Alert.AlertType.INFORMATION, "Avertissement", "Aucun détail trouvé pour ce client.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Détails trouvés : " + list.size());
            tableClientDetail.setItems(list);
            tableClientDetail.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    formInitial_hDetail.setVisible(false);
                    form_modifDetail.setVisible(true);
                    selectInvoice = newSelection;
                    remplirFormulaireModifDetail(newSelection);
                }
            });

        }
    }

    @FXML
    private void remplirFormulaireModifDetail(Invoice invoice) {
        modifIdDetail.setText(String.valueOf(invoice.getId()));
        modifNote.setText(invoice.getDescription());
        modifStatut.setText(invoice.getStatus().toString());
        if(invoice.getStatus() == StatusInvoice.UNPAID) {
            envoyerRappel.setVisible(true);
        }
        
    }

    @FXML
    private void rechercheDetail(ActionEvent event) {
        logger.info("Recherche d'un detail du client : {}", searchBarreDetail.getText());
        String keyword = searchBarreDetail.getText();
        if (keyword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Veuillez entrer un mot clé pour la recherche.");
            return;
        }
        ObservableList<Invoice> list = FXCollections.observableArrayList(clientService.findByKeywordDetails(keyword));
        idp.setCellValueFactory(new PropertyValueFactory<>("id"));
        desp.setCellValueFactory(new PropertyValueFactory<>("descriptionArticle"));
        datep.setCellValueFactory(new PropertyValueFactory<>("date"));
        quantitep.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        prixUp.setCellValueFactory(new PropertyValueFactory<>("price"));
        prixTp.setCellValueFactory(cellData -> {
            Invoice invoice = cellData.getValue();
            double total = invoice.getPrice() * invoice.getQuantity(); //
            return new ReadOnlyObjectWrapper<>((Double) total);
        });
        Statutp.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableClientDetail.setItems(list);
        Client c= clientService.findById(list.get(0).getClient().getIdc()).orElse(null); 
        try {
            showClientDetails(c, list);
            annulerRechercheDetail.setVisible(true);
        } catch (IOException e) {
            logger.error("Erreur lors de l'affichage des détails du client : {}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'affichage des détails du client.");
        }
       
    }

    @FXML
    private void fctannulerRechercheDetail(ActionEvent event) {
        logger.info("Annulation de la recherche de detail");
        searchBarreDetail.clear();
        Client c= clientService.findById(listAllInvoices.get(0).getClient().getIdc()).orElse(null);
        try {
            showClientDetails(c, listAllInvoices);
            annulerRechercheDetail.setVisible(false);
        } catch (IOException e) {
            logger.error("Erreur lors de l'annulation de la recherche de détail : {}", e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'annulation de la recherche de détail.");
        }
        
    }
    @FXML
    private void  ModifDetail(ActionEvent event) {
        logger.info("Modification d'un detail du client : {}", modifIdDetail.getText());
        String note=modifNote.getText();
        //fonction update invoice
        if(clientService.modifDescriptionFacture(Long.parseLong(modifIdDetail.getText()), note)){
            logger.info("Détail modifié : {}", note);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Détail modifié avec succès.");
            //recuperer l'id du client
            Long idClient = clientService.findClientByInvoiceId(Long.parseLong(modifIdDetail.getText())).orElse(null).getIdc();
            listAllInvoices = FXCollections.observableArrayList(clientService.getClientDetails(idClient));
            try {
                Client c= clientService.findById(idClient).orElse(null);
                showClientDetails(c, listAllInvoices);
                form_modifDetail.setVisible(false);
                form_detailClient.setVisible(true);
                formInitial_hDetail.setVisible(true);
            } catch (IOException e) {
                logger.error("Erreur lors de l'affichage des détails du client : {}", e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'affichage des détails du client.");
            }

        } else {
            logger.error("Erreur lors de la modification du détail : {}", note);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification du détail.");
        }
    }

    @FXML
    private void fctVoirDevis(ActionEvent event) {
        logger.info("Affichage du devis");
        //afficher factures.fxml        
        showInvoiceDetails(selectInvoice);
    }

    @FXML
    private void fctEnvoyerRappel(ActionEvent event) {
        logger.info("Envoi d'un rappel");
        //envoyer un mail
        /*if(clientService.envoyerRappel(Long.parseLong(modifIdDetail.getText()))){
            logger.info("Rappel envoyé");*/
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Rappel envoyé avec succès à "+clientService.findClientByInvoiceId(Long.parseLong(modifIdDetail.getText())).orElse(null).getContact());
       /*  } else {
            logger.error("Erreur lors de l'envoi du rappel");
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'envoi du rappel.");
        }*/
    }

    public void refreshClientList() {
        logger.info("Rafraîchissement de la liste des clients");
        listAllClients = FXCollections.observableArrayList(clientService.getAllClients());
        affiche(listAllClients);
    }

    private void showInvoiceDetails(Invoice invoice) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/koumare/comptease/fxml/invoiceDetailPopup.fxml"));
            Parent root = loader.load();
            InvoiceDetailPopupController controller = loader.getController();
            controller.setInvoice(invoice);
            Stage stage = new Stage();
            stage.setTitle("Détail de la facture");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            logger.error("Erreur lors de l'affichage du détail de la facture : {}", e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'afficher le détail de la facture : " + e.getMessage());
        }
    }
}

