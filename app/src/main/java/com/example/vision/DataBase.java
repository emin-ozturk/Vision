package com.example.vision;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "languages";
    private static final String ROW_ID = "id";
    private static final String ROW_LANGUAGE = "language";

    DataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "("
                + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ROW_LANGUAGE + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    List<String> list() {
        List<String> data = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String[] row = {ROW_ID, ROW_LANGUAGE};
            Cursor cursor = db.query(TABLE_NAME, row, null, null, null, null, null);
            while (cursor.moveToNext()) {
                data.add(cursor.getInt(0)
                        + " - "
                        + cursor.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return data;
    }

    void insert(String language) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(ROW_LANGUAGE, language);
            db.insert(TABLE_NAME, null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    void update(int id, String language) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(ROW_LANGUAGE, language);
            String where = ROW_ID + " = '" + id + "'";
            db.update(TABLE_NAME, cv, where, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }
}
