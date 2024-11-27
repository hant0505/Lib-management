package com.book;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;

public class BookCardController {

    @FXML
    private Label bookTitle;

    @FXML
    private ImageView imageBookCover;

    public void setBookData(String title, String imagePath, byte[] image) {
        bookTitle.setText(title);

        Image coverImage = null;
        if (imagePath != null && !imagePath.isEmpty()) {
            coverImage = new Image(imagePath, true);
        } else {
            if (image != null && image.length > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(image);
                coverImage = new Image(bis);
            }
        }
        imageBookCover.setImage(coverImage);
    }

    public String getBookTitle() {
        return bookTitle.getText();
    }
}
