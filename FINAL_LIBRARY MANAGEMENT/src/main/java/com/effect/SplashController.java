package com.effect;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class SplashController {
    public Rectangle rec_main;
    public Rectangle rec_sub;
    public Label label_progress;
    private Stage splashStage;

    public void initialize() {
        LoadingTask loadingTask = new LoadingTask();
        loadingTask.progressProperty().addListener((_, _, newValue) -> {
            String formattedNum = String.format("%.0f", newValue.doubleValue() * 100);
            label_progress.setText(formattedNum + " %");
            rec_sub.setWidth(rec_main.getWidth() * newValue.doubleValue());

            if (newValue.doubleValue() == 1.0) {
                if (splashStage != null) {
                    Platform.runLater(() -> {
                        splashStage.close();
                    });
                }
            }
        });
        new Thread(loadingTask).start();
    }

}
