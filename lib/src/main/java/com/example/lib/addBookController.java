package com.example.lib;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;

public class addBookController implements Initializable {
    @FXML
    public TextField tf_bookTitle;

    @FXML
    public TextField tf_bookAuthor;

    @FXML
    public TextField tf_bookEdition;

    @FXML
    public TextField tf_bookQuantity;

    @FXML
    public TextField tf_bookISBN;

    @FXML
    public TextArea ta_bookDescription;

    @FXML
    public Button button_addAbook;

    private BookController bookController;

    // Gán bookController từ bên ngoài để có thể gọi loadData
    public void setBookController(BookController bookController) {
        this.bookController = bookController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_addAbook.setOnAction(e -> {
            if (!tf_bookTitle.getText().trim().isEmpty() &&
                    !tf_bookAuthor.getText().trim().isEmpty() &&
                    !tf_bookISBN.getText().trim().isEmpty() &&
                    !ta_bookDescription.getText().trim().isEmpty() &&
                    !tf_bookQuantity.getText().trim().isEmpty() &&
                    !tf_bookEdition.getText().trim().isEmpty()) {

                try {
                    Book newBook = new Book(
                            tf_bookTitle.getText().trim(),
                            tf_bookAuthor.getText().trim(),
                            Integer.parseInt(tf_bookEdition.getText().trim()),
                            Integer.parseInt(tf_bookQuantity.getText().trim()),
                            "still available"  // Hoặc trạng thái mặc định, nếu có.
                    );

                    // Thêm ISBN và mô tả cho sách
                    // chỉ thủ thư mới nhìn thấy được isbn và description của sách
                    newBook.setIsbn(Integer.parseInt(tf_bookISBN.getText().trim()));
                    newBook.setDescription(ta_bookDescription.getText().trim());

                    // bấm button để thêm sách và chuyển sang scence mới
                    DBUtils.addBook(newBook, e);

                    // bơi vì sách mới được thêm này không có bookID nên sau khi tạo sách thì table
                    //view cần được load lại từ csdl để bookID được auto increment
                    // chuyển đến loadData trong bookController để update table view
                    if (bookController != null) {
                        bookController.loadData();
                    }


                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please enter valid numbers for ISBN, Edition, and Quantity fields.");
                    alert.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please fill in all the fields.");
                alert.show();
            }
        });
    }








}
