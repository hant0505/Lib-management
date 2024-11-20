module com.example.user {
    // Required modules
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires java.net.http;
    requires javafx.base; // Thêm các module cần thiết khác
    requires com.fasterxml.jackson.databind;
    requires io.nayuki.qrcodegen; // QR CODE

    // Export packages to make them accessible to other modules
    exports com.book;

    // Open packages to the javafx.fxml module for reflection (used by FXMLLoader)
    opens com.book to javafx.fxml;

}
