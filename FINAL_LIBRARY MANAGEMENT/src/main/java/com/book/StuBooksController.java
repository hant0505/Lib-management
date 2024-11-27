package com.book;

import com.DatabaseConnection;
import com.user.DBUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("CallToPrintStackTrace")
public class StuBooksController {
    private final ObservableList<Node> bookCards = FXCollections.observableArrayList();
    @FXML
    private Button button_transactionsForStu;
    @FXML
    private Button button_home;
    @FXML
    private Button button_game;
    @FXML
    private Button button_account;
    @FXML
    private TextField tf_search;
    @FXML
    private HBox bookCardContainer;

    @FXML
    private void initialize() {
        fetchAndDisplayBooks();
        searchBook();

        button_account.setOnAction(e -> DBUtils.changeScene(e, "/com/user/account_info_stu.fxml", "Account"));
        button_home.setOnAction(e -> DBUtils.changeScene(e, "/com/home_student.fxml", "Home"));
        button_game.setOnAction(e -> DBUtils.changeScene(e, "/com/game/game.fxml", "Game"));
        button_transactionsForStu.setOnAction(e -> DBUtils.changeScene(e, "/com/book/transactions_stu.fxml", "Game"));
    }

    private void fetchAndDisplayBooks() {
        Task<Void> fetchBooksTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String sql = "SELECT isbn, title, author, publishYear, quantity, category, description, imagePath, image FROM books";
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(sql)
                ) {
                    ResultSet rs = preparedStatement.executeQuery();
                    while (rs.next()) {
                        String isbn = rs.getString("isbn");
                        String title = rs.getString("title");
                        String author = rs.getString("author");
                        int publishYear = rs.getInt("publishYear");
                        int quantity = rs.getInt("quantity");
                        String category = rs.getString("category");
                        String description = rs.getString("description");
                        String imagePath = rs.getString("imagePath");
                        byte[] image = rs.getBytes("image");

                        // Sử dụng phương thức updateBookCard trong thread chính để thêm thẻ sách
                        updateBookCard(isbn, title, author, publishYear, quantity, category, description, imagePath, image);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                // Cập nhật lại giao diện
                bookCardContainer.getChildren().setAll(bookCards);
            }

            @Override
            protected void failed() {
                super.failed();
                // Dùng getException() để lấy lỗi nếu có trong Task
                Throwable exception = getException();
                if (exception != null) {
                    exception.printStackTrace();
                }
            }
        };

        // Chạy task trong luồng riêng
        Thread thread = new Thread(fetchBooksTask);
        thread.setDaemon(true); // Đảm bảo thread sẽ không giữ ứng dụng khi hoàn tất
        thread.start();
    }


    private void updateBookCard(String isbn, String title, String author, int publishYear, int quantity, String category, String description, String imagePath, byte[] image) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/book/book_card.fxml"));
            VBox card = loader.load();

            // Lấy controller của BookCard để cập nhật dữ liệu
            BookCardController cardController = loader.getController();
            cardController.setBookData(title, imagePath, image);  // Cập nhật dữ liệu sách
            card.getProperties().put("controller", cardController); // Lưu controller vào thuộc tính của VBox

            // Thêm double click vào card
            card.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    // Tạo đối tượng Book và lưu vào BookSession
                    Book book;
                    if (imagePath != null) {
                        book = new Book(isbn, title, author, publishYear, quantity, category, description, imagePath);
                    } else {
                        book = new Book(isbn, title, author, publishYear, quantity, category, description, image);
                    }
                    BookSession.setSelectedBook(book);
                    DBUtils.changeScene(e, "/com/book/details_book.fxml", "Details Book");

                }
            });
            // Thêm thẻ BookCard vào container
            bookCards.add(card);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void searchBook() {
        // Tạo FilteredList từ ObservableList bookCards
        FilteredList<Node> filteredData = new FilteredList<>(bookCards, _ -> true);

        // Lắng nghe sự thay đổi từ ô tìm kiếm
        tf_search.textProperty().addListener((_, _, newValue) -> {
            filteredData.setPredicate(bookCard -> {
                // Nếu không có gì nhập vào, hiển thị tất cả sách
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Lấy controller từ VBox (thẻ sách)
                try {
                    BookCardController cardController = (BookCardController) bookCard.getProperties().get("controller");

                    // Kiểm tra tiêu đề của sách với từ khóa tìm kiếm
                    if (cardController != null) {
                        String title = cardController.getBookTitle(); // Phương thức này cần được định nghĩa trong BookCardController
                        return title.toLowerCase().contains(newValue.toLowerCase());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false; // Nếu không thể xác định, không hiển thị thẻ này
            });

            // Cập nhật các thẻ được hiển thị
            bookCardContainer.getChildren().setAll(filteredData);
        });
    }


}
