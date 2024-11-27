package com.book;

import com.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.io.ByteArrayInputStream;
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

                bookList.add(new Book(isbn, title, author, publishYear, category, quantity, imagePath));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookList;
    }

    public static ObservableList<Transaction> getTransactions() {
        ObservableList<Transaction> transactList = FXCollections.observableArrayList();
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

                transactList.add(new Transaction(transactionID, username, bookTitle, borrowedDate, dueDate, status, returnedDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactList;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void addBook(Book newBook, ActionEvent event) {
        String sql = "INSERT INTO books (title, author, publishYear, quantity, isbn, description, category, imagePath, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, newBook.getTitle());
            preparedStatement.setString(2, newBook.getAuthor());
            preparedStatement.setInt(3, newBook.getPublishYear());
            preparedStatement.setInt(4, newBook.getQuantity());
            preparedStatement.setString(5, newBook.getIsbn());
            preparedStatement.setString(6, newBook.getDescription());
            preparedStatement.setString(7, newBook.getCategory());
            preparedStatement.setString(8, newBook.getImagePath());
            // Convert byte array to ByteArrayInputStream
            if (newBook.getImage() != null) {
                ByteArrayInputStream bit = new ByteArrayInputStream(newBook.getImage());
                preparedStatement.setBinaryStream(9, bit, newBook.getImage().length);
            } else {
                preparedStatement.setBinaryStream(9, null);
            }

            preparedStatement.executeUpdate();

            com.user.DBUtils.changeScene(event, "/com/book/bookInfo_lib.fxml", "Book Info");
            System.out.println("Book added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addTransaction(Book book, String username, LocalDate borrowedDate, LocalDate dueDate, String status) {
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

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, book.getIsbn()); // Đặt giá trị ISBN từ đối tượng Book
                preparedStatement.setString(2, username); // Đặt giá trị username
                preparedStatement.setString(3, book.getTitle()); // Đặt tiêu đề sách từ đối tượng Book
                preparedStatement.setDate(4, Date.valueOf(borrowedDate)); // Ngày mượn sách
                preparedStatement.setDate(5, Date.valueOf(dueDate)); // Ngày hết hạn
                preparedStatement.setString(6, status); // Trạng thái của giao dịch

                preparedStatement.executeUpdate(); // Thực thi lệnh chèn giao dịch
                System.out.println("Transaction added successfully.");

                //HANT : ĐƯA QR PATH VÀO DATABASE
                // Lấy ID giao dịch vừa được tạo
                ResultSet rs = preparedStatement.getGeneratedKeys();
                int transactionID = -1;
                if (rs.next()) {
                    transactionID = rs.getInt(1); // Lấy transactionID
                }

                if (transactionID != -1) {
                    QR_SCAN.createQR(transactionID, username, book.getTitle(), borrowedDate, dueDate, status);
                } else System.out.println("Failed to generate transaction ID.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
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
    public static void updateBookFromDB(Book newBook) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "update books set title = ?, author = ?, publishYear = ?, quantity = ?, description = ? where isbn = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // Setting all parameters according to SQL placeholders
            preparedStatement.setString(1, newBook.getTitle());
            preparedStatement.setString(2, newBook.getAuthor());
            preparedStatement.setInt(3, newBook.getPublishYear());
            preparedStatement.setInt(4, newBook.getQuantity());
            preparedStatement.setString(5, newBook.getDescription());
            preparedStatement.setString(6, newBook.getIsbn());

            preparedStatement.executeUpdate();
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
            if (resultSet.next()) {
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
                String imagePath = resultSet.getString("imagePath");
                byte[] image = resultSet.getBytes("image");
                if (imagePath != null)
                    return new Book(isbn, title, author, publishYear, quantity, description, category, imagePath);
                else return new Book(isbn, title, author, publishYear, quantity, description, category, image);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void updateTransactionStatus(int transactionID) {
        LocalDate returnedDate = LocalDate.now();

        // cập nhật cả trạng thái và ngày trả sách
        String updateTransactionSQL = "UPDATE transactions SET status = 'Returned', returnedDate = ? WHERE transactionID = ?";

        // tăng số lượng sách vừa dựa trên ISBN
        String updateBookQuantitySQL = "UPDATE books SET quantity = quantity + 1 WHERE isbn = (" +
                "SELECT isbn FROM transactions WHERE transactionID = ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Thực hiện cả hai thao tác trong một giao dịch
            connection.setAutoCommit(false);

            // Cập nhật trạng thái và ngày trả trong bảng transactions
            try (PreparedStatement transactionStmt = connection.prepareStatement(updateTransactionSQL)) {
                transactionStmt.setDate(1, Date.valueOf(returnedDate));
                transactionStmt.setInt(2, transactionID);

                int transactionRows = transactionStmt.executeUpdate();
                if (transactionRows > 0) {
                    System.out.println("Transaction status updated successfully.");
                } else {
                    System.out.println("Transaction not found or already updated.");
                    connection.commit();
                    return;
                }
            }

            // Cập nhật lại số lượng khi trả
            try (PreparedStatement bookStmt = connection.prepareStatement(updateBookQuantitySQL)) {
                bookStmt.setInt(1, transactionID);

                int bookRows = bookStmt.executeUpdate();
                if (bookRows > 0) {
                    System.out.println("Book quantity updated successfully.");
                } else {
                    System.out.println("ISBN for transaction not found in books.");
                    connection.commit();
                    return;
                }
            }
            connection.commit();
            System.out.println("Transaction and book quantity updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void extendTransactions(int transactionID, int days) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Trừ coins
            String updateCoinsSQL = "UPDATE users SET coins = coins - ?*50 WHERE username = (SELECT username FROM transactions WHERE transactionID = ?)";
            // Gia hạn dueDate
            String updateDueDateSQL = "UPDATE transactions SET dueDate = DATE_ADD(dueDate, INTERVAL ? DAY) WHERE transactionID = ?";

            // Bắt đầu transaction (nếu DB hỗ trợ)
            connection.setAutoCommit(false);

            try (
                    PreparedStatement updateCoinsStmt = connection.prepareStatement(updateCoinsSQL);
                    PreparedStatement updateDueDateStmt = connection.prepareStatement(updateDueDateSQL)
            ) {
                // Cập nhật coins
                updateCoinsStmt.setInt(1, days);
                updateCoinsStmt.setInt(2, transactionID);
                int rowsAffectedCoins = updateCoinsStmt.executeUpdate();
                if (rowsAffectedCoins <= 0) {
                    System.out.println("No user found with transactionID: " + transactionID);
                    connection.rollback(); // Hoàn tác thay đổi nếu có lỗi
                    return;
                }

                // Cập nhật dueDate
                updateDueDateStmt.setInt(1, days);
                updateDueDateStmt.setInt(2, transactionID);
                int rowsAffectedDueDate = updateDueDateStmt.executeUpdate();

                if (rowsAffectedDueDate > 0) {
                    System.out.println("Transaction " + transactionID + " extended.");
                    connection.commit(); // Xác nhận thay đổi
                    PreparedStatement ps = connection.prepareStatement("select transactionID, username, bookTitle, borrowedDate, dueDate, status from transactions where transactionID = ?");
                    ps.setInt(1, transactionID);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        QR_SCAN.updateDueDateInQRCode(transactionID, rs.getString("username"),
                                rs.getString("bookTitle"),
                                rs.getDate("borrowedDate").toLocalDate(),
                                rs.getDate("DueDate").toLocalDate(),
                                rs.getString("status"));
                    }

                } else {
                    System.out.println("Not found transaction to extend: " + transactionID);
                    connection.rollback(); // Hoàn tác thay đổi nếu có lỗi
                }
            } catch (Exception e) {
                System.err.println("Lỗi trong quá trình gia hạn giao dịch: " + e.getMessage());
                connection.rollback(); // Hoàn tác thay đổi trong trường hợp có lỗi
            } finally {
                connection.setAutoCommit(true); // Khôi phục chế độ tự động commit
            }
        } catch (Exception e) {
            System.err.println("Không thể kết nối với cơ sở dữ liệu: " + e.getMessage());
        }
    }

    public static int booksBorrowing(String username) {
        int count = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("select count(transactionID) from transactions where username = ? and status = ?");
            ps.setString(1, username);
            ps.setString(2, "Borrowed");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }

    public static int monthlyTransMade(String username) {
        int count = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "select count(transactionID) from transactions where username = ? and status = ? " +
                            "and extract(month from returnedDate) = extract(month from ?)" +
                            "and extract(year from returnedDate) = extract(year from ?)");
            ps.setString(1, username);
            ps.setString(2, "Returned");
            LocalDate today = LocalDate.now();
            ps.setDate(3, Date.valueOf(today));
            ps.setDate(4, Date.valueOf(today));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }
}