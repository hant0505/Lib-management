package com.book;


import com.DatabaseConnection;
import com.effect.AlertUtils;
import com.user.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

@SuppressWarnings({"CallToPrintStackTrace", "OptionalGetWithoutIsPresent", "MismatchedQueryAndUpdateOfCollection"})
public class LibBooksController implements Initializable {
    ObservableList<Book> books = FXCollections.observableArrayList();
    @FXML
    private TableView<Book> bookTableView;
    @FXML
    private TableColumn<Book, String> table_bookIsbn;
    @FXML
    private TableColumn<Book, String> table_bookTitle;
    @FXML
    private TableColumn<Book, String> table_bookAuthor;
    @FXML
    private TableColumn<Book, Integer> table_bookPublishYear;
    @FXML
    private TableColumn<Book, Integer> table_bookQuantity;
    @FXML
    private TableColumn<Book, CheckBox> table_checkBox;
    @FXML
    private TableColumn<Book, String> table_bookCategory;
    @FXML
    private TextField tf_search;
    @FXML
    private Button button_addBook;
    @FXML
    private Button button_deleteBook;
    @FXML
    private Button button_update;
    @FXML
    private Button button_transactionsForLib;
    @FXML
    private Button button_home;
    @FXML
    private Button button_students;
    @FXML
    private Button button_account;

    public LibBooksController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        table_bookIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        table_bookTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        table_bookAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        table_bookPublishYear.setCellValueFactory(new PropertyValueFactory<>("publishYear"));
        table_bookQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        table_bookCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        table_checkBox.setCellValueFactory(new PropertyValueFactory<>("checkBox"));

        loadData();

        button_addBook.setOnAction(e -> com.user.DBUtils.changeScene(e, "/com/book/add_book.fxml", "Add Book"));
        button_home.setOnAction(e -> com.user.DBUtils.changeScene(e, "/com/home_librarian.fxml", "Home Librarian"));
        button_account.setOnAction(e -> com.user.DBUtils.changeScene(e, "/com/user/account_info_lib.fxml", "Account Information"));
        button_students.setOnAction(e -> com.user.DBUtils.changeScene(e, "/com/user/student_info.fxml", "Students Information"));
        button_transactionsForLib.setOnAction(e -> com.user.DBUtils.changeScene(e, "/com/book/transactions_lib.fxml", "Transactions Information"));
        button_deleteBook.setOnAction(_ -> deleteBook());

        searchBook();

        button_update.setOnAction(_ -> updateBook());

        //transaction
        bookTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
                if (selectedBook != null) {
                    Book detailsBook = DBUtils.getDetailsBookFromDB(selectedBook.getIsbn());
                    BookSession.setSelectedBook(detailsBook);
                    com.user.DBUtils.changeScene(event, "/com/book/details_book.fxml", "Book Details");
                }
            }
        });
    }

    @FXML
    private void deleteBook() {
        List<Book> bookToDelete = new ArrayList<>();
        for (Book book : books) {
            if (book.getCheckBox().isSelected()) {
                bookToDelete.add(book);
            }
        }

        if (bookToDelete.isEmpty()) {
            AlertUtils.warningAlert("No book chosen!");
            return;
        }

        Alert a = AlertUtils.confirmAlert("Are you sure want to delete " + bookToDelete.size() + " book sets?");
        if (a.showAndWait().get() == ButtonType.OK) {
            List<Book> notDeletableBooks = new ArrayList<>();
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT isbn FROM transactions WHERE isbn = ? AND returnedDate IS NULL")) {

                for (Book b : bookToDelete) {
                    ps.setString(1, b.getIsbn());
                    if (ps.executeQuery().next()) {
                        notDeletableBooks.add(b);
                    }
                }

                if (!notDeletableBooks.isEmpty()) {
                    StringBuilder errorMessage = new StringBuilder("The following books cannot be deleted as they are borrowed:\n");
                    for (Book b : notDeletableBooks) {
                        errorMessage.append("- ").append(b.getTitle()).append(" (ISBN: ").append(b.getIsbn()).append(")\n");
                    }
                    AlertUtils.errorAlert(errorMessage.toString());
                    return;
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // Xóa sách khỏi danh sách và cập nhật cơ sở dữ liệu
            books.removeAll(bookToDelete);
            bookTableView.getItems().removeAll(bookToDelete); // Đồng bộ giao diện
            DBUtils.deleteBookFromDB(bookToDelete);
        }
    }

    @FXML
    private void searchBook() {
        FilteredList<Book> filteredData = new FilteredList<>(books, _ -> true);
        tf_search.textProperty().addListener((_, _, newValue) -> filteredData.setPredicate(b -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();
            if (b.getIsbn().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (b.getAuthor().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (String.valueOf(b.getPublishYear()).toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (String.valueOf(b.getQuantity()).toLowerCase().contains(lowerCaseFilter)) {
                return true;

            } else if (b.getCategory().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else return b.getTitle().toLowerCase().contains(lowerCaseFilter);
        }));
        SortedList<Book> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(bookTableView.comparatorProperty());
        bookTableView.setItems(sortedData);
    }

    @FXML
    private void updateBook() {
        List<Book> selectedBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.getCheckBox().isSelected()) {
                selectedBooks.add(book);
            }
        }

        if (selectedBooks.size() != 1) {
            AlertUtils.warningAlert("Please choose ONLY 1 book to update!");
            return;
        }

        Book selected = selectedBooks.getFirst(); // getFirst ~ get(0)
        try {
            if (selected == null) {
                AlertUtils.warningAlert("Please choose ONLY 1 book to update!");
            }

            Stage currentStage = (Stage) button_update.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("update_Book.fxml"));
            Parent root = loader.load();

            UpdateBookController controller = loader.getController();
            if (selected != null) {
                controller.setBook(selected);
            }

            Stage stage = new Stage();
            stage.setTitle("Book Information Update");
            stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/com/Image/logo.png"))));
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void loadData() {
        books.clear();
        books.addAll(DBUtils.getBooks());
        bookTableView.setItems(books);
        System.out.println("Số lượng sách đã tải về: " + books.size());
    }
}
