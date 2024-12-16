package UserManagement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class User {
    private int id;                 // Unique ID for the user
    private String username;        // Username of the user
    private String password;        // Password for authentication
    private String role;            // Role of the user (e.g., "admin" or "user")
    private final Connection connection; // Database connection

    // Constructor used for creating User objects with user details
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.connection = null; // No connection for this constructor
    }

    // Constructor for managing database-related operations
    public User(Connection connection) {
        this.connection = connection;
        initializeUsersTable(); // Create the users table when User is initialized
    }

    // Initialize the users table if it doesn't exist
    private void initializeUsersTable() {
        try (Statement stmt = connection.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(255) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(50) NOT NULL" +
                    ")";
            stmt.execute(createTableSQL);
            System.out.println("Users table is ready.");

            // Insert default admin user if the table is empty
            String insertAdminSQL = "INSERT IGNORE INTO users (username, password, role)\n" +
                    "VALUES ('root', 'password', 'admin');\n";
            stmt.executeUpdate(insertAdminSQL);
            System.out.println("Default admin user added (if not already present).");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error initializing the users table: " + e.getMessage());
        }
    }

    // Getters and Setters for user properties
    public int getId() {
        return id;
    }

    public void setUserId(int userId) {
        this.id = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "Userid=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
