package com.example.wogprideanalog.data.model;

public class User {
    public String email;
    public String password;
    public int fuelAmount;
    public int accountBalance;
    public int coffeeCups;

    public User(String email, String password, int accountBalance, int fuelAmount, int coffeeCups) {
        this.email = email;
        this.password = password;
        this.accountBalance = accountBalance;
        this.fuelAmount = fuelAmount;
        this.coffeeCups = coffeeCups;
    }
}