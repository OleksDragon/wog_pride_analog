package com.example.wogprideanalog.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wogprideanalog.data.model.Transaction;
import com.example.wogprideanalog.data.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "WogPride.db";
    private static final int DATABASE_VERSION = 5;
    public static final String TABLE_USERS = "users";
    private static final String TABLE_ITEMS = "items";
    private static final String TABLE_TRANSACTIONS = "transactions";

    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_BALANCE = "accountBalance";
    private static final String COLUMN_FUEL_A95 = "fuel_a95";
    private static final String COLUMN_FUEL_A92 = "fuel_a92";
    private static final String COLUMN_FUEL_DIESEL = "fuel_diesel";
    private static final String COLUMN_COFFEE_ESPRESSO = "coffee_espresso";
    private static final String COLUMN_COFFEE_LATTE = "coffee_latte";
    private static final String COLUMN_COFFEE_AMERICANO = "coffee_americano";
    private static final String COLUMN_EXTRA_FUEL = "extra_fuel";
    private static final String COLUMN_EXTRA_COFFEE = "extra_coffee";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";

    private static final String COLUMN_TRANSACTION_ID = "transaction_id";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_ITEM_NAME = "item_name";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_TOTAL_COST = "total_cost";
    private static final String COLUMN_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_BALANCE + " INTEGER, " +
                COLUMN_FUEL_A95 + " INTEGER DEFAULT 0, " +
                COLUMN_FUEL_A92 + " INTEGER DEFAULT 0, " +
                COLUMN_FUEL_DIESEL + " INTEGER DEFAULT 0, " +
                COLUMN_COFFEE_ESPRESSO + " INTEGER DEFAULT 0, " +
                COLUMN_COFFEE_LATTE + " INTEGER DEFAULT 0, " +
                COLUMN_COFFEE_AMERICANO + " INTEGER DEFAULT 0, " +
                COLUMN_EXTRA_FUEL + " TEXT DEFAULT '{}', " +
                COLUMN_EXTRA_COFFEE + " TEXT DEFAULT '{}'" +
                ")";
        db.execSQL(createUsersTable);

        String createItemsTable = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CATEGORY + " TEXT NOT NULL, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_PRICE + " INTEGER NOT NULL)";
        db.execSQL(createItemsTable);

        String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_EMAIL + " TEXT NOT NULL, " +
                COLUMN_TYPE + " TEXT NOT NULL, " +
                COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
                COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                COLUMN_TOTAL_COST + " INTEGER NOT NULL, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_EMAIL + "))";
        db.execSQL(createTransactionsTable);

        initializeDefaultItems(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
            String createItemsTable = "CREATE TABLE " + TABLE_ITEMS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CATEGORY + " TEXT NOT NULL, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_PRICE + " INTEGER NOT NULL)";
            db.execSQL(createItemsTable);
            initializeDefaultItems(db);
        }
        if (oldVersion < 3) {
            String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                    COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_EMAIL + " TEXT NOT NULL, " +
                    COLUMN_TYPE + " TEXT NOT NULL, " +
                    COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
                    COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                    COLUMN_TOTAL_COST + " INTEGER NOT NULL, " +
                    COLUMN_DATE + " TEXT NOT NULL, " +
                    "FOREIGN KEY(" + COLUMN_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_EMAIL + "))";
            db.execSQL(createTransactionsTable);
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " RENAME TO " + TABLE_USERS + "_temp");
            onCreate(db);
            String selectQuery = "SELECT * FROM " + TABLE_USERS + "_temp";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    int oldFuelAmount = cursor.getInt(cursor.getColumnIndexOrThrow("fuelAmount"));
                    int oldCoffeeCups = cursor.getInt(cursor.getColumnIndexOrThrow("coffeeCups"));

                    User user = new User(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BALANCE)),
                            0, 0
                    );

                    user.addFuel("A95", oldFuelAmount);
                    user.addCoffee("Еспресо", oldCoffeeCups);
                    updateUser(user);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS + "_temp");
        }
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_EXTRA_FUEL + " TEXT DEFAULT '{}'");
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_EXTRA_COFFEE + " TEXT DEFAULT '{}'");
        }
    }

    private void initializeDefaultItems(SQLiteDatabase db) {
        insertItem(db, "fuel", "A95", 59);
        insertItem(db, "fuel", "A92", 55);
        insertItem(db, "fuel", "Дизель", 54);
        insertItem(db, "coffee", "Еспресо", 30);
        insertItem(db, "coffee", "Лате", 40);
        insertItem(db, "coffee", "Americano", 35);
    }

    private void insertItem(SQLiteDatabase db, String category, String name, int price) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PRICE, price);
        db.insert(TABLE_ITEMS, null, values);
    }

    // Функция для добавления или обновления товара
    public void updateOrAddItem(String category, String name, int price) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(
                TABLE_ITEMS,
                new String[]{COLUMN_ID},
                COLUMN_CATEGORY + "=? AND " + COLUMN_NAME + "=?",
                new String[]{category, name},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PRICE, price);
            db.update(
                    TABLE_ITEMS,
                    values,
                    COLUMN_CATEGORY + "=? AND " + COLUMN_NAME + "=?",
                    new String[]{category, name}
            );
            cursor.close();
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY, category);
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_PRICE, price);
            db.insert(TABLE_ITEMS, null, values);
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }

    public void addItem(String category, String name, int price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PRICE, price);
        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }

    public Map<String, StoreConfig.Item> getFuelItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, StoreConfig.Item> items = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + COLUMN_CATEGORY + " = ?", new String[]{"fuel"});
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                int price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                items.put(name, new StoreConfig.Item(name, price));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public Map<String, StoreConfig.Item> getCoffeeItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, StoreConfig.Item> items = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + COLUMN_CATEGORY + " = ?", new String[]{"coffee"});
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                int price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                items.put(name, new StoreConfig.Item(name, price));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public User getUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query(TABLE_USERS, new String[]{
                COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_BALANCE,
                COLUMN_FUEL_A95, COLUMN_FUEL_A92, COLUMN_FUEL_DIESEL,
                COLUMN_COFFEE_ESPRESSO, COLUMN_COFFEE_LATTE, COLUMN_COFFEE_AMERICANO,
                COLUMN_EXTRA_FUEL, COLUMN_EXTRA_COFFEE
        }, COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{email, password}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BALANCE)),
                    0, 0
            );
            user.fuelLitersByCategory.put("A95", cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FUEL_A95)));
            user.fuelLitersByCategory.put("A92", cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FUEL_A92)));
            user.fuelLitersByCategory.put("Дизель", cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FUEL_DIESEL)));
            user.coffeeCupsByCategory.put("Еспресо", cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COFFEE_ESPRESSO)));
            user.coffeeCupsByCategory.put("Лате", cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COFFEE_LATTE)));
            user.coffeeCupsByCategory.put("Americano", cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COFFEE_AMERICANO)));
            user.setExtraFuelFromJson(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXTRA_FUEL)));
            user.setExtraCoffeeFromJson(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXTRA_COFFEE)));
        }
        if (cursor != null) {
            cursor.close();
        }
        return user;
    }

    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.email);
        values.put(COLUMN_PASSWORD, user.password);
        values.put(COLUMN_BALANCE, user.accountBalance);
        values.put(COLUMN_FUEL_A95, user.fuelLitersByCategory.getOrDefault("A95", 0));
        values.put(COLUMN_FUEL_A92, user.fuelLitersByCategory.getOrDefault("A92", 0));
        values.put(COLUMN_FUEL_DIESEL, user.fuelLitersByCategory.getOrDefault("Дизель", 0));
        values.put(COLUMN_COFFEE_ESPRESSO, user.coffeeCupsByCategory.getOrDefault("Еспресо", 0));
        values.put(COLUMN_COFFEE_LATTE, user.coffeeCupsByCategory.getOrDefault("Лате", 0));
        values.put(COLUMN_COFFEE_AMERICANO, user.coffeeCupsByCategory.getOrDefault("Americano", 0));
        values.put(COLUMN_EXTRA_FUEL, user.getExtraFuelJson());
        values.put(COLUMN_EXTRA_COFFEE, user.getExtraCoffeeJson());

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BALANCE, user.accountBalance);
        values.put(COLUMN_FUEL_A95, user.fuelLitersByCategory.getOrDefault("A95", 0));
        values.put(COLUMN_FUEL_A92, user.fuelLitersByCategory.getOrDefault("A92", 0));
        values.put(COLUMN_FUEL_DIESEL, user.fuelLitersByCategory.getOrDefault("Дизель", 0));
        values.put(COLUMN_COFFEE_ESPRESSO, user.coffeeCupsByCategory.getOrDefault("Еспресо", 0));
        values.put(COLUMN_COFFEE_LATTE, user.coffeeCupsByCategory.getOrDefault("Лате", 0));
        values.put(COLUMN_COFFEE_AMERICANO, user.coffeeCupsByCategory.getOrDefault("Americano", 0));
        values.put(COLUMN_EXTRA_FUEL, user.getExtraFuelJson());
        values.put(COLUMN_EXTRA_COFFEE, user.getExtraCoffeeJson());

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{user.email, user.password});
        db.close();
        return rowsAffected;
    }

    public void addTransaction(String userEmail, String type, String itemName, int quantity, int totalCost, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, userEmail);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_TOTAL_COST, totalCost);
        values.put(COLUMN_DATE, date);
        db.insert(TABLE_TRANSACTIONS, null, values);
        db.close();
    }

    public List<Transaction> getUserTransactions(String userEmail) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_TRANSACTIONS,
                new String[]{
                        COLUMN_USER_EMAIL,
                        COLUMN_TYPE,
                        COLUMN_ITEM_NAME,
                        COLUMN_QUANTITY,
                        COLUMN_TOTAL_COST,
                        COLUMN_DATE
                },
                COLUMN_USER_EMAIL + "=?",
                new String[]{userEmail},
                null,
                null,
                COLUMN_DATE + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_COST)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                );
                transactions.add(transaction);
            } while (cursor.moveToNext());

            cursor.close();
        }
        return transactions;
    }
}