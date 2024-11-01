package com.example.lib;

public class Book {
    private int bookID;
    private String title;
    private String author;
    private int edition;
    private int quantity;
    private String available;
    public Book(int bookID, String title, String author, int edition, int quantity, String available) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.edition = edition;
        this.quantity = quantity;
        this.available = available;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }
}
