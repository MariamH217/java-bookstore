import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class CustomerOperations {

    static void updateCustomer(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the customer ID you want to update:");
        int customerId = scanner.nextInt();


        if (!isCustomerExisting(connection, customerId)) {
            System.out.println("Customer with ID " + customerId + " does not exist.");
            return;
        }


        scanner.nextLine();

        System.out.println("Enter the new customer name:");
        String newCustomerName = scanner.nextLine();

        System.out.println("Enter the new customer email:");
        String newCustomerEmail = scanner.nextLine();

        System.out.println("Enter the new customer phone:");
        String newCustomerPhone = scanner.nextLine();


        String updateQuery = "UPDATE customers SET name=?, email=?,phone=? WHERE customerid=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newCustomerName);
            preparedStatement.setString(2, newCustomerEmail);
            preparedStatement.setString(3, newCustomerPhone);
            preparedStatement.setInt(4, customerId);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Customer information updated successfully.");
            } else {
                System.out.println("Failed to update customer information.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating customer information.");
            e.printStackTrace();
        }
    }

    static boolean isCustomerExisting(Connection connection, int customerId) {
        String checkQuery = "SELECT COUNT(*) FROM customers WHERE customerid=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(checkQuery)) {
            preparedStatement.setInt(1, customerId);


            ResultSet resultSet = preparedStatement.executeQuery();


            resultSet.next();
            int count = resultSet.getInt(1);

            return count > 0;

        } catch (SQLException e) {
            System.out.println("Error checking if customer exists.");
            e.printStackTrace();
            return false;
        }
    }

    static void viewCustomerPurchaseHistory(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the customer ID to view purchase history:");
        int customerId = scanner.nextInt();


        if (!CustomerOperations.isCustomerExisting(connection, customerId)) {
            System.out.println("Customer with ID " + customerId + " does not exist.");
            return;
        }

        String purchaseHistoryQuery = "SELECT s.saleID, b.title, s.quantity_sold, s.total_price, s.date_of_sale " +
                "FROM sales s " +
                "JOIN books b ON s.bookID = b.bookID " +
                "WHERE s.customerID = ? " +
                "ORDER BY s.date_of_sale DESC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(purchaseHistoryQuery)) {
            preparedStatement.setInt(1, customerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("Customer Purchase History:");

                while (resultSet.next()) {
                    int saleId = resultSet.getInt("saleID");
                    String bookTitle = resultSet.getString("title");
                    int quantitySold = resultSet.getInt("quantity_sold");
                    double total_price = resultSet.getDouble("total_price");
                    String dateOfSale = resultSet.getString("date_of_sale");

                    System.out.println("Sale ID: " + saleId + ", Book: " + bookTitle +
                            ", Quantity Sold: " + quantitySold + ", Total Price: $" + total_price +
                            ", Date of Sale: " + dateOfSale);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving customer purchase history.");
            e.printStackTrace();
        }
    }


}
