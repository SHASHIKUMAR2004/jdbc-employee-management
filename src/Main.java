import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        // Initialize the database (drop and recreate table with sample data)
        EmployeeDAO.initializeDatabase();
        EmployeeDAO employeeDAO = new EmployeeDAO();

        System.out.println("=== Employee Management System (JDBC + MySQL) ===");

        boolean exit = false;
        while (!exit) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> employeeDAO.printEmployee();
                case 2 -> addEmployee(employeeDAO);
                case 3 -> updateEmployeeById(employeeDAO);
                case 4 -> deleteEmployeeById(employeeDAO);
                case 5 -> batchInsertSampleEmployees(employeeDAO);
                case 6 -> findEmployeeById(employeeDAO);
                case 0 -> {
                    exit = true;
                    System.out.println("Exiting application. Goodbye!");
                }
                default -> System.out.println("Invalid choice, please try again.");
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n----------- MENU -----------");
        System.out.println("1. List all employees");
        System.out.println("2. Add new employee");
        System.out.println("3. Update employee by ID");
        System.out.println("4. Delete employee by ID");
        System.out.println("5. Batch insert sample employees");
        System.out.println("6. Find employee by ID");
        System.out.println("0. Exit");
        System.out.println("----------------------------");
    }

    private static int readInt(String message) {
        while (true) {
            System.out.print(message);
            String line = scanner.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readNonEmptyString(String message) {
        while (true) {
            System.out.print(message);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    private static void addEmployee(EmployeeDAO employeeDAO) {
        System.out.println("\n--- Add New Employee ---");
        String name = readNonEmptyString("Name: ");
        String email = readNonEmptyString("Email: ");
        String country = readNonEmptyString("Country: ");

        Employee employee = new Employee(name, email, country);
        employeeDAO.insertEmployee(employee);
    }

    private static void updateEmployeeById(EmployeeDAO employeeDAO) {
        System.out.println("\n--- Update Employee By ID ---");
        int id = readInt("Enter employee ID: ");

        String newName = readNonEmptyString("New name: ");
        String newEmail = readNonEmptyString("New email: ");
        String newCountry = readNonEmptyString("New country: ");

        employeeDAO.updateEmployeeById(id, newName, newEmail, newCountry);
    }

    private static void deleteEmployeeById(EmployeeDAO employeeDAO) {
        System.out.println("\n--- Delete Employee By ID ---");
        int id = readInt("Enter employee ID to delete: ");
        employeeDAO.deleteEmployee(id);
    }

    private static void batchInsertSampleEmployees(EmployeeDAO employeeDAO) {
        System.out.println("\n--- Batch Insert Sample Employees ---");

        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("Emily Davis", "emily@example.com", "Australia"));
        employees.add(new Employee("David Brown", "david@example.com", "Germany"));
        employees.add(new Employee("Laura Wilson", "laura@example.com", "France"));
        employees.add(new Employee("Mike Johnson", "mike@example.com", "Canada"));

        employeeDAO.batchInsertEmployees(employees);
        employeeDAO.printEmployee();
    }

    private static void findEmployeeById(EmployeeDAO employeeDAO) {
        System.out.println("\n--- Find Employee By ID ---");
        int id = readInt("Enter employee ID: ");

        Employee employee = employeeDAO.getEmployeeById(id);
        if (employee == null) {
            System.out.println("No employee found with ID: " + id);
        } else {
            System.out.println("Found: " + employee);
        }
    }
}
