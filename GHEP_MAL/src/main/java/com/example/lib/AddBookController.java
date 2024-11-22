package com.example.lib;

import javafx.application.Platform;
import javafx.concurrent.Task;
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

        ///API: Khi bấm nút tìm kiếm hoặc nhập ISBN xong
        tf_bookISBN.setOnAction(event -> fetchBookDetails());
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
            ///
            tf_bookCategory.setText(existingBook.getCategory());
            tf_bookPublishYear.setText(String.valueOf(existingBook.getPublishYear()));
            ta_bookDescription.setText(existingBook.getDescription());
            tf_bookQuantity.setText(String.valueOf(existingBook.getQuantity()));

            showAlert("ISBN already exists. Book details loaded.");
        } else {
            // Nếu ISBN không có trong database, gọi API để lấy thông tin sách
            APIclient_ggbook apiClient = new APIclient_ggbook();
            List<Book> books = apiClient.getBooksByISBN(isbn);

            if (!books.isEmpty()) {
                // Nếu tìm thấy thông tin sách từ API
                Book book = books.get(0);

                // Cập nhật giao diện
                Platform.runLater(() -> {
                    tf_bookTitle.setText(book.getTitle());
                    tf_bookAuthor.setText(book.getAuthor());
                    tf_bookCategory.setText(book.getCategory());
                    tf_bookPublishYear.setText(String.valueOf(book.getPublishYear()));
                    ta_bookDescription.setText(book.getDescription());
                    tf_bookQuantity.setText("1"); // Giá trị mặc định
                });

                showAlert("Book Info loaded from API.");
            } else {
                // Nếu không tìm thấy thông tin sách từ API
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("No book found with ISBN: " + isbn);
                    alert.show();
                });
            }
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
        /// HANT
        String imagePath = "";  // Đặt mặc định là rỗng

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || category.isEmpty() || description.isEmpty()) {
            showAlert("Please fill in all the fields.");
            return;
        }
        System.out.println("Ko lấy à:<<");///DEBUG

        Book existingBook = DBUtils.getBookByIsbn(isbn);

        if (existingBook != null) {
            if (quantity <= 0) {
                showAlert("Quantity must be greater than 0.");
                return;
            }
            existingBook.setQuantity(existingBook.getQuantity() + quantity);
            /// HANT: Cập nhật đường dẫn ảnh
            //existingBook.setImagePath(imagePath);
            //System.out.println("LA GI : " + existingBook.getImagePath());

            DBUtils.updateBookFromDB(existingBook, event);
            showAlert("Book quantity has been updated.");
        } else {
            // ISBN chưa tồn tại, thêm sách mới
            System.out.println("Khong co trong DATABASE"); ///DEBUG
            //Book newBook = new Book(isbn, title, author, publishYear, quantity, description, category,imagePath);
            APIclient_ggbook api = new APIclient_ggbook();
            List<Book> booksFromAPI = api.getBooksByISBN(isbn);

            ///Nếu rỗng KO CÓ TRONG API
            if (booksFromAPI.isEmpty()) {
                System.out.println("Không tìm thấy sách trong API."); /// DEBUG
                Book newBook = new Book(isbn, title, author, publishYear, quantity, description, category, imagePath);
                newBook.setImagePath("Not found");
                // Lưu sách mới vào cơ sở dữ liệu
                DBUtils.addBook(newBook, event);
                showAlert("New book - no api - no data - has been added.");
            } else {
                Book newBook = booksFromAPI.get(0);
                System.out.println("TEST:" + newBook.getImagePath());///DEBUG: Af la ko co trong api lun
                if (newBook.getImagePath().isEmpty()) {
                    newBook.setImagePath("Not found");
                }
                DBUtils.addBook(newBook, event);
                showAlert("New book has been added.");
            }

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


    /**
     *     Hàm lấy thông tin sách từ API và tự điền vào giao diện.
     *
     *    Task hoặc Service gọi API trong một luồng riêng biệt,
     */
    private void fetchBookDetails() {
        String isbn = tf_bookISBN.getText().trim();
        if (!isbn.isEmpty()) {
            // Tạo Task để thực hiện việc gọi API
            Task<List<Book>> task = new Task<List<Book>>() {
                @Override
                protected List<Book> call() throws Exception {
                    APIclient_ggbook apiClient = new APIclient_ggbook();
                    return apiClient.getBooksByISBN(isbn);
                }
            };

            task.setOnSucceeded(event -> {
                List<Book> books = task.getValue();
                if (!books.isEmpty()) {
                    Book book = books.get(0);
                    tf_bookTitle.setText(book.getTitle());
                    tf_bookAuthor.setText(book.getAuthor());
                    tf_bookCategory.setText(book.getCategory());
                    tf_bookPublishYear.setText(String.valueOf(book.getPublishYear()));
                    ta_bookDescription.setText(book.getDescription());
                    tf_bookQuantity.setText("1"); // Giá trị mặc định

                    // Nếu có ảnh, cập nhật đường dẫn ảnh cho book
                    System.out.println("Dữ liệu sách: " + book.getTitle() + ", Image Path: " + book.getImagePath());

                    if (book.getImagePath() != null) {
                        String imagePath = book.getImagePath();
                        // Bạn có thể lưu imagePath vào database tại đây nếu cần
                        System.out.println("Image path: " + imagePath);
                    }
                } else {
                    showAlert("Không tìm thấy thông tin sách với ISBN: " + isbn);
                }
            });

            task.setOnFailed(event -> {
                showAlert("Lỗi khi lấy dữ liệu từ API.");
            });

            // Chạy task trong một luồng riêng
            new Thread(task).start();
        } else {
            showAlert("Vui lòng nhập ISBN trước.");
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
