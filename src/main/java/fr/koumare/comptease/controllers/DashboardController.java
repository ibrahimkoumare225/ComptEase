package fr.koumare.comptease.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;


public class DashboardController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void helloButton(){welcomeText.setText("bienvenue"); }
}
