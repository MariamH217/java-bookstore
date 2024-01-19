import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class SaleOperations {

    static void processNewSale(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the customer ID for the sale:");
        int customerId = scanner.nextInt();


        if (!CustomerOperations.isCustomerExisting(connection, customerId)) {
            System.out.println("Customer with ID " + customerId + " does not exist.");
            return;
        }

        scanner.nextLine();

        System.out.println("Enter the book ID for the sale:");
        int bookId = scanner.nextInt();


        if (!BookOperations.isBookExisting(connection, bookId)) {
            System.out.println("Book with ID " + bookId + " does not exist.");
            return;
        }

        System.out.println("Enter the quantity for the sale:");
        int quantity = scanner.nextInt();


        if (!isEnoughQuantityInStock(connection, bookId, quantity)) {
            System.out.println("Not enough quantity in stock for the sale.");
            return;
        }


        double bookPrice ;
        try {
            bookPrice = BookOperations.getBookPrice(connection, bookId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        double totalPrice = bookPrice * quantity;


        try {
            connection.setAutoCommit(false);

            insertSalesData(connection, customerId, bookId, quantity, totalPrice);


            connection.commit();
            System.out.println("Sale processed successfully.");

        } catch (SQLException e) {
            System.out.println("Error processing sale. Rolling back transaction.");
            e.printStackTrace();

            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                System.out.println("Error rolling back transaction.");
                rollbackException.printStackTrace();
            }

        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException autoCommitException) {
                System.out.println("Error setting auto-commit to true.");
                autoCommitException.printStackTrace();
            }
        }
    }


    static boolean isEnoughQuantityInStock(Connection connection, int bookId, int requestedQuantity) {
        String checkQuantityQuery = "SELECT quantity_in_stock FROM books WHERE bookid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(checkQuantityQuery)) {
            preparedStatement.setInt(1, bookId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int availableQuantity = resultSet.getInt("quantity_in_stock");
                    return availableQuantity >= requestedQuantity;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking quantity in stock for book ID: " + bookId);
            e.printStackTrace();
        }

        return false;
    }

    static void insertSalesData(Connection connection, int customerId, int bookId, int quantity, double totalPrice) throws SQLException {
        String insertSalesQuery = "INSERT INTO sales (customerID, bookID, quantity_sold, total_price, date_of_sale) VALUES (?, ?, ?, ?, current_date)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSalesQuery)) {
            preparedStatement.setInt(1, customerId);
            preparedStatement.setInt(2, bookId);
            preparedStatement.setInt(3, quantity);
            preparedStatement.setDouble(4, totalPrice);

            preparedStatement.executeUpdate();
        }
    }

    static void calculateTotalRevenueByGenre(Connection connection) {
        String revenueByGenreQuery = "SELECT b.genre, SUM(s.total_price) AS total_revenue " +
                "FROM sales s " +
                "JOIN books b ON s.bookID = b.bookID " +
                "GROUP BY b.genre";

        try (PreparedStatement preparedStatement = connection.prepareStatement(revenueByGenreQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("Total Revenue by Genre:");

            while (resultSet.next()) {
                String genre = resultSet.getString("genre");
                double totalRevenue = resultSet.getDouble("total_revenue");

                System.out.println("Genre: " + genre + ", Total Revenue: $" + totalRevenue);
            }

        } catch (SQLException e) {
            System.out.println("Error calculating total revenue by genre.");
            e.printStackTrace();
        }
    }
}
