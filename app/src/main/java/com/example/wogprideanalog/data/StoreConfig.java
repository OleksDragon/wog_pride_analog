package com.example.wogprideanalog.data;

import java.util.HashMap;
import java.util.Map;

public class StoreConfig {

    private static Map<String, Item> fuelItems = new HashMap<>();
    private static Map<String, Item> coffeeItems = new HashMap<>();

    public static void initialize(DatabaseHelper dbHelper) {
        fuelItems.clear();
        coffeeItems.clear();

        fuelItems.putAll(dbHelper.getFuelItems());

        coffeeItems.putAll(dbHelper.getCoffeeItems());
    }

    public static Map<String, Item> getFuelItems() {
        return fuelItems;
    }

    public static Map<String, Item> getCoffeeItems() {
        return coffeeItems;
    }

    public static class Item {
        private String name;
        private int price;

        public Item(String name, int price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }
    }
}