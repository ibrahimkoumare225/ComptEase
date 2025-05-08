package fr.koumare.comptease.controllers;

import fr.koumare.comptease.model.User;
import fr.koumare.comptease.service.UserService;
import fr.koumare.comptease.service.impl.UserServiceImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class AuthController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    // Regex pattern for email validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @FXML
    private AnchorPane login_form;

    @FXML
    private Button si_createAccountBtn;

    @FXML
    private Label si_fgtPwd;

    @FXML
    private Button si_loginBtn;

    @FXML
    private PasswordField si_password;

    @FXML
    private TextField si_pseudo;

    @FXML
    private AnchorPane signup_form;

    @FXML
    private Button su_loginAccountBtn;

    @FXML
    private TextField su_mail;

    @FXML
    private PasswordField su_password;

    @FXML
    private TextField su_pseudo;

    @FXML
    private Button su_signupBtn;

    private UserService userService = new UserServiceImpl();

    @FXML
    private void handleLogin(ActionEvent event) {
        String pseudo = si_pseudo.getText().trim();
        String password = si_password.getText(); // Ne pas trimmer le mot de passe

        logger.info("Tentative de connexion avec pseudo : {}", pseudo);

        if (pseudo.isEmpty() || password.isEmpty()) {
            logger.warn("Champs vides détectés : pseudo = '{}', mot de passe = '{}'", pseudo, password.isEmpty() ? "vide" : "non vide");
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez remplir tous les champs.");
            return;
        }

        if (userService.authenticateUser(pseudo, password)) {
            logger.info("Connexion réussie pour : {}", pseudo);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Authentification réussie !");
            // Redirection vers Dashboard.fxml (1300x720)
            loadFXML(event, "/fr/koumare/comptease/fxml/dashboard.fxml", "Tableau de bord", 1300, 720);
        } else {
            logger.warn("Échec de la connexion pour : {}", pseudo);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Identifiants incorrects.");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String pseudo = su_pseudo.getText().trim();
        String email = su_mail.getText().trim();
        String password = su_password.getText(); // Ne pas trimmer le mot de passe

        logger.info("Tentative d'inscription avec pseudo : {}, email : {}", pseudo, email);

        // Check for empty fields
        if (email.isEmpty() || password.isEmpty() || pseudo.isEmpty()) {
            logger.warn("Champs vides détectés : pseudo = '{}', email = '{}', mot de passe = '{}'", pseudo, email, password.isEmpty() ? "vide" : "non vide");
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez remplir tous les champs.");
            return;
        }

        // Validate email format
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            logger.warn("Format d'email invalide : {}", email);
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez entrer un email valide (ex. : exemple@domaine.com).");
            return;
        }

        if (userService.registerUser(pseudo, email, password)) {
            logger.info("Inscription réussie pour : {}", pseudo);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur enregistré avec succès !");
            // Récupérer l'utilisateur nouvellement créé
            Optional<User> newUserOpt = userService.findByPseudo(pseudo);
            if (newUserOpt.isPresent()) {
                logger.info("Utilisateur récupéré après inscription : {}", pseudo);
                loadInfosFXML(event, newUserOpt.get());
            } else {
                logger.error("Impossible de récupérer l'utilisateur après inscription : {}", pseudo);
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de récupérer l'utilisateur.");
            }
        } else {
            logger.warn("Échec de l'inscription : pseudo {} existe déjà", pseudo);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le pseudo existe déjà.");
        }
    }

    private void loadInfosFXML(ActionEvent event, User user) {
        try {
            logger.info("Chargement de infos.fxml pour l'utilisateur : {}", user.getPseudo());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/koumare/comptease/fxml/infos.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            InfosController infosController = loader.getController();
            infosController.setCurrentUser(user); // Passer l'utilisateur
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Informations entreprise");
            stage.show();
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de infos.fxml", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger infos.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadFXML(ActionEvent event, String fxmlPath, String title, double width, double height) {
        try {
            logger.info("Chargement de {} ({}x{})", fxmlPath, width, height);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), width, height);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de {} : {}", fxmlPath, e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'interface : " + e.getMessage());
            e.printStackTrace();
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
    public void switchForm(ActionEvent event) {
        if (event.getSource() == su_loginAccountBtn) {
            logger.info("Passage au formulaire de connexion");
            login_form.setVisible(true);
            signup_form.setVisible(false);
        } else if (event.getSource() == si_createAccountBtn) {
            logger.info("Passage au formulaire d'inscription");
            login_form.setVisible(false);
            signup_form.setVisible(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initialisation de AuthController");
    }
}