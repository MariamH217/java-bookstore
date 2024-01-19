import java.sql.*;
import java.util.Scanner;

public class Main {
    static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    static final String USER = "postgres";
    static final String PASSWORD = "password1234";

    public static void main(String[] args) {

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connected to the PostgresSQL server");
            executeOption(connection);
        } catch (SQLException e) {
            System.out.println("Connection failure.");
            e.printStackTrace();
        }
    }

    private static void executeOption(Connection connection) {
        int answer;

        do {
            Main.dbControl();
            Scanner sc = new Scanner(System.in);
            answer = sc.nextInt();

            switch (answer) {
                case 0:
                    System.out.println("Exiting the program. Goodbye!");
                    break;
                case 1:
                    BookOperations.updateBookDetails(connection);
                    break;
                case 2:
                    BookOperations.listBooksByGenre(connection);
                    break;
                case 3:
                    BookOperations.listBooksByAuthor(connection);
                    break;
                case 4:
                    CustomerOperations.updateCustomer(connection);
                    break;
                case 5:
                    CustomerOperations.viewCustomerPurchaseHistory(connection);
                    break;
                case 6:
                    SaleOperations.processNewSale(connection);
                    break;
                case 7:
                    SaleOperations.calculateTotalRevenueByGenre(connection);
                    break;
                case 8:
                    BookOperations.generateBooksSoldReport(connection);
                    break;
                default:
                    System.out.println("Invalid option. Please choose a valid option.");
            }
        } while (answer != 0);
    }

    private static void dbControl() {
        System.out.println("0. Exit the program");
        System.out.println("1. Update book details");
        System.out.println("2. List books by genre");
        System.out.println("3. List books by author");
        System.out.println("4. Update customer information");
        System.out.println("5. View customer purchase history");
        System.out.println("6. Make a new sale");
        System.out.println("7. Report of total revenue by genre");
        System.out.println("8. Report of all sold books");

    }


}
