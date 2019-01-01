package com.example.syahril.yourtaskapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqldatabaseHelper extends SQLiteOpenHelper {

    static final String DB_NAME="mytask";
    static final int version=1;

    public SqldatabaseHelper(Context context) {super(context,DB_NAME, null,version);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        DataTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DataTable.onUpgrade(db,oldVersion,newVersion);
    }
}
