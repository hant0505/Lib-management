module com.example.user {
    // Required modules
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires com.google.zxing.javase;
    requires com.google.zxing;
    requires java.desktop;
    requires io.nayuki.qrcodegen;
    requires jdk.httpserver; // http connect


    // Export packages to make them accessible to other modules
    exports com.book;
    exports com.user;
    exports com.game;
    exports com.attendance;

    // Open packages to the javafx.fxml module for reflection (used by FXMLLoader)
    opens com.book to javafx.fxml;
    opens com.user to javafx.fxml;
    opens com.game to javafx.fxml;
    opens com.attendance to javafx.fxml;
    exports com;
    opens com to javafx.fxml;
    exports com.effect;
    opens com.effect to javafx.fxml;
}
