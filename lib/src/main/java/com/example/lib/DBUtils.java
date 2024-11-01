package com.example.lib;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;

public class DBUtils {
    public static ObservableList<Book> getBooks() {
        ObservableList<Book> bookList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM books";

        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int bookID = resultSet.getInt("bookID");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int edition = resultSet.getInt("edition");
                int quantity = resultSet.getInt("quantity");
                String available = resultSet.getString("available");

                bookList.add(new Book(bookID, title, author, edition, quantity, available));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookList;
    }

    public static void changeScene(ActionEvent event, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addBook(Book newBook) {
        String sql = "INSERT INTO books (title, author, edition, quantity, available) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(2, newBook.getTitle());
            preparedStatement.setString(3, newBook.getAuthor());
            preparedStatement.setInt(4, newBook.getEdition());
            preparedStatement.setInt(5, newBook.getQuantity());
            preparedStatement.setString(6, newBook.getAvailable());

            preparedStatement.executeUpdate();


            System.out.println("Book added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}