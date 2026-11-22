package com.veerendra.library;

public record Member(int id, String name, String email) {
    @Override
    public String toString() {
        return String.format("ID: %d | %s | %s", id, name, email);
    }
}
