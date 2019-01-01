package com.example.syahril.yourtaskapp;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataTable {

    static final String TABLENAME="TaskNote";

    static final String date="date";
    static final String id="id";
    static final String note="note";
    static final String staff="staff";
    static final String title="title";
    static final String status="status";

    static public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE " + TABLENAME + " (");
        sb.append(date + " text not null, ");
        sb.append(id + " integer primary key autoincrement, ");
        sb.append(note + " text not null, ");
        sb.append(staff + " text not null, ");
        sb.append(title + "text not null, ");
        sb.append(status + " text ");
        sb.append(");");

        try {
            db.execSQL(sb.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    static public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
        DataTable.onCreate(db);
    }


    }

