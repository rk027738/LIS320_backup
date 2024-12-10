import LoanManagement.Loan;

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
    }
}
