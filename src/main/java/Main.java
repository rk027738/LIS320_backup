import SystemManagement.Gui;
import SystemManagement.LibrarySystem;

public class Main {
    public static void main(String[] args) {
        System.out.println("Library System is starting...");
        LibrarySystem librarySystem = new LibrarySystem();
        new Gui(librarySystem); // Launch the GUI
    }
}
