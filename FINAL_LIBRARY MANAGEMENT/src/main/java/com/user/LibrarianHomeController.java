package com.user;

import com.Calendar;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

@SuppressWarnings("unchecked")
public class LibrarianHomeController implements Initializable {

    private final ZonedDateTime[] dateFocus = {ZonedDateTime.now()};
    private final ZonedDateTime today = ZonedDateTime.now();
    @FXML
    public Label label_welcome;
    String username;
    String name;
    String phone;
    String email;
    @FXML
    private Button button_account;
    @FXML
    private Button button_students;
    @FXML
    private Button button_transactionsForLib;
    @FXML
    private Button button_booksForLib;
    @FXML
    private Button button_preMonth;
    @FXML
    private Button button_nextMonth;
    @FXML
    private Label label_month;
    @FXML
    private FlowPane fp_calendar;
    @FXML
    private BarChart<String, Number> chart_topBorrowedBooks;
    @FXML
    private BarChart<String, Number> chart_currentBorrowedBooks;
    @FXML
    private BarChart<String, Number> chart_totalBooks;

    public void setUserInfo(String username, String name, String email, String phone) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Calendar.drawCalendar(label_month, fp_calendar, dateFocus[0], today);

        label_welcome.setText("Welcome " + DBUtils.loggedInName + "!");
        label_welcome.widthProperty().addListener((_, _, _) -> {
            double layX = 175 - (label_welcome.getWidth() / 2);
            label_welcome.setTranslateX(layX);
        });

        button_preMonth.setOnAction(event -> Calendar.backOneMonth(event, label_month, fp_calendar, dateFocus, today));
        button_nextMonth.setOnAction(event -> Calendar.forwardOneMonth(event, label_month, fp_calendar, dateFocus, today));
        //menu buttons change scene
        button_transactionsForLib.setOnAction(event -> DBUtils.changeScene(event, "/com/book/transactions_lib.fxml", "Transactions Information"));
        button_booksForLib.setOnAction(event -> DBUtils.changeScene(event, "/com/book/bookInfo_lib.fxml", "Book Information"));
        button_students.setOnAction(event -> com.user.DBUtils.changeScene(event, "student_info.fxml", "Student Information"));
        button_account.setOnAction(event -> DBUtils.changeScene(event, "account_info_lib.fxml", "Account Information"));

        loadCharts();
    }

    public void loadCharts() {
        XYChart.Series<String, Number> topBooksSeries = new XYChart.Series<>();
        DBUtils.getTopBorrowedBooks().forEach((book, count) -> {
            XYChart.Data<String, Number> data = new XYChart.Data<>(book, count);
            topBooksSeries.getData().add(data);
        });

        chart_topBorrowedBooks.getData().add(topBooksSeries);

        topBooksSeries.getData().forEach(data -> {
            data.getNode().setStyle("-fx-bar-fill: #8dd8e1;");
        });

        XYChart.Series<String, Number> borrowedBooksSeries = new XYChart.Series<>();
        borrowedBooksSeries.getData().add(new XYChart.Data<>("Borrowed Books", DBUtils.getBorrowedBookCount()));
        chart_currentBorrowedBooks.getData().add(borrowedBooksSeries);

        borrowedBooksSeries.getData().forEach(data -> {
            data.getNode().setStyle("-fx-bar-fill: #fad0ae;");
        });

        XYChart.Series<String, Number> totalBooksSeries = new XYChart.Series<>();
        totalBooksSeries.getData().add(new XYChart.Data<>("Total Books", DBUtils.getTotalBookCount()));
        chart_totalBooks.getData().add(totalBooksSeries);

        totalBooksSeries.getData().forEach(data -> {
            data.getNode().setStyle("-fx-bar-fill: #f8c8e6;");
        });
    }


}
