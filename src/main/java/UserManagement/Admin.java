package UserManagement;

import BookManagement.Book;
import BookManagement.Catalog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Admin extends User {
    private final Connection connection; // Database connection for admin-specific operations

    // Constructor for creating an Admin user
    public Admin(int id, String username, String password, String role) {
        super(id, username, password, role);
        this.connection = null; // No direct connection for this constructor
    }

    // Constructor for Admin database-related operations
    public Admin(Connection connection) {
        super(connection); // Call the User constructor for shared setup
        this.connection = connection;
        initializeAdmin();
    }

    private void initializeAdmin() {
        try (Statement stmt = connection.createStatement()) {
            String checkQuery = "SELECT COUNT(*) AS user_count FROM users";
            java.sql.ResultSet rs = stmt.executeQuery(checkQuery);
            if (rs.next() && rs.getInt("user_count") == 0) {
                String insertAdminQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertAdminQuery)) {
                    insertStmt.setString(1, "root");  // Default admin username
                    insertStmt.setString(2, "password");  // Default admin password
                    insertStmt.setString(3, "admin");  // Role
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a book to the database using the Catalog
    public void addBook(Catalog catalog, Book book) {
        catalog.addBook(book);
        System.out.println("Book added: " + book.getTitle());
    }

    // Remove a book from the database using the Catalog
    public void removeBook(Catalog catalog, int bookId) {
        catalog.removeBook(bookId);
        System.out.println("Book with ID " + bookId + " removed.");
    }

    // Add a user directly to the database
    public void addUser(String username, String password, String role) {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.executeUpdate();
            System.out.println("User added by Admin: " + username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Remove a user from the database by ID
    public void removeUser(int userId) {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User with ID " + userId + " removed by Admin.");
            } else {
                System.out.println("No user found with ID " + userId + ".");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
