package Book;
import java.sql.*;


public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/library";
    private static final String USER = "root";
    private static final String PASSWORD = "0966732929";

    public static Connection getConnection() throws SQLException {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to database successfully!");
            return connection;
    }
}

