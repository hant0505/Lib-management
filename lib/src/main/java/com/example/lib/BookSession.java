package com.example.lib;

public class BookSession {
    private static Book selectedBook;

    public static void setSelectedBook(Book book) {
        selectedBook = book;
    }

    public static Book getSelectedBook() {
        return selectedBook;
    }
}
