module fr.koumare.comptease {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.sql;  // Pour JDBC et SQL
    requires mysql.connector.j;
    requires java.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires static lombok;
    requires java.management;
    requires javafx.graphics;
    requires jbcrypt;
    requires com.sun.istack.runtime;
    requires org.slf4j;
    requires java.desktop;

    // Ouvrir les packages pour JavaFX et Hibernate
    opens fr.koumare.comptease to javafx.graphics, javafx.fxml;
    opens fr.koumare.comptease.controllers to javafx.fxml;  // 📌 Important pour éviter l'IllegalAccessException
    opens fr.koumare.comptease.model to org.hibernate.orm.core;


    // Exporter les packages accessibles
    exports fr.koumare.comptease;
    exports fr.koumare.comptease.model;
    exports fr.koumare.comptease.controllers;
    exports fr.koumare.comptease.annotations;
    exports fr.koumare.comptease.utilis;
    opens fr.koumare.comptease.utilis to javafx.fxml, javafx.graphics;
}
