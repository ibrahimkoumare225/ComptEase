package fr.koumare.comptease.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class FacturesController extends BaseController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(FacturesController.class);

    @FXML
    private BorderPane chartContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initialisation de FacturesController");
        // Add factures-specific initialization here
    }
}