package com.example.lib;

import java.time.LocalDate;


public class Transact {
    private int transactionID;
    private String isbn;
    private String bookTitle;
    private String username;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private LocalDate returnedDate;
    private Boolean isReturned;
    private String status;


    public Transact(int transactionID, String username, String bookTitle, LocalDate borrowedDate, LocalDate dueDate, String status, LocalDate returnedDate) {
        this.transactionID = transactionID;
        this.username = username;
        this.bookTitle = bookTitle;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;

        if ("borrowed".equalsIgnoreCase(status)) {
            this.returnedDate = null;
            this.status = "Borrowed";
            this.isReturned = false;
        } else {
            this.returnedDate = returnedDate;
            this.isReturned = returnedDate != null;
            if (returnedDate != null && returnedDate.isAfter(dueDate)) {
                this.status = "Overdue";
            } else {
                this.status = status;
            }
        }
    }

    public Transact(String username, String bookTitle, LocalDate borrowedDate, LocalDate dueDate, String status, LocalDate returnedDate) {
        this.bookTitle = bookTitle;
        this.username = username;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.returnedDate = returnedDate;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(LocalDate borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDate returnedDate) {
        this.returnedDate = returnedDate;
    }

    public Boolean getReturned() {
        return isReturned;
    }

    public void setReturned(Boolean returned) {
        isReturned = returned;
    }

}


