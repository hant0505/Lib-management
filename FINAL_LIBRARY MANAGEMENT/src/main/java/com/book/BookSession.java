package com.book;

public class BookSession {
    private static Book selectedBook;

    public static Book getSelectedBook() {
        return selectedBook;
    }

    public static void setSelectedBook(Book book) {
        selectedBook = book;
    }
}
