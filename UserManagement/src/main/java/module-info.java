module com.example.user {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens com.example.user to javafx.fxml;
    exports com.example.user;
}