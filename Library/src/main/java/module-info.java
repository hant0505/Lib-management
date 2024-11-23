module com.example.lib {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires mysql.connector.j;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    // Exports and opens packages
    opens Transactions to javafx.fxml;
    exports Transactions;

    opens Animation to javafx.fxml;
    exports Animation;

    opens Book to javafx.fxml;
    exports Book;

    opens Users to javafx.fxml;
    exports Users; // Xuất package Users cho các module khác

    opens Attendance to javafx.fxml;
    exports Attendance;
}

