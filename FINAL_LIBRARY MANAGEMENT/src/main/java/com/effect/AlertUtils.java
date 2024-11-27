package com.effect;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Objects;

public class AlertUtils {
    public static void errorAlert(String content) {
        // Create an alert
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        // Set custom icon for the alert window (stage)
        ImageView icon = new ImageView(new Image(Objects.requireNonNull(AlertUtils.class.getResourceAsStream("/com/Image/error.png"))));
        setIcon(icon, alert);
        alert.show();
    }

    public static void infoAlert(String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(content);

        ImageView icon = new ImageView(new Image(Objects.requireNonNull(AlertUtils.class.getResourceAsStream("/com/Image/information.png"))));
        setIcon(icon, alert);
        alert.show();
    }


    public static void warningAlert(String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(content);

        ImageView icon = new ImageView(new Image(Objects.requireNonNull(AlertUtils.class.getResourceAsStream("/com/Image/warning.png"))));
        setIcon(icon, alert);
        alert.show();
    }

    public static Alert confirmAlert(String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(content);

        ImageView icon = new ImageView(new Image(Objects.requireNonNull(AlertUtils.class.getResourceAsStream("/com/Image/confirmation.png"))));
        setIcon(icon, alert);
        return alert;
    }

    private static void setIcon(ImageView icon, Alert alert) {
        icon.setFitHeight(50);
        icon.setFitWidth(50);
        alert.getDialogPane().setGraphic(icon);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(icon.getImage());
    }
}
