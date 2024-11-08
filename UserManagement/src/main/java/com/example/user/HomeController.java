package com.example.user;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.*;

@SuppressWarnings({"CallToPrintStackTrace"})
public class HomeController implements Initializable {
    @FXML
    public AnchorPane chart_review;

    @FXML
    private Button button_account;
    @FXML
    private Button button_students;
    @FXML
    private Button button_issuedBook;
    @FXML
    private Button button_transactions;
    @FXML
    private Button button_books;
    @FXML
    private Button button_preMonth;
    @FXML
    private Button button_nextMonth;

    @FXML
    public Label label_welcome;
    @FXML
    private Label label_month;

    @FXML
    private FlowPane fp_calendar;


    ZonedDateTime dateFocus;
    ZonedDateTime today;

    private String username;
    private String email;
    private String phone;

    public void setUserInfo(String username, String email, String phone) {
        this.username = username;
        this.email = email;
        this.phone = phone;
    }

    private void showAccountInfo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("account-info.fxml"));
            Parent root = loader.load();

            AccountController accountController = loader.getController();
            accountController.showInfo(username, email, phone);
            accountController.showInfoEdit(username, email, phone);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawCalendar() {
        // update month - year
        label_month.setText(dateFocus.getMonth() + " " + dateFocus.getYear());

        double calendarWidth = fp_calendar.getPrefWidth();
        double spacingH = fp_calendar.getHgap();
        int monthMaxDate = dateFocus.getMonth().maxLength();
        //Check for leap year
        if(dateFocus.getYear() % 4 != 0 && monthMaxDate == 29) {
            monthMaxDate = 28;
        }
        int dateOffset = ZonedDateTime.of(dateFocus.getYear(), dateFocus.getMonthValue(), 1,0,0,0,0,dateFocus.getZone()).getDayOfWeek().getValue();
        ZonedDateTime preMonth = dateFocus.minusMonths(1);
        ZonedDateTime nextMonth = dateFocus.plusMonths(1);
        int preMonthMaxDate = preMonth.getMonth().maxLength();
        int nextMonthMaxDate = nextMonth.getMonth().maxLength();
        //Check for leap year at pre/ next month
        if (preMonth.getYear() % 4 != 0 && preMonthMaxDate == 29 ) {
            preMonthMaxDate = 28;
        }
        if (nextMonth.getYear() % 4 != 0 && nextMonthMaxDate == 29 ) {
            nextMonthMaxDate = 28;
        }

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                Circle circle = new Circle();
                circle.setFill(Color.LIGHTGRAY);
                circle.setOpacity(0.5);
                double radius =((calendarWidth/7)  - spacingH) / 2;
                circle.setRadius(radius);

                int calculatedDate = (j + 1) + (7 * i);
                // add previous month date
                if (calculatedDate <= dateOffset) {
                    int preDate = preMonthMaxDate - (dateOffset - calculatedDate);
                    drawDateCircle(circle, preDate, false);
                } else if (calculatedDate <= dateOffset + monthMaxDate) { // add current month date
                    int currentDate = calculatedDate - dateOffset;
                    if(currentDate <= monthMaxDate) {
                        circle.setFill(Color.ANTIQUEWHITE);
                        circle.setOpacity(1);
                        drawDateCircle(circle, currentDate, true);
                    }
                    // today circle set
                    if(today.getYear() == dateFocus.getYear() && today.getMonth() == dateFocus.getMonth() && today.getDayOfMonth() == currentDate){
                        circle.setFill(Color.SANDYBROWN);
                        circle.setOpacity(1);
                    }
                } else { // add next month date
                    int nextDate = calculatedDate - monthMaxDate - dateOffset;
                    drawDateCircle(circle, nextDate, false);
                }
                fp_calendar.setAlignment(Pos.CENTER);
            }
        }
    }

    private void drawDateCircle(Circle circle, int currentDate, boolean currentMonth) {
        Label date = new Label(String.valueOf(currentDate));
        date.setStyle("-fx-font-size: 14.0; -fx-font-weight: bold; -fx-font-family:Century Gothic;");
        if (!currentMonth) {
            date.setOpacity(0.5);
        }
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(circle, date);
        stackPane.setAlignment(Pos.CENTER);
        fp_calendar.getChildren().add(stackPane);
    }

    @FXML
    void backOneMonth(ActionEvent event) {
        dateFocus = dateFocus.minusMonths(1);
        fp_calendar.getChildren().clear();
        drawCalendar();
    }

    @FXML
    void forwardOneMonth(ActionEvent event) {
        dateFocus = dateFocus.plusMonths(1);
        fp_calendar.getChildren().clear();
        drawCalendar();
    }

    public static void hoverButton(Button button) {
        button.styleProperty().bind(Bindings.when(button.hoverProperty())
                .then("-fx-background-color: #fda35a; ")
                .otherwise("-fx-background-color: #dfc19f;"));
    }

    public static void hoverButtonFlowPane(List<Button> buttons) {
        for (Button button : buttons) {
            button.styleProperty().bind(Bindings.when(button.hoverProperty())
                    .then("-fx-background-color: #fda35a; ")
                    .otherwise("-fx-background-color: #dfc19f;"));
        }

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateFocus = ZonedDateTime.now();
        today = ZonedDateTime.now();
        drawCalendar();
        List<Button> buttons = new ArrayList<>();
        Collections.addAll(buttons, button_account, button_students, button_issuedBook, button_books, button_transactions);
        hoverButtonFlowPane(buttons);
        hoverButton(button_nextMonth);
        hoverButton(button_preMonth);

        button_preMonth.setOnAction(this::backOneMonth);
        button_nextMonth.setOnAction(this::forwardOneMonth);
        button_account.setOnAction(this::showAccountInfo);
        label_welcome.setText("Welcome back!");
    }
}
