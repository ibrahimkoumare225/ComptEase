package fr.koumare.comptease.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Anchor;

import java.io.IOException;


import fr.koumare.comptease.dao.CompanyDao;
import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.model.Company;
import fr.koumare.comptease.model.User;
import fr.koumare.comptease.service.impl.UserServiceImpl;
import fr.koumare.comptease.service.impl.CompanyServiceImpl;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import java.time.LocalDate;
import javafx.scene.control.Alert;
import java.time.LocalDate;
import java.io.IOException;
import java.net.URL;
import java.text.Normalizer.Form;
import java.util.Observable;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import java.util.List;

public class ParametreController extends BaseController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(ParametreController.class);

    @FXML
    private BorderPane chartContainer;

    //AnchorPane
    @FXML private AnchorPane main_form;
    @FXML private AnchorPane FormInfoEntreprise;
    @FXML private AnchorPane FormUserEntreprise;
    @FXML private AnchorPane accueilParam;
    @FXML private AnchorPane FormAProposEntreprise;
    @FXML private AnchorPane FormNousContacterEntreprise;

    // TextField
    @FXML private TextField nomEntreprise;

    // ChoiceBox
    @FXML private ChoiceBox<String> nomCreateur;
    @FXML private ChoiceBox<String> choiceActivite;
    @FXML private ChoiceBox<String> choiceProfession;
    @FXML private ChoiceBox<String> choiceRegime;
    @FXML private ChoiceBox<String> ChoiceFormeJuridique;

    // DatePicker
    @FXML private DatePicker dateCreation;
    @FXML private DatePicker dateCloture;

    // Labels (si tu as besoin d'y accéder depuis le code)
    @FXML private Label labelNomEntreprise;
    @FXML private Label labelNomCreateur;

    @FXML private TextArea messageArea;

    // Buttons
    @FXML private Button UsersEntreprise;
    @FXML private Button informationsEntreprise;
    @FXML private Button aPropos;
    @FXML private Button nousContacter;
    
    @FXML private Button retourAc_Us;
    @FXML private Button retourAc_Info;
    @FXML private Button retourAc_NC;
    @FXML private Button retourAc_AP;
    @FXML private Button logoutButton;
    @FXML private Button btnModifier; 
    @FXML private Button envoyerMessage;

    //TableView
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> pseudoUser;
    @FXML private TableColumn<User, String> mailUser;
    @FXML private TableColumn<User, Void> actionUser;

    @FXML private AnchorPane modifInfoUser;
    @FXML private TextField modifPseudo;
    @FXML private TextField modifEmail;
    @FXML private PasswordField modifMdp;
    @FXML private PasswordField newMdp;
    @FXML private PasswordField confirmNewMdp;
    @FXML private Button modifExecuter;
    @FXML private Button retour;

    @FXML private TextField siretField;
    @FXML private TextField ribField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField capitalSocialField;
    @FXML private TextField tvaNumberField;

    CompanyServiceImpl companyService = new CompanyServiceImpl();
    UserServiceImpl userService = new UserServiceImpl();

    ObservableList<User> listAllUsers;
    ObservableList<User> selectedUser;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initialisation de ParametreController");
        
        // Initialisation des ChoiceBox
        choiceActivite.setItems(FXCollections.observableArrayList(
            "Vente de marchandises", "Prestation de services", "Mixte", "Exportation"
        ));
        
        choiceProfession.setItems(FXCollections.observableArrayList(
            "Commerce", "Artisanat", "Profession libérale", "Services", "Industrie", "Agriculture"
        ));
        
        choiceRegime.setItems(FXCollections.observableArrayList(
            "Régime réel normal", "Régime réel simplifié", "Micro-entreprise", "Franchise en base de TVA"
        ));
        
        ChoiceFormeJuridique.setItems(FXCollections.observableArrayList(
            "SARL", "SAS", "SA", "EURL", "SASU", "Auto-entrepreneur", "SCI"
        ));

        // Initialisation de la TableView
        pseudoUser.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPseudo()));
        mailUser.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEmail()));
        
        // Configuration de la colonne d'actions
        actionUser.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button btn = new Button("Modifier");
            {
                btn.getStyleClass().add("btnVoirDetails");
                btn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    logger.info("Affichage des infos de l'utilisateur : {}", user.getPseudo());
                    listAllUsers = FXCollections.observableArrayList(userService.getAllUsers());
                    modifInfoUser.setVisible(true);
                    remplirFormulaireModif(user);
                    userTable.setLayoutY(260.0);
                    userTable.setPrefHeight(359);
                });
            }
            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Afficher les informations initiales
        showInformations();
        showUsers();
    }

    @FXML
    public void close(){
        System.exit(0);
    }
    public void minimize(){
        Stage stage= (Stage)main_form.getScene().getWindow();
        stage.setIconified(true);
    }
    public void switchForm(ActionEvent event) {
        if(event.getSource()==informationsEntreprise){
            FormInfoEntreprise.setVisible(true);
            FormUserEntreprise.setVisible(false);
            accueilParam.setVisible(false);
            FormAProposEntreprise.setVisible(false);
            FormNousContacterEntreprise.setVisible(false);
        }
        if(event.getSource()==UsersEntreprise){
            FormInfoEntreprise.setVisible(false);
            FormUserEntreprise.setVisible(true);
            accueilParam.setVisible(false);
            FormAProposEntreprise.setVisible(false);
            FormNousContacterEntreprise.setVisible(false);
        }
        if(event.getSource()==aPropos){
            FormInfoEntreprise.setVisible(false);
            FormUserEntreprise.setVisible(false);
            accueilParam.setVisible(false);
            FormAProposEntreprise.setVisible(true);
            FormNousContacterEntreprise.setVisible(false);
        }
        if(event.getSource()==nousContacter){
            FormInfoEntreprise.setVisible(false);
            FormUserEntreprise.setVisible(false);
            accueilParam.setVisible(false);
            FormAProposEntreprise.setVisible(false);
            FormNousContacterEntreprise.setVisible(true);
        }
    }
    private void showInformations() {
        try {
            Company company = companyService.getCompanyInformations();
            if (company != null) {
                // Récupérer la liste des utilisateurs pour le ChoiceBox
                nomCreateur.setItems(FXCollections.observableArrayList(
                    userService.getAllUsers().stream()
                        .map(User::getPseudo)
                        .toArray(String[]::new)
                ));

                // Remplir les champs avec les informations de l'entreprise
                nomEntreprise.setText(company.getCompanyName());
                nomCreateur.setValue(company.getUser().getPseudo());
                choiceActivite.setValue(company.getSalesNature());
                choiceProfession.setValue(company.getProfession());
                choiceRegime.setValue(company.getTaxRegime());
                ChoiceFormeJuridique.setValue(company.getLegalForm());
                dateCreation.setValue(company.getCreationDate());
                dateCloture.setValue(company.getClosingDate());
                siretField.setText(company.getSiret());
                ribField.setText(company.getRib());
                addressField.setText(company.getAddress());
                phoneField.setText(company.getPhone());
                emailField.setText(company.getEmail());
                capitalSocialField.setText(String.valueOf(company.getCapitalSocial()));
                tvaNumberField.setText(company.getTvaNumber());
            } else {
                logger.error("Aucune entreprise trouvée");
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune entreprise trouvée. Veuillez créer une entreprise.");
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des informations de l'entreprise : {}", e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération des informations de l'entreprise : " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Validation des champs obligatoires
            if (nomEntreprise.getText() == null || nomEntreprise.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom de l'entreprise est obligatoire");
                return;
            }

            if (nomCreateur.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le créateur est obligatoire");
                return;
            }

            if (choiceActivite.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "L'activité est obligatoire");
                return;
            }

            if (choiceProfession.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La profession est obligatoire");
                return;
            }

            if (choiceRegime.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le régime fiscal est obligatoire");
                return;
            }

            if (ChoiceFormeJuridique.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La forme juridique est obligatoire");
                return;
            }

            if (dateCreation.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La date de création est obligatoire");
                return;
            }

            if (dateCloture.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La date de clôture est obligatoire");
                return;
            }

            // Récupération de l'entreprise actuelle
            Company company = companyService.getCompanyInformations();
            if (company == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune entreprise trouvée");
                return;
            }

            // Mise à jour des informations
            company.setCompanyName(nomEntreprise.getText().trim());
            company.setSalesNature(choiceActivite.getValue());
            company.setProfession(choiceProfession.getValue());
            company.setTaxRegime(choiceRegime.getValue());
            company.setLegalForm(ChoiceFormeJuridique.getValue());
            company.setCreationDate(dateCreation.getValue());
            company.setClosingDate(dateCloture.getValue());
            company.setSiret(siretField.getText());
            company.setRib(ribField.getText());
            company.setAddress(addressField.getText());
            company.setPhone(phoneField.getText());
            company.setEmail(emailField.getText());
            
            try {
                double capitalSocial = Double.parseDouble(capitalSocialField.getText());
                company.setCapitalSocial(capitalSocial);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le capital social doit être un nombre valide");
                return;
            }

            company.setTvaNumber(tvaNumberField.getText());

            // Sauvegarde des modifications
            if (companyService.saveCompany(company)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Les modifications ont été enregistrées avec succès");
                showInformations(); // Rafraîchir l'affichage
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la sauvegarde des modifications");
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde des modifications : {}", e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la sauvegarde des modifications : " + e.getMessage());
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

    @FXML
    public void showUsers() {
        try {
            List<User> users = userService.getAllUsers();
            if (users != null && !users.isEmpty()) {
                userTable.setItems(FXCollections.observableArrayList(users));
            } else {
                logger.warn("Aucun utilisateur trouvé");
                userTable.setItems(FXCollections.observableArrayList());
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des utilisateurs : {}", e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération des utilisateurs : " + e.getMessage());
            userTable.setItems(FXCollections.observableArrayList());
        }
    }

    public void remplirFormulaireModif(User user) {
        logger.info("Remplissage du formulaire de modification pour l'utilisateur : {}", user.getPseudo());
        modifPseudo.setText(user.getPseudo());
        modifEmail.setText(user.getEmail());
        selectedUser = FXCollections.observableArrayList(user);
    }

    @FXML
    public void modifierUser() {
        logger.info("Modification de l'utilisateur");
        String pseudo = modifPseudo.getText();
        String email = modifEmail.getText();
        String mdp = modifMdp.getText();
        String newMdpValue = newMdp.getText();
        String confirmNewMdpValue = confirmNewMdp.getText();


        if (pseudo.isEmpty() || email.isEmpty() || mdp.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        User user = userService.findByPseudo(pseudo).orElse(null);
        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Utilisateur non trouvé.");
            return;
        }

        if (!userService.authenticateUser(pseudo, mdp)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Mot de passe incorrect. Modification impossible.");
            return;
        }
        if(!newMdpValue.isEmpty()) {
            if (!newMdpValue.equals(confirmNewMdpValue) ) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Les nouveaux mots de passe ne correspondent pas.");
                return;
            }
            if(newMdpValue==mdp){
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le nouveau mot de passe doit être différent de l'ancien.");
                return;
            }
            if(userService.updateUserPassword(selectedUser, newMdpValue)){
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur mis à jour avec succès.");
                retourModifUser();
            }
            else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour de l'utilisateur.");
            }
        } else {
            if(userService.updateUser(pseudo, email)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur mis à jour avec succès.");
                retourModifUser();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour de l'utilisateur.");
            }
        }

        
    }

    @FXML
    public void retourModifUser() {
        logger.info("Retour au tableau des utilisateurs");
        modifInfoUser.setVisible(false);
        showUsers();
        userTable.setLayoutY(75.0);
        userTable.setPrefHeight(500);
    }

    @FXML
    public void retourAccueil(ActionEvent event) {
        accueilParam.setVisible(true);
        FormInfoEntreprise.setVisible(false);
        FormUserEntreprise.setVisible(false);
        FormAProposEntreprise.setVisible(false);
        FormNousContacterEntreprise.setVisible(false);
        

    }

    @FXML
    public void envoyerMessage(){
        logger.info("Envoi du message : {}", messageArea.getText());
        String message = messageArea.getText();
        if (message.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir le champ de message.");
            return;
        }
        // Logique pour envoyer le message
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Merci "+companyService.getCompanyInformations().getCompanyName() +" pour votre message!\n Nous nous engageons a y répondre au plus vite.\nLe délai maximun de réponse est de sept jours ouvrable.");
        messageArea.clear();
    }
}