package com.example.lib;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class addBookController {
    @FXML
    public TextField tf_bookTitle;

    @FXML
    public TextField tf_bookAuthor;

    @FXML
    public TextField tf_bookEdition;

    @FXML
    public TextField tf_bookISBN;

    @FXML
    public TextField tf_bookDescription;

    @FXML
    public Button button_addAbook;

    public void initialize() {
        button_addAbook.setOnAction(e -> DBUtils.addBook(new Book()));
    }




}
