package fr.koumare.comptease.controllers;

import fr.koumare.comptease.model.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import fr.koumare.comptease.dao.ClientDao;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static fr.koumare.comptease.utilis.DB.conDB;


public class ClientPageController {
    @FXML
    private Label clientLabel;
    @FXML
    private TextField addContact;

    @FXML
    private Button addExecuter;

    @FXML
    private TextField addNom;

    @FXML
    private TextField addPrenom;

    @FXML
    private TextField addSolde;

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
    //private Statement s;
    //private PreparedStatement ps;
    //private ResultSet rs;

    /* 
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
            formInitial_h.setVisible(false);
            form_modif.setVisible(true);
            form_add.setVisible(false);
        }
        else if(event.getSource()==addExecuter){
            formInitial_h.setVisible(false);
            form_modif.setVisible(false);
            form_add.setVisible(true);
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
    }*/
}
