package com.example.wogprideanalog.data.model;

public class User {
    public String email;
    public String password;
    public int fuelAmount;
    public int accountBalance;
    public int coffeeCups;

    public User(String email, String password, int fuelAmount, int accountBalance, int coffeeCups) {
        this.email = email;
        this.password = password;
        this.fuelAmount = fuelAmount;
        this.accountBalance = accountBalance;
        this.coffeeCups = coffeeCups;
    }
}