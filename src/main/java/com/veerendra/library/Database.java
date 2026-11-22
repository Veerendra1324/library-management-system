package com.veerendra.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private static final String URL = "jdbc:sqlite:library.db";

    private Database() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initialize() {
        String[] statements = {
            "CREATE TABLE IF NOT EXISTS books (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, author TEXT NOT NULL, isbn TEXT UNIQUE, " +
                "available_copies INTEGER NOT NULL CHECK(available_copies >= 0))",
            "CREATE TABLE IF NOT EXISTS members (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, email TEXT NOT NULL UNIQUE)",
            "CREATE TABLE IF NOT EXISTS loans (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "book_id INTEGER NOT NULL REFERENCES books(id), " +
                "member_id INTEGER NOT NULL REFERENCES members(id), " +
                "issue_date TEXT NOT NULL, due_date TEXT NOT NULL, return_date TEXT)"
        };

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            for (String sql : statements) statement.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not initialize the database: " + e.getMessage(), e);
        }
    }
}
