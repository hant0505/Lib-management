package com.example.lib;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class DetailsBookController implements Initializable {
    @FXML
    private Button button_close;
    @FXML
    private Button button_transact;

    @FXML
    private Label label_title;
    @FXML
    private Label label_author;
    @FXML
    private Label label_publishYear;
    @FXML
    private Label label_quantity;
    @FXML
    private Label label_category;
    @FXML
    private Label label_description;

    @FXML
    private ImageView image_coverBook;

    private Book currentBook;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.currentBook = BookSession.getSelectedBook();
        showBookDetails();

        AnimationUtils.applyFadeTransition(label_title);
        AnimationUtils.applyFadeTransition(label_author);
        AnimationUtils.applyFadeTransition(label_publishYear);
        AnimationUtils.applyFadeTransition(label_quantity);
        AnimationUtils.applyFadeTransition(label_category);
        AnimationUtils.applyFadeTransition(image_coverBook);

        AnimationUtils.applySlideInTransition(label_title);
        AnimationUtils.applySlideInTransition(label_author);
        AnimationUtils.applySlideInTransition(label_publishYear);
        AnimationUtils.applySlideInTransition(label_quantity);
        AnimationUtils.applySlideInTransition(label_category);
        AnimationUtils.applySlideInTransition(image_coverBook);

        AnimationUtils.applyScaleTransition(label_title);
        AnimationUtils.applyScaleTransition(label_author);
        AnimationUtils.applyScaleTransition(label_publishYear);
        AnimationUtils.applyScaleTransition(label_quantity);
        AnimationUtils.applyScaleTransition(label_category);
        AnimationUtils.applyScaleTransition(image_coverBook);

        // Event Handlers
        button_close.setOnAction(event -> DBUtils.changeScene(event, "bookInfo.fxml", "Book Information"));
        button_transact.setOnAction(event -> {
            if (currentBook.getQuantity() < 1) {
                showAlert("There are no more books available to borrow!");
            } else {
                DBUtils.changeScene(event, "transaction_book.fxml", "Book Transaction");
            }
        });
    }

    private void showBookDetails() {
        if (currentBook != null) {
            label_title.setText(currentBook.getTitle());
            label_author.setText(currentBook.getAuthor());
            label_quantity.setText("Quantity: " + currentBook.getQuantity());
            label_category.setText("Category: " + currentBook.getCategory());
            label_publishYear.setText("Publish Year: " + currentBook.getPublishYear());
            label_description.setText("Description: \n" + currentBook.getDescription());

            Image coverImage;
            if (currentBook.getImagePath() != null) {
                coverImage = new Image("file:" + currentBook.getImagePath());
            } else {
                coverImage = new Image(getClass().getResource("/com/example/lib/bia-sach-harry-potter-va-cai-dit-con-me-may.jpg").toExternalForm());
            }
            image_coverBook.setImage(coverImage);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
