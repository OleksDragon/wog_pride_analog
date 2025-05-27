package com.example.wogprideanalog.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.SurfaceControl;

import com.example.wogprideanalog.data.model.User;
import com.example.wogprideanalog.data.model.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "WogPride.db";
    private static final int DATABASE_VERSION = 3;
    public static final String TABLE_USERS = "users";
    private static final String TABLE_ITEMS = "items";
    private static final String TABLE_TRANSACTIONS = "transactions";

    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_BALANCE = "accountBalance";
    private static final String COLUMN_FUEL = "fuelAmount";
    private static final String COLUMN_COFFEE = "coffeeCups";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";

    private static final String COLUMN_TRANSACTION_ID = "transaction_id";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_TYPE = "type"; // "fuel" або "coffee"
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
                COLUMN_FUEL + " INTEGER, " +
                COLUMN_COFFEE + " INTEGER)";
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

    // Метод для додавання товару
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
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_BALANCE, COLUMN_FUEL, COLUMN_COFFEE},
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{email, password}, null, null, null);
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BALANCE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FUEL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COFFEE))
            );
        }
        cursor.close();
        return user;
    }

    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.email);
        values.put(COLUMN_PASSWORD, user.password);
        values.put(COLUMN_BALANCE, user.accountBalance);
        values.put(COLUMN_FUEL, user.fuelAmount);
        values.put(COLUMN_COFFEE, user.coffeeCups);
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BALANCE, user.accountBalance);
        values.put(COLUMN_FUEL, user.fuelAmount);
        values.put(COLUMN_COFFEE, user.coffeeCups);
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{user.email, user.password});
        db.close();
        return rowsAffected;
    }

    // Метод для додавання транзакції
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

    // Метод для получения всех транзакций пользователя
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