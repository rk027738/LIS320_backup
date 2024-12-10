//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }
    }
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

}
