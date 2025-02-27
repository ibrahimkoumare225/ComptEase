package fr.koumare.comptease.controllers;

import fr.koumare.comptease.service.UserService;
import fr.koumare.comptease.service.impl.UserServiceImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;

public class AuthController implements Initializable {

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
    private void handleLogin() {
        String pseudo = si_pseudo.getText();
        String password = si_password.getText();

        if (userService.authenticateUser(pseudo, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Authentification réussie !");
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Identifiants incorrects.");
        }
    }

    @FXML
    private void handleRegister() {
        String pseudo = su_pseudo.getText();
        String email = su_mail.getText();
        String password = su_password.getText();

        if (email.isEmpty() || password.isEmpty() || pseudo.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez remplir tous les champs.");
            return;
        }

        if (userService.registerUser(pseudo,email, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur enregistré avec succès !");
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le pseudo existe déjà.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void switchForm(ActionEvent event) {

        if (event.getSource() == su_loginAccountBtn) {
            login_form.setVisible(true);
            signup_form.setVisible(false);
        }else if (event.getSource() == si_createAccountBtn) {
            login_form.setVisible(false);
            signup_form.setVisible(true);
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
