package com.example.lib;

import com.mysql.cj.conf.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


import javax.security.auth.callback.Callback;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

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
    private TableColumn<Book, Boolean> table_checkBox;

    @FXML
    private Button button_deleteBook;



    @FXML
    private void getAddView(MouseEvent event) {

    }

    @FXML
    private void refreshTable(MouseEvent event) {
        

    }

    @FXML
    private void print(MouseEvent event) {

    }

    public BookController() {}

    ObservableList<Book> books = FXCollections.observableArrayList();

    public BookController(ObservableList<Book> books, TableView<Book> bookTable) {
        this.bookTableView = bookTable;
        this.books = books;
    }


    @FXML
    public void initialize() {
        table_checkBox.setCellValueFactory(new PropertyValueFactory<>("selected"));
        table_checkBox.setCellFactory(CheckBoxTableCell.forTableColumn(table_checkBox));

        table_bookID.setCellValueFactory(new PropertyValueFactory<>("bookID"));
        table_bookTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        table_bookAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        table_bookEdition.setCellValueFactory(new PropertyValueFactory<>("edition"));
        table_bookQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        table_bookAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));

        loadData(); //hàm cập nhật dữ liệu sau khi tải

        button_addBook.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("add_book.fxml"));
                Parent root = loader.load();

                addBookController controller = loader.getController();
                controller.setBookController(this); // giúp addBookController có thể truy cập đến BookController đẻ truy cập đến loadData của lớp này

                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        //delete
        button_deleteBook.setOnAction(e -> {
            ObservableList<Book> bookList = FXCollections.observableArrayList();
            for (Book book : books) {
                if(book.isSelected()) {
                    bookList.add(book);
                }
            }

            for (Book book : bookList) {
                DBUtils.deleteSelectedBook(book.getBookID());
            }
        });


    }

    public void loadData() {
        books.clear();
        books.addAll(DBUtils.getBooks());
        bookTableView.setItems(books);
        System.out.println("Số lượng sách đã tải về: " + books.size());
    }


}
