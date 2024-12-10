package UserManagement;

import BookManagement.Book;
import BookManagement.Catalog;

public class Admin extends User{
    public Admin(int id, String username, String password, String role) {
        super(id, username, password, role);
    }

    public void addBook(Catalog catalog, Book book) {
        catalog.addBook(book);
        System.out.println("Book added: " + book.getTitle());
    }

    public void removeBook(Catalog catalog, int bookId) {
        catalog.removeBook(bookId);
        System.out.println("Book with ID " + bookId + " removed.");
    }
}
