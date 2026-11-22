package com.veerendra.library;

public record Loan(int id, String bookTitle, String memberName,
                   String issueDate, String dueDate, String returnDate) {
    @Override
    public String toString() {
        return String.format("Loan #%d | Book: %s | Member: %s | Issued: %s | Due: %s | Returned: %s",
                id, bookTitle, memberName, issueDate, dueDate,
                returnDate == null ? "Not yet" : returnDate);
    }
}
