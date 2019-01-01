package com.example.syahril.yourtaskapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.syahril.yourtaskapp.Model.Data;

import java.util.ArrayList;
import java.util.List;

public class CustomDAO {

    private SQLiteDatabase sdb;

    public CustomDAO (SQLiteDatabase sdb) { this.sdb =sdb; }

    public long save(Data co)
    {
        ContentValues cv = new ContentValues();
        cv.put(DataTable.date, String.valueOf(System.currentTimeMillis()));
        cv.put(DataTable.id,co.getId());
        cv.put(DataTable.note,co.getNote());
        cv.put(DataTable.staff,co.getStaff());
        cv.put(DataTable.status,co.getStatus());
        cv.put(DataTable.status, "0");

        return sdb.insert(DataTable.TABLENAME, null,cv);
    }


    public boolean Delete(Data co)
    {
        return sdb.delete(DataTable.TABLENAME, DataTable.id+"=?",new String[]{co.getId()+" "})>0;

    }

    public Data get(long id)
    {
        Data co = null;
        Cursor c=sdb.query(true,DataTable.TABLENAME,new String[]{DataTable.date,DataTable.id,DataTable.note,DataTable.staff,DataTable.title},DataTable.id+" =?",
                new String[]{id+" "}, null, null,null,null,null);

        if (c!=null&&c.moveToFirst())
        {
            co=buildfromcursor(c);
            if (!c.isClosed())
            {
                c.close();
            }
        }

        return co;
    }

    private Data buildfromcursor(Cursor c)
    {
        Data co=null;
        if (c!=null)
        {
            co=new Data();
            co.setDate(c.getString(0));
            co.setId(c.getString(1));
            co.setNote(c.getString(2));
            co.setStaff(c.getString(3));
            co.setTitle(c.getString(4));

        }

        return co;
    }

    public List<Data> getall()
    {
        List<Data> colist = new ArrayList<Data>();
        Cursor c = sdb.query(DataTable.TABLENAME,new String[]{DataTable.date,DataTable.id,
                DataTable.note,DataTable.staff,DataTable.title}, null,null,null,null,null);
        if (c!=null && c.moveToFirst())
        {
            do {
                Data co = buildfromcursor(c);
                if (co!=null)
                {
                    colist.add(co);
                }
            }while (c.moveToNext());
            if (!c.isClosed())
            {
                c.close();
            }
        }
        return colist;

    }

    public List<Data> getallbypending()
    {
        List<Data> colist = new ArrayList<Data>();
        Cursor c = sdb.query(DataTable.TABLENAME,new String[]{DataTable.date,DataTable.id,
                DataTable.note,DataTable.staff,DataTable.title}, null,null,null,null,null);
        if (c!=null && c.moveToFirst())
        {
            do {
                Data co = buildfromcursor(c);
                if (co!=null)
                {
                    colist.add(co);
                }
            }while (c.moveToNext());
            if (!c.isClosed())
            {
                c.close();
            }
        }
        return colist;

    }

    public List<Data> getallbysuccess()
    {
        List<Data> colist = new ArrayList<Data>();
        Cursor c = sdb.query(DataTable.TABLENAME,new String[]{DataTable.date,DataTable.id,
                DataTable.note,DataTable.staff,DataTable.title}, null,null,null,null,null);
        if (c!=null && c.moveToFirst())
        {
            do {
                Data co = buildfromcursor(c);
                if (co!=null)
                {
                    colist.add(co);
                }
            }while (c.moveToNext());
            if (!c.isClosed())
            {
                c.close();
            }
        }
        return colist;

    }
}
