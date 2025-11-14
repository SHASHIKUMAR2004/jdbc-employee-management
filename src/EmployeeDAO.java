import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmployeeDAO {

    private static final Logger logger = Logger.getLogger(EmployeeDAO.class.getName());

    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS employees (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL UNIQUE, " +
                    "country VARCHAR(100) NOT NULL)";

    private static final String DROP_TABLE_SQL =
            "DROP TABLE IF EXISTS employees";

    private static final String INSERT_SQL =
            "INSERT INTO employees (name, email, country) VALUES (?, ?, ?)";

    private static final String SELECT_ALL_SQL =
            "SELECT id, name, email, country FROM employees";

    private static final String SELECT_BY_ID_SQL =
            "SELECT id, name, email, country FROM employees WHERE id = ?";

    private static final String UPDATE_BY_ID_SQL =
            "UPDATE employees SET name = ?, email = ?, country = ? WHERE id = ?";

    private static final String UPDATE_BY_NAME_SQL =
            "UPDATE employees SET name = ?, email = ?, country = ? WHERE name = ?";

    private static final String UPDATE_BY_EMAIL_SQL =
            "UPDATE employees SET name = ?, email = ?, country = ? WHERE email = ?";

    private static final String DELETE_BY_ID_SQL =
            "DELETE FROM employees WHERE id = ?";

    public static void initializeDatabase() {
        try (Connection connection = DB_Connection.getConnection();
             Statement statement = connection.createStatement()) {

            // For demo purposes: start with a clean table each run
            statement.executeUpdate(DROP_TABLE_SQL);
            statement.executeUpdate(CREATE_TABLE_SQL);

            String seedDataSQL =
                    "INSERT INTO employees (name, email, country) VALUES " +
                            "('John Doe', 'john.doe@example.com', 'USA'), " +
                            "('Jane Smith', 'jane.smith@example.com', 'UK')";
            statement.executeUpdate(seedDataSQL);

            logger.info("Database initialized successfully.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error initializing database", e);
        }
    }

    public void insertEmployee(Employee emp) {
        try (Connection con = DB_Connection.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT_SQL)) {

            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getCountry());

            ps.executeUpdate();
            System.out.println("Employee inserted successfully.");
        } catch (SQLException ex) {
            // 23000 = integrity constraint violation (e.g., duplicate email)
            if ("23000".equals(ex.getSQLState())) {
                System.out.println("Failed to insert employee: email '" +
                        emp.getEmail() + "' already exists.");
            } else {
                ex.printStackTrace();
            }
            throw new RuntimeException(ex);
        }
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();

        try (Connection connection = DB_Connection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL_SQL)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String country = resultSet.getString("country");

                employees.add(new Employee(id, name, email, country));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return employees;
    }

    public Employee getEmployeeById(int id) {
        try (Connection connection = DB_Connection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String email = resultSet.getString("email");
                    String country = resultSet.getString("country");
                    return new Employee(id, name, email, country);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Not found
    }

    public void batchInsertEmployees(List<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            System.out.println("No employees provided for batch insert.");
            return;
        }

        Connection connection = null;
        try {
            connection = DB_Connection.getConnection();
            connection.setAutoCommit(false); // start transaction

            try (PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {
                for (Employee employee : employees) {
                    ps.setString(1, employee.getName());
                    ps.setString(2, employee.getEmail());
                    ps.setString(3, employee.getCountry());
                    ps.addBatch();
                }

                int[] results = ps.executeBatch();
                connection.commit();
                logger.log(Level.INFO,
                        "Batch executed successfully. Inserted {0} records.",
                        results.length);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing batch", e);
            if (connection != null) {
                try {
                    connection.rollback();
                    logger.info("Transaction rolled back successfully.");
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Error rolling back transaction", ex);
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Error closing connection", e);
                }
            }
        }
    }

    public void updateEmployeeById(int id, String newName, String newEmail, String newCountry) {
        try (Connection connection = DB_Connection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ID_SQL)) {

            statement.setString(1, newName);
            statement.setString(2, newEmail);
            statement.setString(3, newCountry);
            statement.setInt(4, id);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("No employee found with ID: " + id);
            } else {
                System.out.println("Employee with ID " + id + " updated successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEmployeeByName(String name, String newName, String newEmail, String newCountry) {
        try (Connection connection = DB_Connection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BY_NAME_SQL)) {

            statement.setString(1, newName);
            statement.setString(2, newEmail);
            statement.setString(3, newCountry);
            statement.setString(4, name);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("No employee found with name: " + name);
            } else {
                System.out.println("Employee(s) with name '" + name + "' updated successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEmployeeByEmail(String email, String newName, String newEmail, String newCountry) {
        try (Connection connection = DB_Connection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BY_EMAIL_SQL)) {

            statement.setString(1, newName);
            statement.setString(2, newEmail);
            statement.setString(3, newCountry);
            statement.setString(4, email);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("No employee found with email: " + email);
            } else {
                System.out.println("Employee with email '" + email + "' updated successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEmployee(int id) {
        try (Connection connection = DB_Connection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID_SQL)) {

            preparedStatement.setInt(1, id);
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted == 0) {
                System.out.println("No employee found with ID: " + id);
            } else {
                System.out.println("Employee with ID " + id + " deleted successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printEmployee() {
        List<Employee> employees = getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No employees found.\n");
            return;
        }

        // Print table header
        System.out.println("+----+--------------+---------------------------+---------------+");
        System.out.println("| ID | Name         | Email                     | Country       |");
        System.out.println("+----+--------------+---------------------------+---------------+");

        // Print table data
        for (Employee employee : employees) {
            System.out.printf("| %2d | %-12s | %25s | %-13s |%n",
                    employee.getId(),
                    employee.getName(),
                    employee.getEmail(),
                    employee.getCountry());
        }

        // Print table footer
        System.out.println("+----+--------------+---------------------------+---------------+\n");
    }
}
