package com.example.lib;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddTransactionController implements Initializable {
    @FXML
    private TextField tf_username;

    @FXML
    private DatePicker dp_borrowedDate;

    @FXML
    private Label label_dueDate;

    @FXML
    private ChoiceBox<Integer> cb_quantity;

    @FXML
    private Button button_submit;

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
    private ImageView image_coverBook;

    @FXML
    private Button button_back;

    private Book currentBook;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // add transact
        this.currentBook = BookSession.getSelectedBook();
        showBookDetails();

        ObservableList<Integer> quantities = FXCollections.observableArrayList();
        for (int i = 1; i <= 5; i++) {
            quantities.add(i);
        }
        cb_quantity.setItems(quantities);

        dp_borrowedDate.valueProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                LocalDate dueDate = newValue.plusDays(30);
                label_dueDate.setText("Due Date: \n" + dueDate);
            }
        });

        button_submit.setOnAction(event -> {
            if (!tf_username.getText().isEmpty() && dp_borrowedDate.getValue() != null && cb_quantity.getValue() != null) {
                String username = tf_username.getText();
                LocalDate borrowedDate = dp_borrowedDate.getValue();
                LocalDate dueDate = borrowedDate.plusDays(30);
                int quantity = cb_quantity.getValue();

                if (quantity <= currentBook.getQuantity()) {
                    for (int i = 1; i <= quantity; i++) {
                        DBUtils.addTransact(currentBook, username, borrowedDate, dueDate, "Borrowed", event);
                        currentBook.setQuantity(currentBook.getQuantity() - 1);
                        System.out.println(currentBook.getQuantity());
                        DBUtils.updateBookFromDB(currentBook, event); // Cập nhật cơ sở dữ liệu sau khi giảm số lượng
                    }
                    if (currentBook.getQuantity() == 0) currentBook.setAvailable(false);
                    showAlert("The book has been successfully borrowed.");
                    DBUtils.changeScene(event, "bookTransact.fxml", "Book Transaction");
                } else {
                    showAlert("You are trying to borrow more books than are available !");
                }
            } else {
                showAlert("Please fill all the fields !");
            }
        });
        button_back.setOnAction(event -> DBUtils.changeScene(event, "bookInfo.fxml", "Book Information"));
    }

    private void showBookDetails() {
        if (currentBook != null) {
            label_title.setText(currentBook.getTitle());
            label_author.setText(currentBook.getAuthor());
            label_quantity.setText("Quantity: " + currentBook.getQuantity());
            label_category.setText("Category: " + currentBook.getCategory());
            label_publishYear.setText("Publish Year: " + currentBook.getPublishYear());

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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
