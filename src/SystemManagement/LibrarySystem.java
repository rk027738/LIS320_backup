package SystemManagement;

import BookManagement.Book;
import BookManagement.Catalog;
import LoanManagement.Loan;
import UserManagement.Admin;
import UserManagement.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LibrarySystem {
    private final Catalog catalog;
    private final List<User> users;
    private final List<Loan> loans;
    private User loggedInUser;

    public LibrarySystem() {
        this.catalog = new Catalog();
        this.users = new ArrayList<>();
        this.loans = new ArrayList<>();
        this.loggedInUser = null;

        initializeSampleData();
    }

    private void initializeSampleData() {
        // added some booksto the db
        catalog.addBook(new Book(1, "1984", "George Orwell", true));
        catalog.addBook(new Book(2, "To Kill a Mockingbird", "Harper Lee", true));
        catalog.addBook(new Book(3, "The Great Gatsby", "F. Scott Fitzgerald", true));

        // added some users to the db
        users.add(new Admin(1, "admin", "password", "admin"));
        users.add(new User(2, "user1", "password", "user"));
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (loggedInUser == null) {
                login(scanner);
            } else {
                if (loggedInUser instanceof Admin) {
                    adminMenu(scanner);
                } else {
                    userMenu(scanner);
                }
            }
        }
    }

    private void login(Scanner scanner) {
        System.out.println("Welcome to the Library Information System!");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            User user = authenticate(username, password);
            if (user != null) {
                loggedInUser = user;
                System.out.println("Login successful! Welcome, " + user.getUsername());
            } else {
                System.out.println("Invalid credentials. Please try again.");
            }
        } else if (choice == 2) {
            System.out.println("Goodbye!");
            System.exit(0);
        } else {
            System.out.println("Invalid choice. Please try again.");
        }
    }

    User authenticate(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    private void adminMenu(Scanner scanner) {
        System.out.println("\nAdmin Menu:");
        System.out.println("1. View Catalog");
        System.out.println("2. Search Book");
        System.out.println("3. Add Book");
        System.out.println("4. Remove Book");
        System.out.println("5. View Loans");
        System.out.println("6. Logout");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> viewCatalog();
            case 2 -> searchBook(scanner);
            case 3 -> addBook(scanner);
            case 4 -> removeBook(scanner);
            case 5 -> viewLoans();
            case 6 -> logout();
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    private void userMenu(Scanner scanner) {
        System.out.println("\nUser Menu:");
        System.out.println("1. View Catalog");
        System.out.println("2. Search Book");
        System.out.println("3. Borrow Book");
        System.out.println("4. Return Book");
        System.out.println("5. Logout");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> viewCatalog();
            case 2 -> searchBook(scanner);
            case 3 -> borrowBook(scanner);
            case 4 -> returnBook(scanner);
            case 5 -> logout();
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    public void viewCatalog() {
        System.out.println("\nCatalog:");
        for (Book book : catalog.getBooks()) {
            System.out.println(book);
        }
    }

    //new method
    public Catalog getCatalog() {
        return catalog;
    }

    //new method
    public List<Loan> getLoans() {
        return loans;
    }

    public List<User> getUsers() {
        return users;
    }

    private void searchBook(Scanner scanner) {
        System.out.print("Enter keyword to search: ");
        String keyword = scanner.nextLine();
        List<Book> results = catalog.searchBooks(keyword);
        if (results.isEmpty()) {
            System.out.println("No books found matching the keyword.");
        } else {
            for (Book book : results) {
                System.out.println(book);
            }
        }
    }

    private void addBook(Scanner scanner) {
        System.out.print("Enter Book ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Author: ");
        String author = scanner.nextLine();
        catalog.addBook(new Book(id, title, author, true));
        System.out.println("Book added successfully!");
    }

    private void removeBook(Scanner scanner) {
        System.out.print("Enter Book ID to remove: ");
        int id = scanner.nextInt();
        catalog.removeBook(id);
        System.out.println("Book removed successfully!");
    }

    public void viewLoans() {
        System.out.println("\nLoans:");
        for (Loan loan : loans) {
            System.out.println(loan);
        }
    }

    private void borrowBook(Scanner scanner) {
        System.out.print("Enter Book ID to borrow: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();
        Book book = catalog.getBooks().stream()
                .filter(b -> b.getId() == bookId && b.isAvailable())
                .findFirst()
                .orElse(null);

        if (book == null) {
            System.out.println("Book is not available.");
        } else {
            book.setAvailable(false);
            Loan loan = new Loan(loans.size() + 1, bookId, loggedInUser.getId(), LocalDate.now(), LocalDate.now().plusDays(14));
            loans.add(loan);
            System.out.println("Book borrowed successfully! Loan ID: " + loan.getLoanId());
        }
    }

    private void returnBook(Scanner scanner) {
        System.out.print("Enter Loan ID to return: ");
        int loanId = scanner.nextInt();
        scanner.nextLine();
        Loan loan = loans.stream()
                .filter(l -> l.getLoanId() == loanId && !l.isReturned())
                .findFirst()
                .orElse(null);

        if (loan == null) {
            System.out.println("Invalid Loan ID or the book is already returned.");
        } else {
            loan.setReturned(true);
            catalog.getBooks().stream()
                    .filter(b -> b.getId() == loan.getBookId())
                    .findFirst()
                    .ifPresent(b -> b.setAvailable(true));
            System.out.println("Book returned successfully!");
        }
    }

    private void logout() {
        System.out.println("Logging out...");
        loggedInUser = null;
    }

    // added new methods to test the code in Main class without using scanner

    public void addBookManually(int id, String title, String author) {
        // checks if the book ID already exists
        if (catalog.getBooks().stream().anyMatch(book -> book.getId() == id)) {
            System.out.println("Book ID " + id + " already exists. Cannot add duplicate book.");
            return;
        }
        // adds book to catalog
        Book newBook = new Book(id, title, author, true);
        catalog.addBook(newBook);
        System.out.println("Book added: " + newBook);
    }

    public void removeBookManually(int bookId) {
        boolean removed = catalog.getBooks().removeIf(book -> book.getId() == bookId);
        if (removed) {
            System.out.println("Book with ID " + bookId + " removed successfully.");
        } else {
            System.out.println("Book with ID " + bookId + " not found.");
        }
    }

    public void borrowBookManually(int bookId, int userId) {
        Book bookToBorrow = catalog.getBooks().stream()
                .filter(book -> book.getId() == bookId && book.isAvailable())
                .findFirst()
                .orElse(null);

        if (bookToBorrow == null) {
            System.out.println("Book with ID " + bookId + " is not available for borrowing.");
            return;
        }

        Loan loan = new Loan(loans.size() + 1, bookId, userId, LocalDate.now(), LocalDate.now().plusDays(14));
        loans.add(loan);
        bookToBorrow.setAvailable(false);
        System.out.println("Book borrowed successfully: " + bookToBorrow);
        System.out.println("Loan details: " + loan);
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

    public void returnBookManually(int loanId) {
        Loan loanToReturn = loans.stream()
                .filter(loan -> loan.getLoanId() == loanId && !loan.isReturned())
                .findFirst()
                .orElse(null);

        if (loanToReturn == null) {
            System.out.println("Invalid Loan ID or the book is already returned.");
            return;
        }

        loanToReturn.setReturned(true);
        catalog.getBooks().stream()
                .filter(book -> book.getId() == loanToReturn.getBookId())
                .findFirst()
                .ifPresent(book -> book.setAvailable(true));

        System.out.println("Book returned successfully: Loan ID " + loanToReturn.getLoanId());
    }
}
