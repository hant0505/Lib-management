package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.time.ZonedDateTime;

public class Calendar {

    public static void drawCalendar(Label label_month, FlowPane fp_calendar, ZonedDateTime dateFocus, ZonedDateTime today) {
        // update month - year
        label_month.setText(dateFocus.getMonth() + " " + dateFocus.getYear());

        double calendarWidth = fp_calendar.getPrefWidth();
        double spacingH = fp_calendar.getHgap();
        int monthMaxDate = dateFocus.getMonth().maxLength();
        // Check for leap year
        if (dateFocus.getYear() % 4 != 0 && monthMaxDate == 29) {
            monthMaxDate = 28;
        }
        int dateOffset = ZonedDateTime.of(dateFocus.getYear(), dateFocus.getMonthValue(), 1, 0, 0, 0, 0, dateFocus.getZone()).getDayOfWeek().getValue();
        ZonedDateTime preMonth = dateFocus.minusMonths(1);
        ZonedDateTime nextMonth = dateFocus.plusMonths(1);
        int preMonthMaxDate = preMonth.getMonth().maxLength();
        int nextMonthMaxDate = nextMonth.getMonth().maxLength();
        // Check for leap year at pre/next month
        if (preMonth.getYear() % 4 != 0 && preMonthMaxDate == 29) {
            preMonthMaxDate = 28;
        }
        if (nextMonth.getYear() % 4 != 0 && nextMonthMaxDate == 29) {
            nextMonthMaxDate = 28;
        }

        fp_calendar.getChildren().clear();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                Circle circle = new Circle();
                circle.setFill(Color.LIGHTGRAY);
                circle.setOpacity(0.5);
                double radius = ((calendarWidth / 7) - spacingH) / 2;
                circle.setRadius(radius);

                int calculatedDate = (j + 1) + (7 * i);
                // add previous month date
                if (calculatedDate <= dateOffset) {
                    int preDate = preMonthMaxDate - (dateOffset - calculatedDate);
                    drawDateCircle(circle, preDate, false, fp_calendar);
                } else if (calculatedDate <= dateOffset + monthMaxDate) { // add current month date
                    int currentDate = calculatedDate - dateOffset;
                    if (currentDate <= monthMaxDate) {
                        circle.setFill(Color.ANTIQUEWHITE);
                        circle.setOpacity(1);
                        drawDateCircle(circle, currentDate, true, fp_calendar);
                    }
                    // today circle set
                    if (today.getYear() == dateFocus.getYear() && today.getMonth() == dateFocus.getMonth() && today.getDayOfMonth() == currentDate) {
                        circle.setFill(Color.SANDYBROWN);
                        circle.setOpacity(1);
                    }
                } else { // add next month date
                    int nextDate = calculatedDate - monthMaxDate - dateOffset;
                    drawDateCircle(circle, nextDate, false, fp_calendar);
                }
                fp_calendar.setAlignment(Pos.CENTER);
            }
        }
    }

    private static void drawDateCircle(Circle circle, int currentDate, boolean currentMonth, FlowPane fp_calendar) {
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
    public static void backOneMonth(ActionEvent ignoredEvent, Label label_month, FlowPane fp_calendar, ZonedDateTime[] dateFocus, ZonedDateTime today) {
        dateFocus[0] = dateFocus[0].minusMonths(1);
        drawCalendar(label_month, fp_calendar, dateFocus[0], today);
    }

    @FXML
    public static void forwardOneMonth(ActionEvent ignoredEvent, Label label_month, FlowPane fp_calendar, ZonedDateTime[] dateFocus, ZonedDateTime today) {
        dateFocus[0] = dateFocus[0].plusMonths(1);
        drawCalendar(label_month, fp_calendar, dateFocus[0], today);
    }
}
