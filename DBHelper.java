package com.example.powerpay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "PinkBillDB";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "bill_history";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, month TEXT, units INTEGER, total REAL, final REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertBill(String month, int units, double total, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("month", month);
        values.put("units", units);
        values.put("total", total);
        values.put("final", finalCost);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<String> getAllBills() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            String entry = cursor.getString(1) + " - " + cursor.getInt(2) + "kWh - RM" + String.format("%.2f", cursor.getDouble(4));
            list.add(entry);
        }
        cursor.close();
        db.close();
        return list;
    }
}