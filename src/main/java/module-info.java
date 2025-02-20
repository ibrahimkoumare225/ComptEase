module fr.koumare.comptease {
    requires javafx.controls;
    requires javafx.fxml;


    opens fr.koumare.comptease to javafx.fxml;
    exports fr.koumare.comptease;
}