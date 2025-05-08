package fr.koumare.comptease;

import fr.koumare.comptease.dao.ClientDao;
import fr.koumare.comptease.model.Client;
import fr.koumare.comptease.utilis.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fr/koumare/comptease/fxml/clientPage.fxml"));
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        Scene scene = new Scene(fxmlLoader.load(), 1100, 600);
        stage.setTitle("Connexion");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // 🔥 Initialisation de Hibernate avant de lancer JavaFX
        HibernateUtil.getSessionFactory();
        System.out.println("✅ Hibernate est bien initialisé depuis App !");
        ClientDao clientDao = new ClientDao();
        clientDao.getAllClients().forEach(client -> {System.out.println("IDC: " + client.getIdc() +"IDUser: " + client.getIdUser() +
                ", Nom: " + client.getFirstName() +
                ", Prénom: " + client.getLastName() +
                ", Contact: " + client.getContact());});
        // Lancement de l'application JavaFX
        launch();
    }
}
