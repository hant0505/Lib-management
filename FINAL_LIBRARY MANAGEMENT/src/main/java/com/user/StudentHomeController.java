package com.user;

import com.Calendar;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.ResourceBundle;

public class StudentHomeController implements Initializable {
    private final ZonedDateTime[] dateFocus = {ZonedDateTime.now()};
    private final ZonedDateTime today = ZonedDateTime.now();
    String username;
    String name;
    String email;
    String phone;
    @FXML
    private FlowPane fp_calendar;
    @FXML
    private Button button_account;
    @FXML
    private Button button_transactionsForStu;
    @FXML
    private Button button_booksForStu;
    @FXML
    private Button button_preMonth;
    @FXML
    private Button button_nextMonth;
    @FXML
    private Button button_game;
    @FXML
    private Label label_welcome;
    @FXML
    private Label label_month;
    @FXML
    private Label label_coin;
    @FXML
    private Label label_booksBorrowing;
    @FXML
    private Label label_treeLevel;
    @FXML
    private Label label_monthTrans;

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

        label_treeLevel.setText("Tree Level: " + Objects.requireNonNull(com.game.DBUtils.getTreeState(DBUtils.loggedInUser)).getCurrentGrowth());
        label_booksBorrowing.setText("Books Borrowing: " + com.book.DBUtils.booksBorrowing(DBUtils.loggedInUser));
        label_monthTrans.setText("This month transactions: " + com.book.DBUtils.monthlyTransMade(DBUtils.loggedInUser));

        button_preMonth.setOnAction(event -> Calendar.backOneMonth(event, label_month, fp_calendar, dateFocus, today));
        button_nextMonth.setOnAction(event -> Calendar.forwardOneMonth(event, label_month, fp_calendar, dateFocus, today));
        label_coin.setText(String.valueOf(DBUtils.getCoins(DBUtils.loggedInUser)));

        button_account.setOnAction(event -> DBUtils.changeScene(event, "account_info_stu.fxml", "Account Information"));
        button_transactionsForStu.setOnAction(event -> DBUtils.changeScene(event, "/com/book/transactions_stu.fxml", "Transactions"));
        button_game.setOnAction(event -> DBUtils.changeScene(event, "/com/game/game.fxml", "Game"));
        button_booksForStu.setOnAction(event -> DBUtils.changeScene(event, "/com/book/bookInfo_stu.fxml", "Books"));

    }
}
