package com.example.lib;

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

    @FXML
    public Button button_1;

    @FXML
    public Button button_2;

    @FXML
    public Button button_3;

    @FXML
    public Button button_4;

    @FXML
    public Button button_5;

    @FXML
    public Button button_6;

    @FXML
    public Button button_7;

    @FXML
    public Button button_8;

    @FXML
    public Button button_9;

    @FXML
    public Button button_0;

    private BookController bookController;

    // Gán bookController từ bên ngoài để có thể gọi loadData
    public void setBookController(BookController bookController) {
        this.bookController = bookController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillIsbn();
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
                            "still available"  // sách mới luôn mặc định là available
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

    // set button numbers for isbn
    public void fillIsbn() {
        List<Button> listOfNumbers = Arrays.asList(button_1, button_2, button_3, button_4,
                button_5, button_6, button_7, button_8, button_9, button_0);
        for (Button button : listOfNumbers) {
            button.setOnAction(_ -> {
                String currentText = tf_bookISBN.getText();
                String newText = currentText + button.getText();
                tf_bookISBN.setText(newText);
                System.out.println(button.getText());
            });
        }
    }









}
