package LoanManagement;

import java.time.LocalDate;

public class Loan {
    private int loanId;
    private int bookId;
    private int userId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private boolean isReturned;

    // Constructor to create a new loan record
    public Loan(int loanId, int bookId, int userId, LocalDate issueDate, LocalDate dueDate) {
        this.loanId = loanId;
        this.bookId = bookId;
        this.userId = userId;
        this.issueDate = issueDate;
        this.dueDate = issueDate.plusDays(7); // fixed loan duration to 7 days to prevent manual setting
        this.isReturned = false;  // Default is that the book hasn't been returned
    }

    // Getters and setters
    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    // Method to mark the loan as returned
    public void returnBook() {
        this.isReturned = true;
        System.out.println("Loan " + loanId + " for book ID " + bookId + " has been returned.");
    }

    @Override
    public String toString() {
        return "Loan ID: " + loanId + ", Book ID: " + bookId + ", User ID: " + userId +
               ", Issue Date: " + issueDate + ", Due Date: " + dueDate + ", Returned: " + isReturned;
    }
}
