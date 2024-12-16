package SystemManagement;

import BookManagement.Book;
import BookManagement.Catalog;
import LoanManagement.Loan;
import UserManagement.Admin;
import UserManagement.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibrarySystem {
    public Connection getConnection() {
        return connection;
    }

    private final Connection connection; // Shared database connection
    private final Catalog catalog;      // Handles book-related operations
    private final List<Loan> loans;     // Manages loan-related data
    private User loggedInUser;          // Tracks the currently logged-in user

    public LibrarySystem() {
        try {
            // Establish the database connection
            connection = DriverManager.getConnection(
                    "jdbc:mysql://sql7.freesqldatabase.com:3306/sql7752156",
                    "sql7752156",
                    "NmwMfs5UnR"
            );
            new User(connection);
            // Initialize Catalog with the shared connection
            catalog = new Catalog(connection);
            // Initialize loans
            loans = new ArrayList<>();
            // Add sample data
            initializeSampleData();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }

    /**
     * Adds sample data for testing purposes.
     */
    private void initializeSampleData() {
        // Add sample books
//        catalog.addBook(new Book(1, "1984", "George Orwell", true));
//        catalog.addBook(new Book(2, "To Kill a Mockingbird", "Harper Lee", true));
//        catalog.addBook(new Book(3, "The Great Gatsby", "F. Scott Fitzgerald", true));
    }



    /**
     * Authenticates a user by their username and password.
     *
     * @param username the entered username
     * @param password the entered password
     * @return a User object if authentication succeeds, null otherwise
     */
//    public User authenticate(String username, String password) {
//        try {
//            // Query the database to validate user credentials
//            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
//            PreparedStatement stmt = connection.prepareStatement(query);
//            stmt.setString(1, username);
//            stmt.setString(2, password);
//            ResultSet rs = stmt.executeQuery();
//
//            if (rs.next()) {
//                String role = rs.getString("role");
//                int userId = rs.getInt("id");
//
//                // Return an Admin or User object based on the role
//                if ("admin".equalsIgnoreCase(role)) {
//                    return new Admin(userId, username, password, role);
//                } else {
//                    return new User(userId, username, password, role);
//                }
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return null; // Authentication failed
//    }

    public User authenticate(String username, String password) {
        try {
            // Debugging: Print the username and password being used for login
            System.out.println("Attempting login with username: " + username + " and password: " + password);

            // Query to check for matching username and password
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            // Execute the query
            ResultSet rs = stmt.executeQuery();

            // Check if a matching user was found
            if (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("id");
                System.out.println("Login successful! User ID: " + userId + ", Role: " + role);

                // Return User or Admin based on role
                if ("admin".equalsIgnoreCase(role)) {
                    return new Admin(userId, username, password, role);
                } else {
                    return new User(userId, username, password, role);
                }
            } else {
                System.out.println("Invalid credentials. No user found with the provided details.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if login fails
    }

    public void addBookManually(int id, String title, String author) {
        Book newBook = new Book(id, title, author, true);
        catalog.addBook(newBook); // Add book to the database via the Catalog
        System.out.println("Book added successfully: " + newBook);
    }

    public void removeBookManually(int bookId) {
        catalog.removeBook(bookId); // Remove book from the database via the Catalog
        System.out.println("Book with ID " + bookId + " removed successfully.");
    }

    public void borrowBookManually(int bookId, int userId) {
        try {
            // Check if the book is available
            Book book = catalog.getBooks().stream()
                    .filter(b -> b.getId() == bookId && b.isAvailable())
                    .findFirst()
                    .orElse(null);

            if (book == null) {
                System.out.println("Book with ID " + bookId + " is not available for borrowing.");
                return;
            }

            // Create a new loan record
            Loan loan = new Loan(loans.size() + 1, bookId, userId,
                    java.time.LocalDate.now(),
                    java.time.LocalDate.now().plusDays(14));
            loans.add(loan); // Add to the in-memory list

            // Update book availability in the database
            updateBookAvailability(bookId, false);
            System.out.println("Book borrowed successfully! Loan details: " + loan);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while borrowing the book.");
        }
    }

    public void returnBookManually(int loanId) {
        try {
            // Find the loan by its ID
            Loan loan = loans.stream()
                    .filter(l -> l.getLoanId() == loanId && !l.isReturned())
                    .findFirst()
                    .orElse(null);

            if (loan == null) {
                System.out.println("Invalid Loan ID or the book is already returned.");
                return;
            }

            // Update the loan status
            loan.setReturned(true);

            // Make the book available again
            updateBookAvailability(loan.getBookId(), true);
            System.out.println("Book returned successfully! Loan ID: " + loanId);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while returning the book.");
        }
    }

    public void searchBookByKeyword(String keyword) {
        List<Book> results = catalog.searchBooks(keyword);
        if (results.isEmpty()) {
            System.out.println("No books found matching the keyword: " + keyword);
        } else {
            System.out.println("Search results:");
            results.forEach(System.out::println);
        }
    }

    public void updateBookAvailability(int bookId, boolean isAvailable) {
        String query = "UPDATE books SET is_available = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, isAvailable);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");

                // Check role and create User or Admin objects
                if ("admin".equalsIgnoreCase(role)) {
                    users.add(new Admin(id, username, "******", role)); //doesn't show admin passwords.
                } else {
                    users.add(new User(id, username, password, role));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }



    /**
     * Provides access to the catalog for the GUI.
     *
     * @return the catalog instance
     */
    public Catalog getCatalog() {
        return catalog;
    }

    /**
     * Provides access to the list of loans for the GUI.
     *
     * @return the list of loans
     */
    public List<Loan> getLoans() {
        return loans;
    }

    /**
     * Sets the currently logged-in user.
     *
     * @param user the logged-in user
     */
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    /**
     * Gets the currently logged-in user.
     *
     * @return the logged-in user
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }
}
