package com.book;

import com.effect.AlertUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;


public class AddBookController implements Initializable {
    // For setting allow to edit after enter ISBN
    TextField[] textFields;
    String imagePath = null;  // default null cho link ảnh
    byte[] image = null; // default null cho ảnh từ pc
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
    private ImageView imageView_cover;
    @FXML
    private Button button_check;
    @FXML
    private Button button_addABook;
    @FXML
    private Button button_back;
    @FXML
    private Button button_import;
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

        textFields = new TextField[]{tf_bookAuthor, tf_bookCategory, tf_bookQuantity, tf_bookTitle, tf_bookPublishYear};
        //Check fill ISBN first
        checkIsbnAndSetHandlers();
        //Re-check if ISBN is filled?
        tf_bookISBN.textProperty().addListener((_, _, _) -> checkIsbnAndSetHandlers());

        button_addABook.setOnAction(this::handleAddBook);
        button_back.setOnAction(e -> com.user.DBUtils.changeScene(e, "/com/book/bookInfo_lib.fxml", "Books Information"));

        button_import.setOnAction(_ -> handleSelectImage());

        tf_bookISBN.setOnAction(_ -> fetchBookDetails());
    }

    private void checkIsbn() {
        String isbn = tf_bookISBN.getText().trim();
        if (isbn.isEmpty()) AlertUtils.errorAlert("Please enter an ISBN to check.");

        Book existingBook = DBUtils.getBookByIsbn(isbn);
        if (existingBook != null) {
            // ISBN co trong database
            tf_bookTitle.setText(existingBook.getTitle());
            tf_bookAuthor.setText(existingBook.getAuthor());
            tf_bookCategory.setText(existingBook.getCategory());
            tf_bookPublishYear.setText(String.valueOf(existingBook.getPublishYear()));
            ta_bookDescription.setText(existingBook.getDescription());
            tf_bookQuantity.setText(String.valueOf(existingBook.getQuantity()));
            if (existingBook.getImagePath() != null) {
                imageView_cover.setImage(new Image(existingBook.getImagePath()));
                button_import.setVisible(false);
            } else {
                image = existingBook.getImage();
                if (image != null && image.length > 0) {
                    ByteArrayInputStream bis = new ByteArrayInputStream(image);
                    imageView_cover.setImage(new Image(bis));
                    button_import.setVisible(false);
                }
            }

            AlertUtils.infoAlert("ISBN existed. Book details loaded.");
        } else {
            API_book apiClient = new API_book();
            List<Book> books = apiClient.getBooksByISBN(isbn);

            if (!books.isEmpty()) {
                Book book = books.getFirst();
                Platform.runLater(() -> {
                    tf_bookTitle.setText(book.getTitle());
                    tf_bookAuthor.setText(book.getAuthor());
                    tf_bookCategory.setText(book.getCategory());
                    tf_bookPublishYear.setText(String.valueOf(book.getPublishYear()));
                    ta_bookDescription.setText(book.getDescription());
                    tf_bookQuantity.setText("1"); // default value
                    if (!book.getImagePath().isEmpty()) {
                        imageView_cover.setImage(new Image(book.getImagePath()));
                        button_import.setVisible(false);
                    } else {
                        imageView_cover.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/Image/cover_not_found.png")).toExternalForm()));
                        button_import.setVisible(true);
                    }
                });
                AlertUtils.infoAlert("Book information loaded from API.");
            } else {
                Platform.runLater(() -> {
                    //Clear old ISBN info first
                    textFields = new TextField[]{tf_bookAuthor, tf_bookCategory, tf_bookQuantity, tf_bookTitle, tf_bookPublishYear};
                    for (TextField tf : textFields) tf.clear();
                    ta_bookDescription.clear();
                    imageView_cover.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/Image/cover_not_found.png")).toExternalForm()));
                    button_import.setVisible(true);

                    AlertUtils.infoAlert("No book found with ISBN: " + isbn);
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

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || category.isEmpty() || description.isEmpty()) {
            AlertUtils.warningAlert("Please fill in all the fields.");
            return;
        }
        Book existingBook = DBUtils.getBookByIsbn(isbn);
        // existing book in database -> update quantity
        if (existingBook != null) {
            if (quantity <= 0) {
                AlertUtils.warningAlert("Quantity must be greater than 0.");
            } else {
                existingBook.setQuantity(existingBook.getQuantity() + quantity);
                existingBook.setTitle(title);
                existingBook.setAuthor(author);
                DBUtils.updateBookFromDB(existingBook);
                AlertUtils.infoAlert("Book quantity has been updated.");
                com.user.DBUtils.changeScene(event, "/com/book/bookInfo_lib.fxml", "Books Information");
            }
        } else {

            API_book api = new API_book();
            List<Book> booksFromAPI = api.getBooksByISBN(isbn);
            //KO CÓ TRONG API + DATABASE -> Import ảnh từ pc
            if (booksFromAPI.isEmpty()) {
                System.out.println("Không tìm thấy sách trong API.");

                Book newBook = new Book(isbn, title, author, publishYear, quantity, description, category, image);
                DBUtils.addBook(newBook, event);
                AlertUtils.infoAlert("New book (not in API/data has been added.");
            } else { //CÓ trong API but not in database
                Book newBook = booksFromAPI.getFirst();
                DBUtils.addBook(newBook, event);
                AlertUtils.infoAlert("New book has been added.");
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
     * Hàm lấy thông tin sách từ API và tự điền vào giao diện.
     * Task hoặc Service gọi API trong một luồng riêng biệt,
     */
    private void fetchBookDetails() {
        String isbn = tf_bookISBN.getText().trim();
        if (!isbn.isEmpty()) {
            // Tạo Task để thực hiện gọi API
            Task<List<Book>> task = new Task<>() {
                @Override
                protected List<Book> call() {
                    API_book apiClient = new API_book();
                    return apiClient.getBooksByISBN(isbn);
                }
            };

            task.setOnSucceeded(_ -> {
                List<Book> books = task.getValue();
                if (!books.isEmpty()) {
                    Book book = books.getFirst();
                    tf_bookTitle.setText(book.getTitle());
                    tf_bookAuthor.setText(book.getAuthor());
                    tf_bookCategory.setText(book.getCategory());
                    tf_bookPublishYear.setText(String.valueOf(book.getPublishYear()));
                    ta_bookDescription.setText(book.getDescription());
                    tf_bookQuantity.setText("1"); // Giá trị mặc định

                    if (book.getImagePath() != null) {
                        imagePath = book.getImagePath();
                    }
                } else {
                    AlertUtils.errorAlert("Cannot find book with ISBN: " + isbn);
                }
            });

            task.setOnFailed(_ -> AlertUtils.errorAlert("Error when take data from API."));

            // Chạy task trong một luồng riêng
            new Thread(task).start();
        } else {
            AlertUtils.warningAlert("Please enter ISBN first!");
        }
    }

    @FXML
    private void handleSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select book cover image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try {
                image = Files.readAllBytes(file.toPath());
                Image importedImage = new Image(file.toURI().toString());
                imageView_cover.setImage(importedImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void checkIsbnAndSetHandlers() {
        EventHandler<MouseEvent> warningHandler = _ -> AlertUtils.warningAlert("Please enter ISBN first!");
        // Check if ISBN is empty
        boolean isIsbnEmpty = tf_bookISBN.getText().isEmpty();
        for (TextField textField : textFields) {
            if (isIsbnEmpty) textField.setOnMouseClicked(warningHandler);
            else textField.setOnMouseClicked(null); // Remove handler

            textField.setEditable(!isIsbnEmpty);
        }
        if (isIsbnEmpty) ta_bookDescription.setOnMouseClicked(warningHandler);
        else ta_bookDescription.setOnMouseClicked(null); // Remove handler

        ta_bookDescription.setEditable(!isIsbnEmpty);
    }
}
