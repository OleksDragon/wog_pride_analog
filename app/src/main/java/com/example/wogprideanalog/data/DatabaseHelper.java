package com.example.wogprideanalog.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.wogprideanalog.data.model.User;

import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "WogPride.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_USERS = "users";
    private static final String TABLE_ITEMS = "items";

    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_BALANCE = "accountBalance";
    private static final String COLUMN_FUEL = "fuelAmount";
    private static final String COLUMN_COFFEE = "coffeeCups";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";

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
}