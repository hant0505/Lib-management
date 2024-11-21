package com.example.lib;

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

public class TransactController implements Initializable {
    @FXML
    private TableView<Transact> transactTableView;

    @FXML
    private TableColumn<Transact, Integer> table_transactionID;
    @FXML
    private TableColumn<Transact, String> table_bookTitle;
    @FXML
    private TableColumn<Transact, String> table_username;
    @FXML
    private TableColumn<Transact, LocalDate> table_borrowedDate;
    @FXML
    private TableColumn<Transact, LocalDate> table_dueDate;
    @FXML
    private TableColumn<Transact, LocalDate> table_returnedDate;
    @FXML
    private TableColumn<Transact, String> table_status;

    @FXML
    private Button button_books;
    @FXML
    private Button button_addTransaction;

    @FXML
    private TextField tf_searchTransaction;

    ObservableList<Transact> transactList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        table_transactionID.setCellValueFactory(new PropertyValueFactory<>("transactionID"));
        table_bookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        table_username.setCellValueFactory(new PropertyValueFactory<>("username"));
        table_borrowedDate.setCellValueFactory(new PropertyValueFactory<>("borrowedDate"));
        table_dueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        table_returnedDate.setCellValueFactory(new PropertyValueFactory<>("returnedDate"));
        table_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        transactTableView.setItems(transactList);
        loadData();

        //changescene
        button_books.setOnAction(event -> DBUtils.changeScene(event,"bookInfo.fxml", "Book Information"));

        //search transact
        searchTransact();

        button_addTransaction.setOnAction(event -> DBUtils.changeScene(event,"bookInfo.fxml", "Book Information"));

    }

    public void searchTransact() {
        FilteredList<Transact> filteredData = new FilteredList<>(transactList, _ -> true);
        tf_searchTransaction.textProperty().addListener((_, _, newValue) -> filteredData.setPredicate(b -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();
            if (b.getBookTitle().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (b.getUsername().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else return false;
        }));
        SortedList<Transact> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(transactTableView.comparatorProperty());
        transactTableView.setItems(sortedData);
    }

    public void loadData() {
        transactList.clear();
        transactList.addAll(DBUtils.getTransacts());
        transactTableView.setItems(transactList);
    }
}
