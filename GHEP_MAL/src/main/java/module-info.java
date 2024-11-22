module com.example.lib {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires mysql.connector.j;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.google.zxing;
    requires com.google.zxing.javase;

    requires io.nayuki.qrcodegen; // QR CODE



    opens com.example.lib to javafx.fxml;
    exports com.example.lib;
}