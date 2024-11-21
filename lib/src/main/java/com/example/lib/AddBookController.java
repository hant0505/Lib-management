package com.example.lib;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;


public class AddBookController implements Initializable {
    @FXML
    private TextField tf_bookTitle;
    @FXML
    private TextField tf_bookAuthor;
    @FXML
    private TextField tf_bookPublishYear;
    @FXML
    private TextField tf_bookQuantity;
    @FXML
    private TextField tf_bookISBN;
    @FXML
    private TextField tf_bookCategory;

    @FXML
    private TextArea ta_bookDescription;

    @FXML
    private Button button_check;
    @FXML
    private Button button_addABook;
    @FXML
    private Button button_1;
    @FXML
    private Button button_2;
    @FXML
    private Button button_3;
    @FXML
    private Button button_4;
    @FXML
    private Button button_5;
    @FXML
    private Button button_6;
    @FXML
    private Button button_7;
    @FXML
    private Button button_8;
    @FXML
    private Button button_9;
    @FXML
    private Button button_0;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillIsbn();

        button_check.setOnAction(_ -> checkIsbn());

        button_addABook.setOnAction(this::handleAddBook);
    }

    // Kiểm tra ISBN
    private void checkIsbn() {
        String isbn = tf_bookISBN.getText().trim();
        if (isbn.isEmpty()) {
            showAlert("Please enter an ISBN to check.");
            return;
        }


        Book existingBook = DBUtils.getBookByIsbn(isbn);
        if (existingBook != null) {

            tf_bookTitle.setText(existingBook.getTitle());
            tf_bookAuthor.setText(existingBook.getAuthor());
            tf_bookCategory.setText(existingBook.getCategory());
            tf_bookPublishYear.setText(String.valueOf(existingBook.getPublishYear()));
            ta_bookDescription.setText(existingBook.getDescription());
            tf_bookQuantity.setText(String.valueOf(existingBook.getQuantity()));

            showAlert("ISBN already exists. Book details loaded.");
        } else {
            showAlert("ISBN is new. You can add a new book.");
        }
    }

    private void handleAddBook(ActionEvent event) {
        String title = tf_bookTitle.getText().trim();
        String author = tf_bookAuthor.getText().trim();
        String category = tf_bookCategory.getText().trim();
        String isbn = tf_bookISBN.getText().trim();
        int publishYear = Integer.parseInt(tf_bookPublishYear.getText().trim());
        int quantity = Integer.parseInt(tf_bookQuantity.getText().trim());
        String description = ta_bookDescription.getText().trim();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || category.isEmpty() || description.isEmpty()) {
            showAlert("Please fill in all the fields.");
            return;
        }
        Book existingBook = DBUtils.getBookByIsbn(isbn);
        if (existingBook != null) {
            if (quantity <= 0) {
                showAlert("Quantity must be greater than 0.");
                return;
            }
            existingBook.setQuantity(existingBook.getQuantity() + quantity);
            DBUtils.updateBookFromDB(existingBook, event);
            showAlert("Book quantity has been updated.");
        } else {
            // ISBN chưa tồn tại, thêm sách mới
            Book newBook = new Book(isbn, title, author, publishYear, quantity, description, category);
            DBUtils.addBook(newBook, event);
            showAlert("New book has been added.");
        }
    }

    public void fillIsbn() {
        List<Button> listOfNumbers = Arrays.asList(button_1, button_2, button_3, button_4,
                button_5, button_6, button_7, button_8, button_9, button_0);
        for (Button button : listOfNumbers) {
            button.setOnAction(_ -> {
                String currentText = tf_bookISBN.getText();
                String newText = currentText + button.getText();
                tf_bookISBN.setText(newText);
            });
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
