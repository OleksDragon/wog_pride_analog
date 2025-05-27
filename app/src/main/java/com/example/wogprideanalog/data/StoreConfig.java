package com.example.wogprideanalog.data;

import java.util.Map;

public class StoreConfig {
    public static class Item {
        private final String name;
        private final int price;

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

    private static DatabaseHelper dbHelper;

    public static void initialize(DatabaseHelper helper) {
        dbHelper = helper;
    }

    public static Map<String, Item> getFuelItems() {
        if (dbHelper == null) throw new IllegalStateException("DatabaseHelper not initialized");
        return dbHelper.getFuelItems();
    }

    public static Map<String, Item> getCoffeeItems() {
        if (dbHelper == null) throw new IllegalStateException("DatabaseHelper not initialized");
        return dbHelper.getCoffeeItems();
    }
}