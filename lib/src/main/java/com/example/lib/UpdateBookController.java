package com.example.lib;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateBookController implements Initializable {
    @FXML
    private TextField tf_bookTitle_update;

    @FXML
    private TextField tf_bookAuthor_update;

    @FXML
    private TextField tf_bookEdition_update;

    @FXML
    private Button button_updateBook;

    private Book currentBook;

    public void setBook(Book book) {
        this.currentBook = book;
        tf_bookTitle_update.setText(book.getTitle());
        tf_bookAuthor_update.setText(book.getAuthor());
        tf_bookEdition_update.setText(Integer.toString(book.getPublishYear()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_updateBook.setOnAction(e -> {
            String title = tf_bookTitle_update.getText();
            String author = tf_bookAuthor_update.getText();
            int edition = Integer.parseInt(tf_bookEdition_update.getText());
            currentBook.setTitle(title);
            currentBook.setAuthor(author);
            currentBook.setPublishYear(edition);

            DBUtils.updateBookFromDB(currentBook, e);
            DBUtils.changeScene(e, "bookInfo.fxml", "Book Information");
        });

    }
}

