package com.example.wogprideanalog.data.model;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class User {
    public String email;
    public String password;
    public int accountBalance;
    public Map<String, Integer> fuelLitersByCategory;
    public Map<String, Integer> coffeeCupsByCategory;
    private Map<String, Integer> extraFuel;
    private Map<String, Integer> extraCoffee;

    public User(String email, String password, int accountBalance, int fuelLiters, int coffeeCups) {
        this.email = email;
        this.password = password;
        this.accountBalance = accountBalance;
        this.fuelLitersByCategory = new HashMap<>();
        this.coffeeCupsByCategory = new HashMap<>();
        this.extraFuel = new HashMap<>();
        this.extraCoffee = new HashMap<>();
    }

    public void addFuel(String fuelType, int liters) {
        if (fuelType.equals("A95") || fuelType.equals("A92") || fuelType.equals("Дизель")) {
            fuelLitersByCategory.put(fuelType, fuelLitersByCategory.getOrDefault(fuelType, 0) + liters);
        } else {
            extraFuel.put(fuelType, extraFuel.getOrDefault(fuelType, 0) + liters);
        }
    }

    public boolean getFuel(String category, int liters) {
        Integer currentLiters = fuelLitersByCategory.get(category);
        if (currentLiters != null && currentLiters >= liters) {
            fuelLitersByCategory.put(category, currentLiters - liters);
            return true;
        }
        return false;
    }

    public void addCoffee(String coffeeType, int cups) {
        if (coffeeType.equals("Еспресо") || coffeeType.equals("Лате") || coffeeType.equals("Americano")) {
            coffeeCupsByCategory.put(coffeeType, coffeeCupsByCategory.getOrDefault(coffeeType, 0) + cups);
        } else {
            extraCoffee.put(coffeeType, extraCoffee.getOrDefault(coffeeType, 0) + cups);
        }
    }

    public String getExtraFuelJson() {
        return new JSONObject(extraFuel).toString();
    }

    public String getExtraCoffeeJson() {
        return new JSONObject(extraCoffee).toString();
    }

    public void setExtraFuelFromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            extraFuel.clear();
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                extraFuel.put(key, jsonObject.getInt(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setExtraCoffeeFromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            extraCoffee.clear();
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                extraCoffee.put(key, jsonObject.getInt(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getAllFuel() {
        Map<String, Integer> allFuel = new HashMap<>(fuelLitersByCategory);
        allFuel.putAll(extraFuel);
        return allFuel;
    }

    public Map<String, Integer> getAllCoffee() {
        Map<String, Integer> allCoffee = new HashMap<>(coffeeCupsByCategory);
        allCoffee.putAll(extraCoffee);
        return allCoffee;
    }

    // Метод для получения общего количества чашек кофе
    public int getTotalCoffeeCups() {
        int total = 0;
        for (int cups : coffeeCupsByCategory.values()) {
            total += cups;
        }
        for (int cups : extraCoffee.values()) {
            total += cups;
        }
        return total;
    }

    // Метод для "получения" кофе (уменьшения количества чашек)
    public boolean getCoffee(String coffeeType, int cups) {
        if (cups <= 0) {
            return false;
        }

        if (coffeeCupsByCategory.containsKey(coffeeType)) {
            int availableCups = coffeeCupsByCategory.getOrDefault(coffeeType, 0);
            if (availableCups >= cups) {
                coffeeCupsByCategory.put(coffeeType, availableCups - cups);
                return true;
            }
        }

        if (extraCoffee.containsKey(coffeeType)) {
            int availableCups = extraCoffee.getOrDefault(coffeeType, 0);
            if (availableCups >= cups) {
                extraCoffee.put(coffeeType, availableCups - cups);
                if (extraCoffee.get(coffeeType) == 0) {
                    extraCoffee.remove(coffeeType);
                }
                return true;
            }
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

}