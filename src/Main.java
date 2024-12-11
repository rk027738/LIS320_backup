import LoanManagement.Loan;
import SystemManagement.LibrarySystem;
import java.time.LocalDate;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Loan loan1 = new Loan(1, 101, 1, LocalDate.now(), LocalDate.now().plusDays(14));
        Loan loan2 = new Loan(2, 102, 2, LocalDate.now(), LocalDate.now().plusDays(7));

        // Display loan details
        System.out.println(loan1);
        System.out.println(loan2);

        // Mark the first loan as returned
        loan1.returnBook();

        // Display the updated loan details
        System.out.println(loan1);

        LibrarySystem librarySystem = new LibrarySystem();


        // Test 1
        System.out.println("Test 1: View Initial Catalog");
        System.out.println("Initial Catalog:");
        librarySystem.viewCatalog();

        // Test 2
        System.out.println("\nTest 2: Add a Book");
        librarySystem.addBookManually(4, "Clean Code", "Robert C. Martin");
        System.out.println("Catalog after adding 'Clean Code':");
        librarySystem.viewCatalog();

        // Test 3
        System.out.println("\nTest 3: Remove a Book");
        librarySystem.removeBookManually(2);
        System.out.println("Catalog after removing Book ID 2:");
        librarySystem.viewCatalog();

        // Test 4: searching for a book
        System.out.println("\nTest 4: Search for a Book");
        System.out.println("Search results for keyword '1984':");
        librarySystem.searchBookByKeyword("1984");

        // Test 5: borrowing a book
        System.out.println("\nTest 5: Borrow a Book");
        librarySystem.borrowBookManually(1, 2);
        System.out.println("Catalog after borrowing:");
        librarySystem.viewCatalog();
    }
}
