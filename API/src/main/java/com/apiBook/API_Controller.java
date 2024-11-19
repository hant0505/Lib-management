package com.apiBook;

import com.book.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.control.CheckBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings({"CallToPrintStackTrace", "OptionalGetWithoutIsPresent"})
public class API_Controller {
    @FXML
    private TableView<API_Book> bookTableView;
    //@FXML
    //private TableColumn<API_Book, CheckBox> table_checkBox;
    @FXML
    private TableColumn<API_Book, String> table_isbn;
    @FXML
    private TableColumn<API_Book, String> table_title;
    @FXML
    private TableColumn<API_Book, String> table_author;
    @FXML
    private TableColumn<API_Book, String> table_quantity;
    @FXML
    private TableColumn<API_Book, String> table_available;

    @FXML
    private TableColumn<API_Book, String> table_categories;

    //@FXML
    //private TableColumn<API_Book, String> table_description;

    //@FXML
    //private TableColumn<API_Book, CheckBox> table_checkBox;

    @FXML
    private TextField tf_search;

    @FXML
    private Button button_addBook;

    @FXML
    private Button button_deleteBook;

    ObservableList<API_Book> books = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        //loadDataFromAPI("");

        // dữ liệu các cột
        //table_checkBox.setCellValueFactory(param -> param.getValue().getCheckBox().selectedProperty());
        //table_checkBox.setCellFactory(CheckBoxTableCell.forTableColumn(table_checkBox));

        table_isbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        table_title.setCellValueFactory(new PropertyValueFactory<>("title"));
        table_author.setCellValueFactory(new PropertyValueFactory<>("author"));
        table_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        table_available.setCellValueFactory(new PropertyValueFactory<>("available"));
        table_categories.setCellValueFactory(new PropertyValueFactory<>("categories"));

        //table_description.setCellValueFactory(new PropertyValueFactory<>("description"));


        //loadData(); //hàm cập nhật dữ liệu sau khi tải
        // Không truyền từ khóa để lấy danh sách mặc định
        loadDataFromAPI("harry potter");
        // add
        button_addBook.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("add_book.fxml"));
                Parent root = loader.load();

                addBookController controller = loader.getController();
                // giúp addBookController có thể truy cập đến BookController đẻ truy cập đến loadData của lớp này
                //controller.setBookController(this);
                controller.setAPI_Controller(this);
                // giúp addBookController có thể truy cập đến apiController đẻ truy cập đến loadData của lớp này

                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

//        tf_search.setOnAction(e -> {
//            String query = tf_search.getText();
//            if (!query.isEmpty()) {
//                loadDataFromAPI(query);
//            } else {
//                showAlert("Vui lòng nhập từ khóa tìm kiếm!");
//            }
//        });

        // Thiết lập sự kiện tìm kiếm khi người dùng nhập vào TextField
        tf_search.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                // Tìm kiếm từ API khi người dùng nhập
                loadDataFromAPI(newValue);
            } else {
                // Nếu không có từ khóa, có thể gọi lại API với từ khóa mặc định hoặc làm gì đó
                loadDataFromAPI("harry potter");
            }
        });

        //delete
        button_deleteBook.setOnAction(_ -> deleteBook());
        //table_checkBox.setCellValueFactory(new PropertyValueFactory<>("checkBox"));

    }

    @FXML
    private void searchBook() {
        FilteredList<API_Book> filteredData = new FilteredList<>(books, _ -> true);
        tf_search.textProperty().addListener((_, _, newValue) -> filteredData.setPredicate(b -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();

            if (b.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (b.getAuthor().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else return b.getIsbn().toLowerCase().contains(lowerCaseFilter);
        }));

        // Tạo SortedList từ FilteredList
        SortedList<API_Book> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(bookTableView.comparatorProperty());

        // Đặt các sách đã lọc vào TableView
        bookTableView.setItems(sortedData);
    }


    @FXML
    private void deleteBook() {
        List<API_Book> bookToDelete = new ArrayList<>();
        for (API_Book book : books) {
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
                //DBUtils.deleteBookfromDB(bookToDelete); //Cho mysql thôi
            }
        }

    }

    /*
        cho DaTABASE thôi
     */
//    public void loadData() {
//        books.clear();
//        books.addAll(DBUtils.getBooks());
//        bookTableView.setItems(books);
//        System.out.println("Số lượng sách đã tải về: " + books.size());
//    }

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

    public void loadDataFromAPI(String query) {
        // Tạo task gọi API
        APIClientTask task = new APIClientTask(query);
        task.setOnSucceeded(event -> {
            List<API_Book> booksFromAPI = task.getValue();
            if (booksFromAPI.isEmpty()) {
                System.out.println("Không có sách nào được tải về.");
            } else {
                System.out.println("Số lượng sách đã tải về: " + booksFromAPI.size());
            }

            books.clear();
            books.addAll(booksFromAPI);
            bookTableView.setItems(books);

            // Gọi searchBook() để áp dụng bộ lọc tìm kiếm nếu cần
            searchBook();
        });

        task.setOnFailed(event -> {
            showAlert("Có lỗi khi tải dữ liệu từ API!");
        });

        // Chạy task trong một thread riêng biệt
        new Thread(task).start();
    }
}