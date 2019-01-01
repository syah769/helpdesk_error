package com.example.syahril.yourtaskapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.syahril.yourtaskapp.Model.Data;

import java.util.List;

public class CustomDatabaseManager {

    private Context context;
    private SQLiteDatabase db;
    private SqldatabaseHelper dbHelper;
    private CustomDAO cDAO;

    public CustomDatabaseManager(Context context) {

        this.context=context;
        dbHelper = new SqldatabaseHelper(this.context);
        db=dbHelper.getWritableDatabase();
        cDAO = new CustomDAO (db);
    }

    public List<Data> getAll() { return cDAO.getall();}

    public List<Data> getallbypending() { return cDAO.getallbypending();}
    public List<Data> getallbysuccess() { return cDAO.getallbysuccess();}

}
