package com.veerendra.library;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibraryService {

    public void addBook(String title, String author, String isbn, int copies) throws SQLException {
        if (copies < 1) throw new IllegalArgumentException("Copies must be at least 1.");
        String sql = "INSERT INTO books(title, author, isbn, available_copies) VALUES(?,?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title.trim());
            ps.setString(2, author.trim());
            ps.setString(3, isbn.isBlank() ? null : isbn.trim());
            ps.setInt(4, copies);
            ps.executeUpdate();
        }
    }

    public List<Book> getBooks(String search) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE lower(title) LIKE ? OR lower(author) LIKE ? ORDER BY title";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String term = "%" + search.toLowerCase() + "%";
            ps.setString(1, term);
            ps.setString(2, term);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) books.add(new Book(rs.getInt("id"), rs.getString("title"),
                        rs.getString("author"), rs.getString("isbn"), rs.getInt("available_copies")));
            }
        }
        return books;
    }

    public void removeBook(int bookId) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ? AND NOT EXISTS " +
                "(SELECT 1 FROM loans WHERE book_id = books.id AND return_date IS NULL)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Book not found or currently issued. Return it before removal.");
            }
        }
    }

    public void registerMember(String name, String email) throws SQLException {
        String sql = "INSERT INTO members(name, email) VALUES(?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, email.trim().toLowerCase());
            ps.executeUpdate();
        }
    }

    public List<Member> getMembers() throws SQLException {
        List<Member> members = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM members ORDER BY name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) members.add(new Member(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
        }
        return members;
    }

    public void issueBook(int bookId, int memberId) throws SQLException {
        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement check = c.prepareStatement(
                        "SELECT available_copies FROM books WHERE id = ?")) {
                    check.setInt(1, bookId);
                    try (ResultSet rs = check.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("Book ID not found.");
                        if (rs.getInt(1) < 1) throw new IllegalArgumentException("No copies are currently available.");
                    }
                }
                try (PreparedStatement check = c.prepareStatement("SELECT id FROM members WHERE id = ?")) {
                    check.setInt(1, memberId);
                    try (ResultSet rs = check.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("Member ID not found.");
                    }
                }
                try (PreparedStatement update = c.prepareStatement(
                        "UPDATE books SET available_copies = available_copies - 1 WHERE id = ? AND available_copies > 0")) {
                    update.setInt(1, bookId);
                    if (update.executeUpdate() != 1) throw new IllegalStateException("Could not reserve a copy.");
                }
                LocalDate today = LocalDate.now();
                try (PreparedStatement insert = c.prepareStatement(
                        "INSERT INTO loans(book_id, member_id, issue_date, due_date) VALUES(?,?,?,?)")) {
                    insert.setInt(1, bookId);
                    insert.setInt(2, memberId);
                    insert.setString(3, today.toString());
                    insert.setString(4, today.plusDays(14).toString());
                    insert.executeUpdate();
                }
                c.commit();
            } catch (Exception e) {
                c.rollback();
                if (e instanceof SQLException sqlException) throw sqlException;
                if (e instanceof RuntimeException runtimeException) throw runtimeException;
                throw new SQLException(e);
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public void returnBook(int loanId) throws SQLException {
        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);
            try {
                int bookId;
                try (PreparedStatement find = c.prepareStatement(
                        "SELECT book_id FROM loans WHERE id = ? AND return_date IS NULL")) {
                    find.setInt(1, loanId);
                    try (ResultSet rs = find.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("Active loan ID not found.");
                        bookId = rs.getInt(1);
                    }
                }
                try (PreparedStatement updateLoan = c.prepareStatement(
                        "UPDATE loans SET return_date = ? WHERE id = ? AND return_date IS NULL")) {
                    updateLoan.setString(1, LocalDate.now().toString());
                    updateLoan.setInt(2, loanId);
                    updateLoan.executeUpdate();
                }
                try (PreparedStatement updateBook = c.prepareStatement(
                        "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?")) {
                    updateBook.setInt(1, bookId);
                    updateBook.executeUpdate();
                }
                c.commit();
            } catch (Exception e) {
                c.rollback();
                if (e instanceof SQLException sqlException) throw sqlException;
                if (e instanceof RuntimeException runtimeException) throw runtimeException;
                throw new SQLException(e);
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public List<Loan> getLoanHistory() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.id, b.title, m.name, l.issue_date, l.due_date, l.return_date " +
                "FROM loans l JOIN books b ON l.book_id=b.id JOIN members m ON l.member_id=m.id ORDER BY l.id DESC";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) loans.add(new Loan(rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6)));
        }
        return loans;
    }
}
