module com.example.user {
    // Required modules
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires java.net.http;
    requires javafx.base; // Thêm các module cần thiết khác
    requires com.fasterxml.jackson.databind;

    // Export packages to make them accessible to other modules
    exports com.book;
    exports com.user;
    exports com.apiBook; // Xuất khẩu package chứa lớp API_Controller

    // Open packages to the javafx.fxml module for reflection (used by FXMLLoader)
    opens com.book to javafx.fxml;
    opens com.user to javafx.fxml;
    opens com.apiBook to javafx.fxml; // Cho phép truy cập vào package com.apiBook cho FXML
}
