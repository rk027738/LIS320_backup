package BookManagement;

import java.util.ArrayList;
import java.util.List;

public class Catalog {
    private List<Book> books;

    public Catalog() {
        this.books = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(int bookId) {
        books.removeIf(book -> book.getId() == bookId);
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> results = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().contains(keyword) || book.getAuthor().contains(keyword)) {
                results.add(book);
            }
        }
        return results;
    }

    public List<Book> getBooks() {
        return books;
    }
}
