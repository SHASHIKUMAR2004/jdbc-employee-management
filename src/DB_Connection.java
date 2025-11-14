import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB_Connection {

    // Update these according to your local DB config if needed
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/JDBC_CRUD";
    private static final String USER = "root";
    private static final String PASSWORD = "mysqls01)";

    // Load MySQL JDBC driver once when the class is loaded
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load MySQL JDBC driver");
            e.printStackTrace();
        }
    }

    private DB_Connection() {
        // Utility class - prevent instantiation
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }
}
