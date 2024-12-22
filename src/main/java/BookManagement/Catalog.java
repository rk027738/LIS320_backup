package BookManagement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;

public class Catalog {
    private final Connection connection;

    public Catalog(Connection connection) {
        this.connection = connection;
        initializeBooksTable();
    }

    private void initializeBooksTable() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS books (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "author VARCHAR(255) NOT NULL, " +
                    "is_available BOOLEAN NOT NULL" +
                    ")");
            System.out.println("Books table is ready.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBook(Book book) {
        String query = "INSERT INTO books (title, author, is_available) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setBoolean(3, book.isAvailable());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeBook(int bookId) {
        String deleteLoansQuery = "DELETE FROM loans WHERE book_id = ?";
        String deleteBookQuery = "DELETE FROM books WHERE id = ?";

        try (PreparedStatement deleteLoansStmt = connection.prepareStatement(deleteLoansQuery);
             PreparedStatement deleteBookStmt = connection.prepareStatement(deleteBookQuery)) {

            // Delete dependent rows in 'loans' table
            deleteLoansStmt.setInt(1, bookId);
            deleteLoansStmt.executeUpdate();

            // Delete the book from 'books' table
            deleteBookStmt.setInt(1, bookId);
            deleteBookStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Book> searchBooks(String keyword) {
        String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        List<Book> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBoolean("is_available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<Book> getBooks() {
        String query = "SELECT * FROM books";
        List<Book> books = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBoolean("is_available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}
