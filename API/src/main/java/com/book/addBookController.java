package com.book;

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
    public TextField tf_bookCategories;

    @FXML
    public TextField tf_bookQuantity;

    @FXML
    public TextField tf_bookISBN;

    @FXML
    public TextArea ta_bookDescription;

    @FXML
    public Button button_addABook;

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
    @FXML
    private Button button_back;

    private BookController bookController;

    // Gán bookController từ bên ngoài để có thể gọi loadData
    public void setBookController(BookController bookController) {
        this.bookController = bookController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillIsbn();

        // Khi bấm nút tìm kiếm hoặc nhập ISBN xong
        tf_bookISBN.setOnAction(event -> fetchBookDetails());

//        button_addABook.setOnAction(e -> {
//            if (!tf_bookTitle.getText().trim().isEmpty() &&
//                    !tf_bookAuthor.getText().trim().isEmpty() &&
//                    !tf_bookISBN.getText().trim().isEmpty() &&
//                    !ta_bookDescription.getText().trim().isEmpty() &&
//                    !tf_bookQuantity.getText().trim().isEmpty() &&
//                    !tf_bookCategories.getText().trim().isEmpty()) {
//
//                try {
//                    Book newBook = new Book(
//                            tf_bookTitle.getText().trim(),
//                            tf_bookAuthor.getText().trim(),
//                            Integer.parseInt(tf_bookCategories.getText().trim()),
//                            Integer.parseInt(tf_bookQuantity.getText().trim()),
//                            "still available"  // sách mới luôn mặc định là available
//                    );
//
//                    // Thêm ISBN và mô tả cho sách
//                    // chỉ thủ thư mới nhìn thấy được isbn và description của sách
//                    newBook.setIsbn(Integer.parseInt(tf_bookISBN.getText().trim()));
//                    newBook.setDescription(ta_bookDescription.getText().trim());
//
//                    // bấm button để thêm sách và chuyển sang scence mới
//                    DBUtils.addBook(newBook, e);
//
//                    // bơi vì sách mới được thêm này không có bookID nên sau khi tạo sách thì table
//                    //view cần được load lại từ csdl để bookID được auto increment
//                    // chuyển đến loadData trong bookController để update table view
//                    if (bookController != null) {
//                        bookController.loadData();
//                    }
//
//                } catch (NumberFormatException ex) {
//                    Alert alert = new Alert(Alert.AlertType.ERROR);
//                    alert.setContentText("Please enter valid numbers for ISBN, Edition, and Quantity fields.");
//                    alert.show();
//                }
//            } else {
//                Alert alert = new Alert(Alert.AlertType.ERROR);
//                alert.setContentText("Please fill in all the fields.");
//                alert.show();
//            }
//        });

        button_addABook.setOnAction(e -> {
            try {
                // Kiểm tra các trường có được điền hay không
                if (tf_bookTitle.getText().trim().isEmpty() ||
                        tf_bookAuthor.getText().trim().isEmpty() ||
                        tf_bookISBN.getText().trim().isEmpty() ||
                        ta_bookDescription.getText().trim().isEmpty() ||
                        tf_bookQuantity.getText().trim().isEmpty() ||
                        tf_bookCategories.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("Please fill in all the fields.");
                }

                // Kiểm tra ISBN và số lượng có phải số không
                String isbnText = tf_bookISBN.getText().trim();
                String quantityText = tf_bookQuantity.getText().trim();

                if (!isbnText.matches("\\d+")) {
                    throw new NumberFormatException("ISBN must be a number.");
                }
                if (!quantityText.matches("\\d+")) {
                    throw new NumberFormatException("Quantity must be a number.");
                }

                // Tạo đối tượng Book
                Book newBook = new Book(
                        tf_bookTitle.getText().trim(),
                        tf_bookAuthor.getText().trim(),
                        tf_bookCategories.getText().trim(),
                        Integer.parseInt(quantityText),
                        "Available" // sách mặc định là available
                );

                newBook.setIsbn(isbnText);
                newBook.setDescription(ta_bookDescription.getText().trim());

                // Gọi DBUtils để thêm sách vào CSDL
                DBUtils.addBook(newBook, e);

                // Load lại dữ liệu trong bảng
                if (bookController != null) {
                    bookController.loadData();
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("ISBN và Quantity phải là số.");
                alert.show();
            } catch (IllegalArgumentException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(ex.getMessage());
                alert.show();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Có lỗi xảy ra: " + ex.getMessage());
                alert.show();
                ex.printStackTrace();
            }
        });

        button_back.setOnAction(event -> DBUtils.changeScene(event, "bookInfo.fxml", "Book Information"));
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
            });
        }
    }

    // Hàm lấy thông tin sách từ API và tự điền vào giao diện
    private void fetchBookDetails() {
        String isbn = tf_bookISBN.getText().trim();
        if (!isbn.isEmpty()) {
            APIclient_ggbook apiClient = new APIclient_ggbook();
            List<Book> books = apiClient.getBooksByISBN(isbn);

            if (!books.isEmpty()) {
                // Lấy sách đầu tiên từ kết quả
                Book book = books.get(0);
                tf_bookTitle.setText(book.getTitle());
                tf_bookAuthor.setText(book.getAuthor());
                tf_bookCategories.setText(book.getCategories());
                ta_bookDescription.setText(book.getDescription());
                tf_bookQuantity.setText("1"); // Giá trị mặc định
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Không tìm thấy thông tin sách với ISBN: " + isbn);
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Vui lòng nhập ISBN trước.");
            alert.show();
        }
    }







}
