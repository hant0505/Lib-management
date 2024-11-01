module com.example.lib {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.lib to javafx.fxml;
    exports com.example.lib;
}