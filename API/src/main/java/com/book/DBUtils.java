package com.book;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;
import java.util.List;


@SuppressWarnings("CallToPrintStackTrace")
public class DBUtils {
    public static ObservableList<Book> getBooks() {
        ObservableList<Book> bookList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM books";

        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                String bookID = resultSet.getString("bookID");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int edition = resultSet.getInt("edition");
                int quantity = resultSet.getInt("quantity");
                String available = resultSet.getString("available");
                //int isbn = resultSet.getInt("isbn");
                //String description = resultSet.getString("description");

                // them sach vao table view
                bookList.add(new Book(bookID,title, author, edition, quantity, available));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookList;
    }

    @SuppressWarnings("CallToPrintStackTrace")
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

    @SuppressWarnings("CallToPrintStackTrace")
    public static void addBook(Book newBook, ActionEvent event) {
        String sql = "INSERT INTO books (title, author, edition, quantity, available) VALUES (?, ?, ?, ?, ?)" ;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Set the common parameters
            //preparedStatement.setString(1, newBook.getBookID());
            preparedStatement.setString(1, newBook.getTitle());
            preparedStatement.setString(2, newBook.getAuthor());
            preparedStatement.setInt(3, newBook.getEdition());
            preparedStatement.setInt(4, newBook.getQuantity());
            preparedStatement.setString(5, newBook.getAvailable());
            //preparedStatement.setInt(6, newBook.getIsbn());
            //preparedStatement.setString(7, newBook.getDescription());

            preparedStatement.executeUpdate();

            changeScene(event, "bookInfo.fxml", "Book Info");
            System.out.println("Book added successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void deleteBookfromDB(List<Book> bookToDelete) {
        String sql = "DELETE FROM books WHERE bookID=?";

        try {
            Connection connection = databaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (Book book : bookToDelete) {
                preparedStatement.setString(1, book.getBookID());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void updateBookfromDB(Book newBook, ActionEvent event) {
        String sql = "update books set title = ?, author = ?, edition = ?, quantity = ? where bookID = ?";
        try {
            Connection connection = databaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // Setting all parameters according to SQL placeholders
            preparedStatement.setString(1, newBook.getTitle());
            preparedStatement.setString(2, newBook.getAuthor());
            preparedStatement.setInt(3, newBook.getEdition());
            preparedStatement.setInt(4, newBook.getQuantity());
            preparedStatement.setString(5, newBook.getBookID());

            preparedStatement.executeUpdate();
            System.out.println("Book updated successfully");
            changeScene(event,"bookInfo.fxml", "Book Information");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}