package SystemManagement;

import BookManagement.Book;
import LoanManagement.Loan;
import UserManagement.User;
import UserManagement.Admin;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Gui {

    private final LibrarySystem librarySystem;
    private JFrame frame;
    private User loggedInUser;

    public Gui(LibrarySystem librarySystem) {
        this.librarySystem = librarySystem;
        initializeLogin();
    }

    private void initializeLogin() {
        frame = new JFrame("Library Information System - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);

        frame.add(panel);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            User user = librarySystem.authenticate(username, password);
            if (user != null) {
                loggedInUser = user;
                frame.dispose();
                if (user instanceof Admin) {
                    initializeAdminMenu();
                } else {
                    initializeUserMenu();
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    private void initializeAdminMenu() {
        frame = new JFrame("Library System - Admin Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1));

        JButton viewCatalogButton = new JButton("View Catalog");
        JButton searchBookButton = new JButton("Search Book");
        JButton addBookButton = new JButton("Add Book");
        JButton removeBookButton = new JButton("Remove Book");
        // added new buttons for add/remove user
        JButton addUserButton = new JButton("Add User");
        JButton removeUserButton = new JButton("Remove User");
        JButton viewUsersButton = new JButton("View Users");
        JButton viewLoansButton = new JButton("View Loans");
        JButton logoutButton = new JButton("Logout");

        panel.add(viewCatalogButton);
        panel.add(searchBookButton);
        panel.add(addBookButton);
        panel.add(removeBookButton);
        //added add/remove user buttons to the gui panel
        panel.add(addUserButton);
        panel.add(removeUserButton);
        panel.add(viewUsersButton);
        panel.add(viewLoansButton);
        panel.add(logoutButton);

        frame.add(panel);

        viewCatalogButton.addActionListener(e -> showCatalog());
        searchBookButton.addActionListener(e -> searchBook());
        addBookButton.addActionListener(e -> addBook());
        removeBookButton.addActionListener(e -> removeBook());
        // added action listeners for add/remove user
        addUserButton.addActionListener(e -> addUser());
        removeUserButton.addActionListener(e -> removeUser());
        viewUsersButton.addActionListener(e -> viewUsers());
        viewLoansButton.addActionListener(e -> viewLoans());
        logoutButton.addActionListener(e -> logout());

        frame.setVisible(true);
    }

    private void initializeUserMenu() {
        frame = new JFrame("Library System - User Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));

        JButton viewCatalogButton = new JButton("View Catalog");
        JButton searchBookButton = new JButton("Search Book");
        JButton borrowBookButton = new JButton("Borrow Book");
        JButton returnBookButton = new JButton("Return Book");
        JButton logoutButton = new JButton("Logout");

        panel.add(viewCatalogButton);
        panel.add(searchBookButton);
        panel.add(borrowBookButton);
        panel.add(returnBookButton);
        panel.add(logoutButton);

        frame.add(panel);

        viewCatalogButton.addActionListener(e -> showCatalog());
        searchBookButton.addActionListener(e -> searchBook());
        borrowBookButton.addActionListener(e -> borrowBook());
        returnBookButton.addActionListener(e -> returnBook());
        logoutButton.addActionListener(e -> logout());

        frame.setVisible(true);
    }

    private void logout() {
        loggedInUser = null;
        frame.dispose();
        initializeLogin();
        librarySystem.setLoggedInUser(null);
    }

    // private void showCatalog() {
    //     List<Book> books = librarySystem.getCatalog().getBooks();
    //     StringBuilder catalogDisplay = new StringBuilder("Books in the Catalog:\n\n");
    //     for (Book book : books) {
    //         catalogDisplay.append(book.toString()).append("\n");
    //     }
    //     JOptionPane.showMessageDialog(frame, catalogDisplay.toString(), "Catalog", JOptionPane.INFORMATION_MESSAGE);
    // }

    private void showCatalog() {
        try {
            // Fetch the list of books from the catalog
            List<Book> books = librarySystem.getCatalog().getBooks();
    
            // Define column names for the JTable
            String[] columnNames = {"Book ID", "Title", "Author", "Available"};
    
            // Prepare data for the JTable
            Object[][] data = new Object[books.size()][4];
            for (int i = 0; i < books.size(); i++) {
                Book book = books.get(i);
                data[i][0] = book.getId();
                data[i][1] = book.getTitle();
                data[i][2] = book.getAuthor();
                data[i][3] = book.isAvailable() ? "Yes" : "No";
            }
    
            // Create the JTable and place it in a JScrollPane
            JTable catalogTable = new JTable(data, columnNames);
            catalogTable.setEnabled(false); // Make the table non-editable
            JScrollPane scrollPane = new JScrollPane(catalogTable);
    
            // Display the table in a JOptionPane
            JOptionPane.showMessageDialog(frame, scrollPane, "View Catalog", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching catalog: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void searchBook() {
        String keyword = JOptionPane.showInputDialog(frame, "Enter a keyword to search for books:", "Search Book", JOptionPane.QUESTION_MESSAGE);
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<Book> results = librarySystem.getCatalog().searchBooks(keyword);
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No books found matching the keyword: " + keyword, "Search Results", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder resultDisplay = new StringBuilder("Search Results:\n\n");
                for (Book book : results) {
                    resultDisplay.append(book.toString()).append("\n");
                }
                JOptionPane.showMessageDialog(frame, resultDisplay.toString(), "Search Results", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void addBook() {
        try {
            String idInput = JOptionPane.showInputDialog(frame, "Enter Book ID:", "Add Book", JOptionPane.QUESTION_MESSAGE);
            String title = JOptionPane.showInputDialog(frame, "Enter Book Title:", "Add Book", JOptionPane.QUESTION_MESSAGE);
            String author = JOptionPane.showInputDialog(frame, "Enter Book Author:", "Add Book", JOptionPane.QUESTION_MESSAGE);

            if (idInput != null && title != null && author != null) {
                int id = Integer.parseInt(idInput);
                librarySystem.addBookManually(id, title, author);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeBook() {
        try {
            String bookIdInput = JOptionPane.showInputDialog(frame, "Enter Book ID to remove:", "Remove Book", JOptionPane.QUESTION_MESSAGE);
            if (bookIdInput != null) {
                int bookId = Integer.parseInt(bookIdInput);
                librarySystem.removeBookManually(bookId);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid Book ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

//    private void borrowBook() {
//        try {
//            String bookIdInput = JOptionPane.showInputDialog(frame, "Enter Book ID to borrow:", "Borrow Book", JOptionPane.QUESTION_MESSAGE);
//            if (bookIdInput != null) {
//                int bookId = Integer.parseInt(bookIdInput);
//                librarySystem.borrowBookManually(bookId, loggedInUser.getId());
//            }
//        } catch (NumberFormatException e) {
//            JOptionPane.showMessageDialog(frame, "Invalid Book ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }

    private void borrowBook() {
        try {
            String bookIdInput = JOptionPane.showInputDialog(frame, "Enter Book ID to borrow:", "Borrow Book", JOptionPane.QUESTION_MESSAGE);
            if (bookIdInput != null) {
                int bookId = Integer.parseInt(bookIdInput);
                librarySystem.borrowBook(bookId, loggedInUser.getId()); // Call borrowBook method
                JOptionPane.showMessageDialog(frame, "Book borrowed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Invalid Book ID", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Borrowing Error", JOptionPane.ERROR_MESSAGE);
        }
    }



//    private void returnBook() {
//        try {
//            String loanIdInput = JOptionPane.showInputDialog(frame, "Enter Loan ID to return:", "Return Book", JOptionPane.QUESTION_MESSAGE);
//            if (loanIdInput != null) {
//                int loanId = Integer.parseInt(loanIdInput);
//                librarySystem.returnBookManually(loanId);
//            }
//        } catch (NumberFormatException e) {
//            JOptionPane.showMessageDialog(frame, "Invalid Loan ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }

    private void returnBook() {
        try {
            String bookIdInput = JOptionPane.showInputDialog(frame, "Enter Book ID to return:", "Return Book", JOptionPane.QUESTION_MESSAGE);
            if (bookIdInput != null) {
                int bookId = Integer.parseInt(bookIdInput);
                librarySystem.returnBook(bookId); // Call the LibrarySystem method
                JOptionPane.showMessageDialog(frame, "Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Invalid Book ID", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Return Error", JOptionPane.ERROR_MESSAGE);
        }
    }



//    private void viewLoans() {
//        List<Loan> loans = librarySystem.getLoans();
//        StringBuilder loansDisplay = new StringBuilder("Loans:\n\n");
//        for (Loan loan : loans) {
//            loansDisplay.append(loan.toString()).append("\n");
//        }
//        JOptionPane.showMessageDialog(frame, loansDisplay.toString(), "Loans", JOptionPane.INFORMATION_MESSAGE);
//    }

    private void viewLoans() {
        try {
            // Fetch all loans
            List<Loan> loans = librarySystem.getAllLoans();

            // Prepare column names for the JTable
            String[] columnNames = {"Loan ID", "Book ID", "User ID", "Issue Date", "Due Date", "Returned"};

            // Prepare data for the JTable
            Object[][] data = new Object[loans.size()][6];
            for (int i = 0; i < loans.size(); i++) {
                Loan loan = loans.get(i);
                data[i][0] = loan.getLoanId();
                data[i][1] = loan.getBookId();
                data[i][2] = loan.getUserId();
                data[i][3] = loan.getIssueDate();
                data[i][4] = loan.getDueDate();
                data[i][5] = loan.isReturned() ? "Yes" : "No";
            }

            // Create the JTable and place it in a JScrollPane
            JTable loanTable = new JTable(data, columnNames);
            loanTable.setEnabled(false); // Make the table non-editable
            JScrollPane scrollPane = new JScrollPane(loanTable);

            // Show the table in a JOptionPane
            JOptionPane.showMessageDialog(frame, scrollPane, "View Loans", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching loans: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void addUser() {
        try {
            // Get user input from dialogs
            String idInput = JOptionPane.showInputDialog(frame, "Enter User ID:", "Add User", JOptionPane.QUESTION_MESSAGE);
            String username = JOptionPane.showInputDialog(frame, "Enter Username:", "Add User", JOptionPane.QUESTION_MESSAGE);
            String password = JOptionPane.showInputDialog(frame, "Enter Password:", "Add User", JOptionPane.QUESTION_MESSAGE);
            String[] roles = {"admin", "user"};
            String role = (String) JOptionPane.showInputDialog(frame, "Select Role:", "Add User", JOptionPane.QUESTION_MESSAGE, null, roles, roles[1]);

            // Ensure no input is null
            if (idInput != null && username != null && password != null && role != null) {
                int id = Integer.parseInt(idInput);

                // Check if the user already exists in the database
                String checkQuery = "SELECT COUNT(*) FROM users WHERE id = ?";
                try (PreparedStatement checkStmt = librarySystem.getConnection().prepareStatement(checkQuery)) {
                    checkStmt.setInt(1, id);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(frame, "User ID already exists. Cannot add duplicate user.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // Insert the new user into the database
                String insertQuery = "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = librarySystem.getConnection().prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, id);
                    insertStmt.setString(2, username);
                    insertStmt.setString(3, password); // Consider hashing passwords for security
                    insertStmt.setString(4, role);
                    insertStmt.executeUpdate();
                }

                // Show success message
                JOptionPane.showMessageDialog(frame, "User added successfully: " + username, "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid User ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error adding user to the database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void removeUser() {
        try {
            String idInput = JOptionPane.showInputDialog(frame, "Enter User ID to remove:", "Remove User", JOptionPane.QUESTION_MESSAGE);
            if (idInput != null) {
                int id = Integer.parseInt(idInput);

                // Prevent removing the root user with ID = 1
                if (id == 1) {
                    throw new UnsupportedOperationException("The root user cannot be removed.");
                }

                // Check if the user exists in the database
                String checkQuery = "SELECT COUNT(*) FROM users WHERE id = ?";
                try (PreparedStatement checkStmt = librarySystem.getConnection().prepareStatement(checkQuery)) {
                    checkStmt.setInt(1, id);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) == 0) {
                        JOptionPane.showMessageDialog(frame, "User with ID " + id + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // Remove the user from the database
                String deleteQuery = "DELETE FROM users WHERE id = ?";
                try (PreparedStatement deleteStmt = librarySystem.getConnection().prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, id);
                    int rowsAffected = deleteStmt.executeUpdate();

                    // Display messages based on the outcome
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(frame, "User with ID " + id + " removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, "User with ID " + id + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid User ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (UnsupportedOperationException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Operation Not Allowed", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error removing user from the database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewUsers() {
        try {
            List<User> users = librarySystem.getUsers();
    
            String[] columnNames = {"User ID", "Username", "Role"};
    
            Object[][] data = new Object[users.size()][3];
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                data[i][0] = user.getId();
                data[i][1] = user.getUsername();
                data[i][2] = user.getRole();
            }
    
            JTable userTable = new JTable(data, columnNames);
            userTable.setEnabled(false); // Make the table non-editable
            JScrollPane scrollPane = new JScrollPane(userTable);
    
            JOptionPane.showMessageDialog(frame, scrollPane, "View Users", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }    

}
