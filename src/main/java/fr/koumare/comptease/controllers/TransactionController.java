package fr.koumare.comptease.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class TransactionController extends BaseController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @FXML
    private BorderPane chartContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Initialisation de TransactionController");
        // Add transaction-specific initialization here
    }
}