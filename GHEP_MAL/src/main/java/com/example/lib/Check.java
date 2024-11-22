package com.example.lib;

import javafx.application.Application;
import javafx.stage.Stage;
/// LẤY R
public class Check extends Application {

    public static void main(String[] args) {
        // Gọi launch() để khởi động JavaFX
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Gọi hàm checkBookByIsbn trong JavaFX thread để tránh lỗi IllegalStateException
        String isbn = "9780439139595";

        Book book = DBUtils.getBookByIsbn(isbn);

        if (book != null) {
            System.out.println("Book found: ");
            System.out.println("Title: " + book.getTitle());
            System.out.println("Author: " + book.getAuthor());
            System.out.println("Publish Year: " + book.getPublishYear());
            System.out.println("Quantity: " + book.getQuantity());
            System.out.println("Category: " + book.getCategory());
            System.out.println("Description: " + book.getDescription());
            System.out.println("Image: " + book.getImagePath());
        } else {
            System.out.println("Book not found.");
        }
    }
}
