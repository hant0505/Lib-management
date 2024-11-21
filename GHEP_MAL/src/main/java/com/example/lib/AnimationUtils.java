package com.example.lib;


import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationUtils {
    public static void applyFadeTransition(Node node) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), node);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    public static void applySlideInTransition(Node node) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), node);
        translateTransition.setFromX(-500);
        translateTransition.setToX(0);
        translateTransition.play();
    }

    public static void applyScaleTransition(Node node) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), node);
        scaleTransition.setFromX(0);
        scaleTransition.setFromY(0);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.play();
    }
}
