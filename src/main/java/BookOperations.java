import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BookOperations {
    static void updateBookDetails(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the book ID you want to update:");
        int bookId = scanner.nextInt();


        if (!isBookExisting(connection, bookId)) {
            System.out.println("Book with ID " + bookId + " does not exist.");
            return;
        }

        scanner.nextLine();

        System.out.println("Enter the new title:");
        String newTitle = scanner.nextLine();

        System.out.println("Enter the new author:");
        String newAuthor = scanner.nextLine();

        System.out.println("Enter the new genre:");
        String newGenre = scanner.nextLine();

        System.out.println("Enter the new price:");
        double newPrice = scanner.nextDouble();

        System.out.println("Enter the new quantity in stock:");
        int newQuantityInStock = scanner.nextInt();

        // Update the book details
        String updateQuery = "UPDATE books SET title=?, author=?, genre=?, price=?, quantity_in_stock=? WHERE bookid=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newTitle);
            preparedStatement.setString(2, newAuthor);
            preparedStatement.setString(3, newGenre);
            preparedStatement.setDouble(4, newPrice);
            preparedStatement.setInt(5, newQuantityInStock);
            preparedStatement.setInt(6, bookId);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Book details updated successfully.");
            } else {
                System.out.println("Failed to update book details.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating book details.");
            e.printStackTrace();
        }
    }

    static boolean isBookExisting(Connection connection, int bookId) {
        String checkQuery = "SELECT COUNT(*) FROM books WHERE bookid=?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(checkQuery)) {
            preparedStatement.setInt(1, bookId);

            // Execute the query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Check if the book exists by checking the count
                resultSet.next();
                int count = resultSet.getInt(1);

                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking if book exists.");
            e.printStackTrace();
            return false;
        }
    }

    static void listBooksByAuthor(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the author to list books:");
        String author = scanner.nextLine();

        // List books by author
        String query = "SELECT * FROM books WHERE author=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, author);


            ResultSet resultSet = preparedStatement.executeQuery();


            while (resultSet.next()) {
                int bookId = resultSet.getInt("bookid");
                String title = resultSet.getString("title");
                String genre = resultSet.getString("genre");
                double price = resultSet.getDouble("price");
                int quantityInStock = resultSet.getInt("quantity_in_stock");

                System.out.println("Book ID: " + bookId);
                System.out.println("Title: " + title);
                System.out.println("Genre: " + genre);
                System.out.println("Price: " + price);
                System.out.println("Quantity in Stock: " + quantityInStock);
                System.out.println("---------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Error listing books by author.");
            e.printStackTrace();
        }
    }


    static void listBooksByGenre(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the genre to list books:");
        String genre = scanner.nextLine();

        // List books by genre
        String query = "SELECT * FROM books WHERE genre=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, genre);


            ResultSet resultSet = preparedStatement.executeQuery();


            while (resultSet.next()) {
                int bookId = resultSet.getInt("bookid");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                double price = resultSet.getDouble("price");
                int quantityInStock = resultSet.getInt("quantity_in_stock");

                System.out.println("Book ID: " + bookId);
                System.out.println("Title: " + title);
                System.out.println("Author: " + author);
                System.out.println("Price: " + price);
                System.out.println("Quantity in Stock: " + quantityInStock);
                System.out.println("---------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Error listing books by genre.");
            e.printStackTrace();
        }
    }

    static double getBookPrice(Connection connection, int bookId) throws SQLException {
        String getPriceQuery = "SELECT price FROM books WHERE bookid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(getPriceQuery)) {
            preparedStatement.setInt(1, bookId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("price");
                }
            }
        }
        throw new SQLException("Book price not found for book ID: " + bookId);
    }

    static void generateBooksSoldReport(Connection connection) {
        String booksSoldReportQuery = "SELECT books.title AS book_title, customers.name, sales.date_of_sale " +
                "FROM sales " +
                "JOIN books ON sales.bookID = books.bookID " +
                "JOIN customers ON sales.customerID = customers.customerID " +
                "ORDER BY sales.date_of_sale DESC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(booksSoldReportQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("Books Sold Report:");

            while (resultSet.next()) {
                String bookTitle = resultSet.getString("book_title");
                String customerName = resultSet.getString("name");
                String dateOfSale = resultSet.getString("date_of_sale");

                System.out.println("Book Title: " + bookTitle + ", Customer Name: " + customerName +
                        ", Date of Sale: " + dateOfSale);
            }

        } catch (SQLException e) {
            System.out.println("Error generating books sold report.");
            e.printStackTrace();
        }
    }

}
