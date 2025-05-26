package com.example.wogprideanalog.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wogprideanalog.data.model.User;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "WOGPrideAnalog.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_FUEL = "fuel";
    private static final String COLUMN_BALANCE = "balance";
    private static final String COLUMN_COFFEE = "coffee";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_EMAIL + " TEXT NOT NULL, " +
            COLUMN_PASSWORD + " TEXT NOT NULL, " +
            COLUMN_FUEL + " INTEGER DEFAULT 0, " +
            COLUMN_BALANCE + " INTEGER DEFAULT 0, " +
            COLUMN_COFFEE + " INTEGER DEFAULT 0)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.email);
        values.put(COLUMN_PASSWORD, user.password);
        values.put(COLUMN_FUEL, user.fuelAmount);
        values.put(COLUMN_BALANCE, user.accountBalance);
        values.put(COLUMN_COFFEE, user.coffeeCups);
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public User getUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        String[] columns = {COLUMN_ID, COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_FUEL, COLUMN_BALANCE, COLUMN_COFFEE};
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FUEL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BALANCE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COFFEE))
            );
        }
        cursor.close();
        db.close();
        return user;
    }
}