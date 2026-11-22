package com.veerendra.library;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final LibraryService library = new LibraryService();

    public static void main(String[] args) {
        Database.initialize();
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("=================================");
            System.out.println("     LIBRARY MANAGEMENT SYSTEM");
            System.out.println("=================================");
            boolean running = true;
            while (running) {
                printMenu();
                int choice = InputHelper.readInt(scanner, "Choose an option: ");
                try {
                    switch (choice) {
                        case 1 -> addBook(scanner);
                        case 2 -> showBooks(scanner);
                        case 3 -> removeBook(scanner);
                        case 4 -> registerMember(scanner);
                        case 5 -> showMembers();
                        case 6 -> issueBook(scanner);
                        case 7 -> returnBook(scanner);
                        case 8 -> showHistory();
                        case 0 -> running = false;
                        default -> System.out.println("Invalid option. Try again.");
                    }
                } catch (SQLException | IllegalArgumentException | IllegalStateException e) {
                    System.out.println("Error: " + e.getMessage());
                }
                System.out.println();
            }
            System.out.println("Thank you for using the Library Management System!");
        }
    }

    private static void printMenu() {
        System.out.println("\n1. Add book");
        System.out.println("2. View / search books");
        System.out.println("3. Remove book");
        System.out.println("4. Register member");
        System.out.println("5. View members");
        System.out.println("6. Issue book");
        System.out.println("7. Return book");
        System.out.println("8. View issue/return history");
        System.out.println("0. Exit");
    }

    private static void addBook(Scanner s) throws SQLException {
        String title = InputHelper.readText(s, "Book title: ");
        String author = InputHelper.readText(s, "Author: ");
        String isbn = InputHelper.readText(s, "ISBN (optional): ");
        int copies = InputHelper.readInt(s, "Number of copies: ");
        library.addBook(title, author, isbn, copies);
        System.out.println("Book added successfully.");
    }

    private static void showBooks(Scanner s) throws SQLException {
        String search = InputHelper.readText(s, "Search by title/author (leave blank for all): ");
        var books = library.getBooks(search);
        if (books.isEmpty()) System.out.println("No books found.");
        else books.forEach(System.out::println);
    }

    private static void removeBook(Scanner s) throws SQLException {
        int id = InputHelper.readInt(s, "Book ID to remove: ");
        library.removeBook(id);
        System.out.println("Book removed successfully.");
    }

    private static void registerMember(Scanner s) throws SQLException {
        String name = InputHelper.readText(s, "Member name: ");
        String email = InputHelper.readText(s, "Member email: ");
        if (name.isBlank() || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Enter a name and a valid email address.");
        }
        library.registerMember(name, email);
        System.out.println("Member registered successfully.");
    }

    private static void showMembers() throws SQLException {
        var members = library.getMembers();
        if (members.isEmpty()) System.out.println("No members registered.");
        else members.forEach(System.out::println);
    }

    private static void issueBook(Scanner s) throws SQLException {
        int bookId = InputHelper.readInt(s, "Book ID: ");
        int memberId = InputHelper.readInt(s, "Member ID: ");
        library.issueBook(bookId, memberId);
        System.out.println("Book issued successfully. Due date is 14 days from today.");
    }

    private static void returnBook(Scanner s) throws SQLException {
        int loanId = InputHelper.readInt(s, "Active loan ID: ");
        library.returnBook(loanId);
        System.out.println("Book returned successfully.");
    }

    private static void showHistory() throws SQLException {
        var loans = library.getLoanHistory();
        if (loans.isEmpty()) System.out.println("No loan history available.");
        else loans.forEach(System.out::println);
    }
}
