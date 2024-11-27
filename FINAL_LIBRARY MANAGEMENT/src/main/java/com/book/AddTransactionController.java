package com.book;

import com.effect.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static com.user.DBUtils.canBorrowMoreBooks;
import static com.user.DBUtils.isUsernameExist;

public class AddTransactionController implements Initializable {
    @FXML
    private TextField tf_username;

    @FXML
    private Label label_borrowedDate;

    @FXML
    private Label label_dueDate;

    @FXML
    private ChoiceBox<Integer> cb_quantity;

    @FXML
    private Button button_submit;
    @FXML
    private Button button_back;


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


    private Book currentBook;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // GET BOOK
        this.currentBook = BookSession.getSelectedBook();
        String isbn = currentBook.getIsbn();
        currentBook = DBUtils.getBookByIsbn(isbn);
        showBookDetails();

        ObservableList<Integer> quantities = FXCollections.observableArrayList();
        for (int i = 1; i <= 5; i++) {
            quantities.add(i);
        }
        cb_quantity.setItems(quantities);

        LocalDate borrowedDate = LocalDate.now();
        LocalDate dueDate = borrowedDate.plusDays(30);

        label_borrowedDate.setText("Borrow Date: \n" + borrowedDate);
        label_dueDate.setText("Due Date: \n" + dueDate);

        button_submit.setOnAction(event -> {
            if (!tf_username.getText().isEmpty() && cb_quantity.getValue() != null) {
                String username = tf_username.getText();
                int quantity = cb_quantity.getValue();

                if (!isUsernameExist(username)) AlertUtils.errorAlert("Username does not exist");

                if (!canBorrowMoreBooks(username)) AlertUtils.infoAlert("User can't borrow more book!");

                if (isUsernameExist(username) && canBorrowMoreBooks(username)) {
                    if (quantity <= currentBook.getQuantity()) {
                        for (int i = 1; i <= quantity; i++) {
                            DBUtils.addTransaction(currentBook, username, borrowedDate, dueDate, "Borrowed");
                            currentBook.setQuantity(currentBook.getQuantity() - 1);
                            DBUtils.updateBookFromDB(currentBook);
                        }
                        if (currentBook.getQuantity() == 0) currentBook.setAvailable(false);
                        AlertUtils.infoAlert("The book has been successfully borrowed.");
                        com.user.DBUtils.changeScene(event, "/com/book/transactions_lib.fxml", "Transactions Information");
                    } else AlertUtils.warningAlert("You are trying to borrow more books than are available!");
                }
            } else AlertUtils.errorAlert("Please fill all the fields !");
        });

        button_back.setOnAction(event -> com.user.DBUtils.changeScene(event, "/com/book/bookInfo_lib.fxml", "Book Information"));
    }

    private void showBookDetails() {
        if (currentBook != null) {
            label_title.setText(currentBook.getTitle());
            label_author.setText(currentBook.getAuthor());
            label_quantity.setText("Quantity: " + currentBook.getQuantity());
            label_category.setText("Category: " + currentBook.getCategory());
            label_publishYear.setText("Publish Year: " + currentBook.getPublishYear());

            Image coverImage = null;
            byte[] image;
            if (currentBook.getImagePath() != null) {
                coverImage = new Image(currentBook.getImagePath(), true);
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
