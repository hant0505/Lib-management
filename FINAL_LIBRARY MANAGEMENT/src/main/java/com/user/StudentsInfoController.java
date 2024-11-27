package com.user;

import com.DatabaseConnection;
import javafx.beans.property.SimpleIntegerProperty;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

import static com.book.DBUtils.booksBorrowing;

@SuppressWarnings("CallToPrintStackTrace")
public class StudentsInfoController implements Initializable {
    static ObservableList<Student> studentsList;
    @FXML
    private Button button_home;
    @FXML
    private Button button_booksForLib;
    @FXML
    private Button button_account;
    @FXML
    private Button button_transactionsForLib;
    @FXML
    private TextField tf_search;
    @FXML
    private TableView<Student> studentTableView;
    @FXML
    private TableColumn<Student, String> table_username;
    @FXML
    private TableColumn<Student, String> table_name;
    @FXML
    private TableColumn<Student, String> table_phone;
    @FXML
    private TableColumn<Student, String> table_email;
    @FXML
    private TableColumn<Student, String> table_transMade;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        table_username.setCellValueFactory(new PropertyValueFactory<>("username"));
        table_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        table_phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        table_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        table_transMade.setCellValueFactory(cellData -> {
            String username = cellData.getValue().getUsername();
            int transMade = booksBorrowing(username); // Gọi phương thức để tính số sách đã mượn
            return new SimpleIntegerProperty(transMade).asObject().asString();
        });

        studentsList = FXCollections.observableArrayList();
        studentTableView.setItems(studentsList);

        loadData();
        searchStudent();

        button_account.setOnAction(event -> com.user.DBUtils.changeScene(event, "/com/user/account_info_lib.fxml", "Library"));
        button_home.setOnAction(event -> com.user.DBUtils.changeScene(event, "/com/home_librarian.fxml", "Library"));
        button_transactionsForLib.setOnAction(event -> DBUtils.changeScene(event, "/com/book/transactions_lib.fxml", "Transactions Information"));
        button_booksForLib.setOnAction(event -> DBUtils.changeScene(event, "/com/book/bookInfo_lib.fxml", "Book Information"));
    }

    private void loadData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "select username, name, phone, email from users where role = 'STUDENT'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String username = rs.getString(1);
                String name = rs.getString(2);
                String phone = rs.getString(3);
                String email = rs.getString(4);
                studentsList.add(new Student(username, name, phone, email));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchStudent() {
        FilteredList<Student> filteredData = new FilteredList<>(studentsList, _ -> true);
        tf_search.textProperty().addListener((_, _, newValue) -> filteredData.setPredicate(student -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();
            if (String.valueOf(student.getUsername()).contains(lowerCaseFilter)) {
                return true;
            } else if (student.getName().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (student.getPhone().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else return student.getEmail().toLowerCase().contains(lowerCaseFilter);
        }));
        SortedList<Student> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(studentTableView.comparatorProperty());
        studentTableView.setItems(sortedData);
    }
}
