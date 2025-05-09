package fr.koumare.comptease.controllers;

import fr.koumare.comptease.dao.ClientDao;
import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.Company;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;


import static fr.koumare.comptease.utilis.DB.conDB;


public class ClientController extends BaseController implements Initializable {

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



    @FXML
    public void initialize(){
        affiche();
        addClientShow();

    }
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
        else if(event.getSource()==modifExecuter){
            formInitial_h.setVisible(true);
            form_modif.setVisible(false);
            form_add.setVisible(false);
        }
        else if(event.getSource()==addExecuter){
            formInitial_h.setVisible(true);
            form_modif.setVisible(false);
            form_add.setVisible(false);
        }
    }

    public ObservableList<Client> addClient(){
        ObservableList<Client> list = FXCollections.observableArrayList();
        String sql="select * from Client";
        connect = conDB();
        try{
            PreparedStatement ps = connect.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Client c =new Client(rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(7),
                        rs.getLong(6)
                );
                list.add(c);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    private ObservableList<Client> addClientList;
    public void addClientShow(){
        addClientList=addClient();
        idc.setCellValueFactory(new PropertyValueFactory<>("idc"));
        nomc.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        prenomc.setCellValueFactory(new PropertyValueFactory<>("LastName"));
        contactc.setCellValueFactory(new PropertyValueFactory<>("contact"));
        adressec.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        soldec.setCellValueFactory(new PropertyValueFactory<>("solde"));

        tableClient.setItems(addClientList);
    }

    public void addClientSelect(){
        Company company = new Company();
        Client client = tableClient.getSelectionModel().getSelectedItem();
        if(client != null){
            addId.setText(String.valueOf(client.getIdc()));
            addNom.setText(client.getFirstName());
            addPrenom.setText(client.getLastName());
            addContact.setText(client.getContact());
            addAdresse.setText(client.getAdresse());
            addSolde.setText(String.valueOf(client.getSolde()));
        }
        String sql="INSERT INTO Client (idc, id_user, firstName, lastName, contact, adresse, solde,user_id) VALUES (?, ?, ?, ?, ?, ?, ?,?)";
        connect = conDB();
        try{
            if(addId.getText().isEmpty() || addNom.getText().isEmpty() || addPrenom.getText().isEmpty() || addAdresse.getText().isEmpty() || addSolde.getText().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Champs vides");
                alert.setContentText("Veuillez remplir tous les champs");
                alert.showAndWait();
            }
            else{
                PreparedStatement ps = connect.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(addId.getText()));
                ps.setInt(2, company.getId().intValue());
                ps.setString(3, addNom.getText());
                ps.setString(4, addPrenom.getText());
                ps.setString(5, addContact.getText());
                ps.setString(6, addAdresse.getText());
                ps.setLong(7, Long.parseLong(addSolde.getText()));
                ps.setInt(8, Integer.parseInt(addId.getText()));

                ps.executeUpdate();
            }
            PreparedStatement ps = connect.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(addId.getText()));
            ps.setInt(2, company.getId().intValue());
            ps.setString(3, addNom.getText());
            ps.setString(4, addPrenom.getText());
            ps.setString(5, addContact.getText());
            ps.setString(6, addAdresse.getText());
            ps.setLong(7, Long.parseLong(addSolde.getText()));
            ps.setInt(8,company.getId().intValue());


            ps.executeUpdate();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur d'insertion dans la base de données");
        }
    }

    public void addClientEffacer(){
        addId.setText("");
        addNom.setText("");
        addPrenom.setText("");    
        addContact.setText("");
        addAdresse.setText("");
        addSolde.setText("");
    }

    protected void affiche(){
        try {
            StringBuilder clientInfo = new StringBuilder();

            cl.getAllClients().forEach(client -> {
                clientInfo.append("IDC: ").append(client.getIdc())
                        .append("IDUser: ").append(client.getIdUser())
                        .append(", Nom: ").append(client.getLastName())
                        .append(", Prénom: ").append(client.getFirstName())
                        .append(", Contact: ").append(client.getContact())
                        .append("\n");
            });

            clientLabel.setText(clientInfo.toString());
            System.out.println(clientLabel.getText());
        } catch (Exception e) {
            e.printStackTrace();  // Affiche l'exception dans la console
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initialisation de ClientController");
        // Add client-specific initialization here
    }


}