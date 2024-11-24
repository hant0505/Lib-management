package com.example.lib;

import io.nayuki.qrcodegen.QrCode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;


@SuppressWarnings("CallToPrintStackTrace")
public class DBUtils {
    public static ObservableList<Book> getBooks() {
        ObservableList<Book> bookList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM books";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                String isbn = resultSet.getString("isbn");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int publishYear = resultSet.getInt("publishYear");
                int quantity = resultSet.getInt("quantity");
                String category = resultSet.getString("category");
                String imagePath = resultSet.getString("imagePath");

                //bookList.add(new Book(isbn, title, author, publishYear, quantity, category));
                ///HANT
                bookList.add(new Book(isbn, title, author, publishYear, category, quantity));
                // 22/11
                //bookList.add(new Book(isbn,title, author, publishYear, category, quantity,imagePath));


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookList;
    }

    public static ObservableList<Transact> getTransacts() {
        ObservableList<Transact> transactList = FXCollections.observableArrayList();
        String sql = "SELECT t.transactionID, t.username, t.status, t.borrowedDate, t.dueDate, t.returnedDate, b.title AS bookTitle " +
                "FROM transactions t " +
                "JOIN books b ON t.isbn = b.isbn";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int transactionID = resultSet.getInt("transactionID");
                String username = resultSet.getString("username");
                String status = resultSet.getString("status");
                LocalDate borrowedDate = resultSet.getDate("borrowedDate") != null
                        ? resultSet.getDate("borrowedDate").toLocalDate()
                        : null;
                LocalDate dueDate = resultSet.getDate("dueDate") != null
                        ? resultSet.getDate("dueDate").toLocalDate()
                        : null;
                LocalDate returnedDate = resultSet.getDate("returnedDate") != null
                        ? resultSet.getDate("returnedDate").toLocalDate()
                        : null;
                String bookTitle = resultSet.getString("bookTitle");

                transactList.add(new Transact(transactionID, username, bookTitle, borrowedDate, dueDate, status, returnedDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactList;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void changeScene(Event event, String fxmlFile, String title) {
        try {
            Parent root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void addBook(Book newBook, ActionEvent event) {

        //String sql = "INSERT INTO books (title, author, publishYear, quantity, isbn, description, category) VALUES (?, ?, ?, ?, ?, ?, ?)";
        ///HANT
        String sql = "INSERT INTO books (title, author, publishYear, quantity, isbn, description, category, imagePath) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, newBook.getTitle());
            preparedStatement.setString(2, newBook.getAuthor());
            preparedStatement.setInt(3, newBook.getPublishYear());
            preparedStatement.setInt(4, newBook.getQuantity());
            preparedStatement.setString(5, newBook.getIsbn());
            preparedStatement.setString(6, newBook.getDescription());
            preparedStatement.setString(7, newBook.getCategory());
            preparedStatement.setString(8, newBook.getImagePath()); /// HANT

            preparedStatement.executeUpdate();

            changeScene(event, "bookInfo.fxml", "Book Info");
            System.out.println("Book added successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addTransact(Book book, String username, LocalDate borrowedDate, LocalDate dueDate, String status, ActionEvent event) {
        String checkISBNSQL = "SELECT 1 FROM books WHERE isbn = ?";
        String checkUserSQL = "SELECT 1 FROM users WHERE username = ?";
        String sql = "INSERT INTO transactions (isbn, username, bookTitle, borrowedDate, dueDate, status) VALUES (?, ?, ?, ?, ?, ?)"; // Chèn giao dịch vào bảng transactions

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps1 = connection.prepareStatement(checkISBNSQL);
             PreparedStatement ps2 = connection.prepareStatement(checkUserSQL)) {

            ps1.setString(1, book.getIsbn());
            ResultSet rs1 = ps1.executeQuery();
            if (!rs1.next()) {
                System.out.println("ISBN does not exist in the database.");
                return;
            }

            ps2.setString(1, username);
            ResultSet rs2 = ps2.executeQuery();
            if (!rs2.next()) {
                System.out.println("User does not exist in the database.");
                return;
            }
            ///QR
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, book.getIsbn());
                preparedStatement.setString(2, username); // Đặt giá trị username
                preparedStatement.setString(3, book.getTitle()); // Đặt tiêu đề sách từ đối tượng Book
                preparedStatement.setDate(4, Date.valueOf(borrowedDate)); // Ngày mượn sách
                preparedStatement.setDate(5, Date.valueOf(dueDate)); // Ngày hết hạn
                preparedStatement.setString(6, status); // Trạng thái của giao dịch

                preparedStatement.executeUpdate(); // Thực thi lệnh chèn giao dịch
                System.out.println("Transaction added successfully.");

                ///HANT : ĐƯA QR PATH VÀO DATABASE
                // Lấy ID giao dịch vừa được tạo
                ResultSet rs = preparedStatement.getGeneratedKeys();
                int transactionID = -1;
                if (rs.next()) {
                    transactionID = rs.getInt(1); // Lấy transactionID
                }


                if (transactionID != -1) {
                    // Tạo mã QR từ thông tin giao dịch
                    String bookTitle = book.getTitle().replace(" ", "+");
                    String serverIP = ServerIP.getServerIP();
                    System.out.println("Sever có IP là :" + serverIP); /// debug
                    String qrData = "http://" + serverIP + ":8080/qrs?data=" + transactionID + ";" + username + ";" + bookTitle + ";" + borrowedDate + ";" + dueDate + ";" + status;
                    QrCode qr = QrCode.encodeText(qrData, QrCode.Ecc.MEDIUM);
                    BufferedImage img = QR_Image.toImage(qr, 4, 10);

                    //Tạo thư mục qrs ngay trong project - lưu qr trong đó
                    String qrPath = "qrs/trans_" + transactionID + ".png";
                    File qrFile = new File(qrPath);
                    qrFile.getParentFile().mkdirs();  // Tạo thư mục nếu chưa có
                    ImageIO.write(img, "PNG", qrFile);

                    // Cập nhật qrCodePath vào bảng transactions
                    System.out.println("QR code written successfully.");
                    String updateSQL = "UPDATE transactions SET qrCodePath = ? WHERE transactionID = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSQL)) {
                        updateStmt.setString(1, qrPath);
                        updateStmt.setInt(2, transactionID);
                        updateStmt.executeUpdate();
                    }

                    System.out.println("Transaction created and QR code generated successfully.");
                } else {
                    System.out.println("Failed to generate transaction ID.");
                }


                changeScene(event, "bookTransact.fxml", "Transaction Info");

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e); // ImageIO.write(img, "PNG", qrFile);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("CallToPrintStackTrace")
    public static void deleteBookFromDB(List<Book> bookToDelete) {
        String sql = "DELETE FROM books WHERE isbn=?";

        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (Book book : bookToDelete) {
                preparedStatement.setString(1, book.getIsbn());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void updateBookFromDB(Book newBook, ActionEvent event) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "update books set title = ?, author = ?, publishYear = ?, quantity = ? where isbn = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // Setting all parameters according to SQL placeholders
            preparedStatement.setString(1, newBook.getTitle());
            preparedStatement.setString(2, newBook.getAuthor());
            preparedStatement.setInt(3, newBook.getPublishYear());
            preparedStatement.setInt(4, newBook.getQuantity());
            preparedStatement.setString(5, newBook.getIsbn());

            preparedStatement.executeUpdate();
            System.out.println("Book updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Book getDetailsBookFromDB(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, isbn);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int publishYear = resultSet.getInt("publishYear");
                int quantity = resultSet.getInt("quantity");
                String description = resultSet.getString("description");
                String category = resultSet.getString("category");
                return new Book(isbn, title, author, publishYear, quantity, description, category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Book getBookByIsbn(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, isbn); // Đặt giá trị ISBN vào câu lệnh SQL
            ResultSet resultSet = preparedStatement.executeQuery();

            // Nếu tìm thấy sách, tạo đối tượng Book và trả về
            if (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int publishYear = resultSet.getInt("publishYear");
                int quantity = resultSet.getInt("quantity");
                String category = resultSet.getString("category");
                String description = resultSet.getString("description");
                /// Hiển thị bìa?
                System.out.println("// Hàm getBookByIsbn// Lâys bìa nào!!");

                String imagePath = resultSet.getString("imagePath");

                return new Book(isbn, title, author, publishYear, quantity, description, category, imagePath);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    ///HANT
    public static void updateTransactionStatus(int transactionID) {
        // Lấy ngày hiện tại
        LocalDate returnedDate = LocalDate.now();

        // cập nhật trạng thái và ngày trả sách
        String sql = "UPDATE transactions SET status = 'Returned', returnedDate = ? WHERE transactionID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setObject(1, returnedDate);
            preparedStatement.setInt(2, transactionID);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Trạng thái giao dịch đãcập nhật thành 'Returned' và ngày trả sách là " + returnedDate);
            } else {
                System.out.println("Không tìm thấy giao dịch để cập nhật.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật trạng thái giao dịch: " + e.getMessage());
        }
    }
}