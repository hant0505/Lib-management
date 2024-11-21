module com.example.lib {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires mysql.connector.j;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;


    opens com.example.lib to javafx.fxml;
    exports com.example.lib;
}