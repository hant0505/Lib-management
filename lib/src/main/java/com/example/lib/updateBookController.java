package com.example.lib;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class updateBookController implements Initializable {
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
        tf_bookEdition_update.setText(Integer.toString(book.getEdition()));
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_updateBook.setOnAction(e -> {
            String title = tf_bookTitle_update.getText();
            String author = tf_bookAuthor_update.getText();
            int edition = Integer.parseInt(tf_bookEdition_update.getText());
            currentBook.setTitle(title);
            currentBook.setAuthor(author);
            currentBook.setEdition(edition);

            DBUtils.updateBookfromDB(currentBook,e);

            Stage stage = (Stage) button_updateBook.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }



        });

    }






}

