package com.example.wogprideanalog.data.model;

public class Transaction {
    public String userEmail;
    public String type;
    public String itemName;
    public int quantity;
    public int totalCost;
    public String date;

    public Transaction(String userEmail, String type, String itemName, int quantity, int totalCost, String date) {
        this.userEmail = userEmail;
        this.type = type;
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalCost = totalCost;
        this.date = date;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}