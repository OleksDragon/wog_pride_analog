package com.example.wogprideanalog.data.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String email;
    public String password;
    public int accountBalance;
    public Map<String, Integer> fuelLitersByCategory;
    public Map<String, Integer> coffeeCupsByCategory;

    public User() {
        this.fuelLitersByCategory = new HashMap<>();
        this.coffeeCupsByCategory = new HashMap<>();
    }

    public User(String email, String password, int accountBalance, int fuelAmount, int coffeeCups) {
        this.email = email;
        this.password = password;
        this.accountBalance = accountBalance;
        this.fuelLitersByCategory = new HashMap<>();
        this.coffeeCupsByCategory = new HashMap<>();
        this.fuelLitersByCategory.put("A95", fuelAmount);
        this.coffeeCupsByCategory.put("Еспресо", coffeeCups);
    }

    public void addFuel(String category, int amount) {
        fuelLitersByCategory.put(category, fuelLitersByCategory.getOrDefault(category, 0) + amount);
    }

    public boolean getFuel(String category, int liters) {
        Integer currentLiters = fuelLitersByCategory.get(category);
        if (currentLiters != null && currentLiters >= liters) {
            fuelLitersByCategory.put(category, currentLiters - liters);
            return true;
        }
        return false;
    }

    public int getTotalFuelLiters() {
        int total = 0;
        for (Integer liters : fuelLitersByCategory.values()) {
            total += liters;
        }
        return total;
    }

    public void addCoffee(String category, int amount) {
        coffeeCupsByCategory.put(category, coffeeCupsByCategory.getOrDefault(category, 0) + amount);
    }

    public boolean getCoffee(String category, int cups) {
        int currentCups = coffeeCupsByCategory.getOrDefault(category, 0);
        if (currentCups >= cups) {
            coffeeCupsByCategory.put(category, currentCups - cups);
            return true;
        }
        return false;
    }

    public int getTotalCoffeeCups() {
        int total = 0;
        for (Integer count : coffeeCupsByCategory.values()) {
            total += count;
        }
        return total;
    }
}