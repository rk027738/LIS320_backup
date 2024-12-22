package SystemManagement;

import BookManagement.Book;
import BookManagement.Catalog;
import LoanManagement.Loan;
import UserManagement.Admin;
import UserManagement.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibrarySystem {
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
            //Initialize Loan table
            initializeLoansTable();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

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

//    public void addBookManually(int id, String title, String author) {
//        Book newBook = new Book(id, title, author, true);
//        catalog.addBook(newBook); // Add book to the database via the Catalog
//        System.out.println("Book added successfully: " + newBook);
//    }

    public void addBookManually(String title, String author) {
        // Check if the book already exists
        if (bookExists(title, author)) {
            System.out.println("Book with the same title and author already exists: " + title + " by " + author);
            return; // Exit the method to avoid adding duplicates
        }

        // Create a new book with an auto-generated ID (handled by the database)
        Book newBook = new Book(0, title, author, true); // Use 0 or null for ID as a placeholder
        catalog.addBook(newBook); // Add book to the database via the Catalog
        System.out.println("Book added successfully: " + newBook);
    }


    public boolean bookExists(String title, String author) {
        // Query the database to check if a book with the given title and author exists
        String query = "SELECT COUNT(*) FROM books WHERE title = ? AND author = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Return true if the count is greater than 0
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void removeBookManually(int bookId) {
        catalog.removeBook(bookId); // Remove book from the database via the Catalog
        System.out.println("Book with ID " + bookId + " removed successfully.");
    }

    private void initializeLoansTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS loans (" +
                "loan_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "book_id INT NOT NULL, " +
                "user_id INT NOT NULL, " +
                "issue_date DATE NOT NULL, " +
                "due_date DATE NOT NULL, " +
                "is_returned BOOLEAN DEFAULT FALSE, " +
                "FOREIGN KEY (book_id) REFERENCES books(id), " +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableQuery);
            System.out.println("Loans table is ready.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void borrowBook(int bookId, int userId) throws IllegalStateException, IllegalArgumentException {
        try {
            // Check if the book is available
            String checkBookQuery = "SELECT is_available FROM books WHERE id = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkBookQuery)) {
                checkStmt.setInt(1, bookId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    boolean isAvailable = rs.getBoolean("is_available");
                    if (!isAvailable) {
                        throw new IllegalStateException("The book is currently unavailable.");
                    }
                } else {
                    throw new IllegalArgumentException("Book ID not found.");
                }
            }

            // Proceed with borrowing the book
            String insertLoanQuery = "INSERT INTO loans (book_id, user_id, issue_date, due_date) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertLoanQuery)) {
                LocalDate issueDate = LocalDate.now();
                LocalDate dueDate = issueDate.plusDays(7);

                stmt.setInt(1, bookId);
                stmt.setInt(2, userId);
                stmt.setDate(3, Date.valueOf(issueDate));
                stmt.setDate(4, Date.valueOf(dueDate));
                stmt.executeUpdate();
            }

            // Mark the book as unavailable
            updateBookAvailability(bookId, false);

            System.out.println("Book borrowed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException("Database error while borrowing the book: " + e.getMessage());
        }
    }

    public void returnBook(int bookId) throws IllegalArgumentException, IllegalStateException {
        try {
            String checkLoanQuery = "SELECT loan_id, is_returned FROM loans WHERE book_id = ? AND is_returned = FALSE";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkLoanQuery)) {
                checkStmt.setInt(1, bookId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    boolean isReturned = rs.getBoolean("is_returned");
                    int loanId = rs.getInt("loan_id");

                    if (isReturned) {
                        throw new IllegalStateException("This book has already been returned.");
                    }

                    // Mark the loan as returned
                    String updateLoanQuery = "UPDATE loans SET is_returned = TRUE WHERE loan_id = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateLoanQuery)) {
                        updateStmt.setInt(1, loanId);
                        updateStmt.executeUpdate();
                    }

                    // Mark the book as available
                    updateBookAvailability(bookId, true);
                    System.out.println("Book returned successfully.");
                } else {
                    throw new IllegalArgumentException("No active loan found for this Book ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException("Database error while returning the book: " + e.getMessage());
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
        String updateBookQuery = "UPDATE books SET is_available = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateBookQuery)) {
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

    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        String getLoansQuery = "SELECT * FROM loans";
        try (PreparedStatement stmt = connection.prepareStatement(getLoansQuery)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int loanId = rs.getInt("loan_id");
                int bookId = rs.getInt("book_id");
                int userId = rs.getInt("user_id");
                LocalDate issueDate = rs.getDate("issue_date").toLocalDate();
                LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                boolean isReturned = rs.getBoolean("is_returned");

                Loan loan = new Loan(loanId, bookId, userId, issueDate, dueDate);
                loan.setReturned(isReturned);
                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
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
     * Gets the currently logged-in user.
     *
     * @return the logged-in user
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Sets the currently logged-in user.
     *
     * @param user the logged-in user
     */
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }
}
