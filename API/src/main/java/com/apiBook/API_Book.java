package com.apiBook;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;

public class API_Book {
    private String isbn;
    private String title;
    private String author;

    private String publishedDate;
    private int quantity;
    private String available;

    private String id;
    private String description;
    private String categories;
    private String language;

    private CheckBox checkBox;



    // Nháp
    public API_Book(String id, String title, String author, String categories, String language, String isbn, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.categories = categories;
        this.language = language;
        this.isbn = isbn;
        this.description = description;
        this.checkBox = new CheckBox();  // Khởi tạo CheckBox
    }

    // bản thứ hai
    public API_Book(String id, String title, String author, String publishedDate, String isbn, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publishedDate = publishedDate;
        this.isbn = isbn;
        this.description = description;
        //this.checkBox = new CheckBox();
    }

    /// sáng 15_11
    public API_Book(String isbn, String title, String author,String categories, int quantity, String available) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.categories = categories;
        this.quantity = quantity;
        this.available = available;
    }


    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }
}