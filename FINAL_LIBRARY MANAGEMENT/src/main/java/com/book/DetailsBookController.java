package com.book;

import com.effect.AlertUtils;
import com.effect.AnimationUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
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
        // GET BOOK
        String isbn = currentBook.getIsbn();
        currentBook = DBUtils.getBookByIsbn(isbn);
        if (currentBook == null) {
            System.out.println("Error: No book is selected.");
            AlertUtils.warningAlert("No book selected.");
            return;
        }
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

        button_close.setOnAction(event -> {
            if (com.user.DBUtils.loggedInRole.equals("LIBRARIAN")) {
                com.user.DBUtils.changeScene(event, "/com/book/bookInfo_lib.fxml", "Books Information");
            } else {
                com.user.DBUtils.changeScene(event, "/com/book/bookInfo_stu.fxml", "Books");
            }
        });
        button_transact.setOnAction(event -> {
            if (currentBook.getQuantity() < 1) AlertUtils.errorAlert("There are no more books available to borrow!");
            else {
                if (com.user.DBUtils.loggedInRole.equals("STUDENT")) {
                    AlertUtils.infoAlert("You should visit the library to borrow books");
                } else com.user.DBUtils.changeScene(event, "/com/book/transaction_book.fxml", "Book Transaction");
            }

        });
    }

    private void showBookDetails() {
        if (currentBook != null) {
            label_title.setText(currentBook.getTitle());
            label_author.setText(currentBook.getAuthor());
            label_quantity.setText("Quantity: " + currentBook.getQuantity());
            label_publishYear.setText("Publish Year: " + currentBook.getPublishYear());
            label_description.setText("Description:\n" + currentBook.getDescription());
            label_description.setWrapText(true);
            label_description.heightProperty().addListener((_, _, newValue) -> label_description.setPrefHeight(newValue.doubleValue()));
            label_category.setText("Category: " + currentBook.getCategory());

            Image coverImage = null; // ready to set into ImageView
            byte[] image;
            if (currentBook.getImagePath() != null) {
                // Nếu imagePath là URL từ API
                if (currentBook.getImagePath().startsWith("http")) {
                    coverImage = new Image(currentBook.getImagePath(), true);
                }
            } else {
                image = currentBook.getImage();
                if (image != null && image.length > 0) {
                    ByteArrayInputStream bis = new ByteArrayInputStream(image);
                    coverImage = new Image(bis);
                }
            }
            image_coverBook.setImage(coverImage);
        }
    }
}
