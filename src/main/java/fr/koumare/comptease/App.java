package fr.koumare.comptease;

import fr.koumare.comptease.utilis.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fr/koumare/comptease/fxml/auth.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Connexion");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        //  Initialisation de Hibernate avant de lancer JavaFX
         HibernateUtil.getSessionFactory();
        System.out.println("✅ Hibernate est bien initialisé depuis App !");

        // Lancement de l'application JavaFX
        launch();
    }
}
