package com.veerendra.library;

public record Book(int id, String title, String author, String isbn, int availableCopies) {
    @Override
    public String toString() {
        return String.format("ID: %d | %s | %s | ISBN: %s | Available: %d",
                id, title, author, isbn, availableCopies);
    }
}