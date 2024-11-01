package com.example.lib;

public class BookLibrarian {
    private String ISBN;
    private String title;
    private String author;
    private int edition;
    private String description;

    public BookLibrarian(String ISBN, String title, String author, int edition, String description) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.edition = edition;
        this.description = description;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
