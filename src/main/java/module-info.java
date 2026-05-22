module com.example.virsa {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;

    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.example.virsa to javafx.fxml;
    opens com.example.virsa.controller to javafx.fxml;
    opens com.example.virsa.model to javafx.base;

    exports com.example.virsa;
    exports com.example.virsa.controller;
    exports com.example.virsa.model;
}
