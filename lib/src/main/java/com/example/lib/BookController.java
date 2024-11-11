package com.example.lib;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.CheckBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings({"CallToPrintStackTrace", "OptionalGetWithoutIsPresent"})
public class BookController {
    @FXML
    private TableView<Book> bookTableView;

    @FXML
    private TableColumn<Book, Integer> table_bookID;

    @FXML
    private TableColumn<Book, String> table_bookTitle;

    @FXML
    private TableColumn<Book, String> table_bookAuthor;

    @FXML
    private TableColumn<Book, Integer> table_bookEdition;

    @FXML
    private TableColumn<Book, Integer> table_bookQuantity;

    @FXML
    private TableColumn<Book, String> table_bookAvailable;


    @FXML
    private TextField tf_search;

    @FXML
    private Button button_addBook;

    @FXML
    private TableColumn<Book,CheckBox> table_checkBox;

    @FXML
    private Button button_deleteBook;

    @FXML
    private Button button_update;

    public BookController() {}

    ObservableList<Book> books = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // dữ liệu từng cột
        table_bookID.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        table_bookTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        table_bookAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        table_bookEdition.setCellValueFactory(new PropertyValueFactory<>("edition"));
        table_bookQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        table_bookAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));
        table_checkBox.setCellValueFactory(new PropertyValueFactory<>("checkBox"));

        loadData(); //hàm cập nhật dữ liệu sau khi tải

        //add
        button_addBook.setOnAction(_ -> addBook());

        //delete
        button_deleteBook.setOnAction(_ -> deleteBook());

        //searching
        searchBook();

        //update
        button_update.setOnAction(_ -> updateBook());
    }

    @FXML
    private void addBook() {
        try {
            Stage currentStage = (Stage) button_addBook.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("add_book.fxml"));
            Parent root = loader.load();

            addBookController controller = loader.getController();
            controller.setBookController(this); // giúp addBookController có thể truy cập đến BookController đẻ truy cập đến loadData của lớp này

            Stage stage = new Stage();
            stage.setTitle("Book Information");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @FXML
    private void deleteBook() {
        List<Book> bookToDelete = new ArrayList<>();
        for (Book book : books) {
            if (book.getCheckBox().isSelected()) {
                bookToDelete.add(book);
            }
        }

        // kiểm tra xem sách đã được selected chưa
        if (bookToDelete.isEmpty()) {
            showAlert("Chưa có sách nào được chọn !");
        } else {
            if (areUsure("Bạn có chắc muốn xóa " + bookToDelete.size() + " quyển sách không ?")) {
                books.removeAll(bookToDelete);
                DBUtils.deleteBookfromDB(bookToDelete);
            }
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
            if (b.getBookID().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (b.getAuthor().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (b.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else return false;
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
            showAlert("Vui lòng chọn một sách duy nhất để cập nhật !");
            return;
        }

        Book selected = selectedBooks.getFirst(); // getFirst ~ get(0)
        try {

            if (selected == null) {
                showAlert("Hãy chọn sách cần cập nhật !");
            }

            // Đóng cửa sổ hiện tại trước khi mở cửa sổ mới
            Stage currentStage = (Stage) button_update.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("update_Book.fxml"));
            Parent root = loader.load();

            updateBookController controller = loader.getController();
            if (selected != null) {
                controller.setBook(selected); // giúp updateBookController có thể truy cập đến BookController đẻ truy cập đến loadData của lớp này
            }

            Stage stage = new Stage();
            stage.setTitle("Book Information");
            stage.setScene(new Scene(root));
            stage.show(); // data will be updated after wiindowclose

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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Boolean areUsure(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().get() == ButtonType.OK;
    }

}
