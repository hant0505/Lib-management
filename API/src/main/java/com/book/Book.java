package com.book;

import javafx.scene.control.CheckBox;

public class Book {
    private String bookID;
    private String title;
    private String author;
    private int edition;
    private int quantity;
    private String available;
    private String isbn;
    private String description;
    private CheckBox checkBox;

    private String categories;



    public Book(String title, String author, int edition, int quantity, String available) {
        this.title = title;
        this.author = author;
        this.edition = edition;
        this.quantity = quantity;
        this.available = available;

    }

    public Book(String bookID, String title, String author, int edition, int quantity, String available) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.edition = edition;
        this.quantity = quantity;
        this.available = available;
        this.checkBox = new CheckBox();
    }
    ///HANT
    public Book(String bookID, String title, String author, int quantity, String available) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.quantity = quantity;
        this.available = available;
        this.checkBox = new CheckBox();
    }

    //Hant
    public Book(String bookID, String title, String author, String categories, int quantity, String available) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.categories = categories;
        this.quantity = quantity;
        this.available = available;
        //this.checkBox = new CheckBox();
    }


    public Book(String bookID, String title, String author, int edition, int quantity,String available, String isbn, String description) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.edition = edition;
        this.quantity = quantity;
        this.available = available;
        this.isbn = isbn;
        this.description = description;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
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
