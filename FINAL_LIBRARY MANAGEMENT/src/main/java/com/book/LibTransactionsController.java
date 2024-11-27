package com.book;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class LibTransactionsController implements Initializable {
    ObservableList<Transaction> TransactionList = FXCollections.observableArrayList();
    @FXML
    private TableView<Transaction> transactionTableView;
    @FXML
    private TableColumn<Transaction, Integer> table_transactionID;
    @FXML
    private TableColumn<Transaction, String> table_bookTitle;
    @FXML
    private TableColumn<Transaction, String> table_username;
    @FXML
    private TableColumn<Transaction, LocalDate> table_borrowedDate;
    @FXML
    private TableColumn<Transaction, LocalDate> table_dueDate;
    @FXML
    private TableColumn<Transaction, LocalDate> table_returnedDate;
    @FXML
    private TableColumn<Transaction, String> table_status;
    @FXML
    private Button button_home;
    @FXML
    private Button button_students;
    @FXML
    private Button button_account;
    @FXML
    private Button button_booksForLib;
    @FXML
    private Button button_addTransaction;
    @FXML
    private TextField tf_searchTransaction;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        table_transactionID.setCellValueFactory(new PropertyValueFactory<>("TransactionID"));
        table_bookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        table_username.setCellValueFactory(new PropertyValueFactory<>("username"));
        table_borrowedDate.setCellValueFactory(new PropertyValueFactory<>("borrowedDate"));
        table_dueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        table_returnedDate.setCellValueFactory(new PropertyValueFactory<>("returnedDate"));
        table_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        transactionTableView.setItems(TransactionList);

        loadData();

        //change scene via menu buttons
        button_booksForLib.setOnAction(event -> com.user.DBUtils.changeScene(event, "/com/book/bookInfo_lib.fxml", "Book Information"));
        button_home.setOnAction(event -> com.user.DBUtils.changeScene(event, "/com/home_librarian.fxml", "Home Librarian"));
        button_students.setOnAction(event -> com.user.DBUtils.changeScene(event, "/com/user/student_info.fxml", "Students Information"));
        button_account.setOnAction(event -> com.user.DBUtils.changeScene(event, "/com/user/account_info_lib.fxml", "Account Information"));
        //search Transaction
        searchTransaction();

        button_addTransaction.setOnAction(event -> com.user.DBUtils.changeScene(event, "/com/book/bookInfo_lib.fxml", "Book Information"));

    }

    public void searchTransaction() {
        FilteredList<Transaction> filteredData = new FilteredList<>(TransactionList, _ -> true);
        tf_searchTransaction.textProperty().addListener((_, _, newValue) -> filteredData.setPredicate(b -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();
            if (b.getBookTitle().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (b.getStatus().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (String.valueOf(b.getTransactionID()).toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else return b.getUsername().toLowerCase().contains(lowerCaseFilter);
        }));
        SortedList<Transaction> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(transactionTableView.comparatorProperty());
        transactionTableView.setItems(sortedData);
    }

    public void loadData() {
        TransactionList.clear();
        TransactionList.addAll(DBUtils.getTransactions());
        transactionTableView.setItems(TransactionList);
    }
}
