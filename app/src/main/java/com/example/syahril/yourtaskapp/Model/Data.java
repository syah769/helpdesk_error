package com.example.syahril.yourtaskapp.Model;

import java.util.Comparator;

public class Data {

    private String title;
    private String note;
    private String staff;
    private String date;
    private String id;
    //new=0;
    //success=1;
    //pending=2;
    private int status;

    public String getParentNode() {
        return parentNode;
    }

    public void setParentNode(String parentNode) {
        this.parentNode = parentNode;
    }

    private String parentNode;


    public Data(){

    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Data(String title, String note, String staff, String date, String id, int status) {
        this.title = title;
        this.note = note;
        this.staff = staff;
        this.date = date;
        this.id = id;
        this.status=status;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static Comparator<Data> comparatortime = new Comparator<Data>() {
        @Override
        public int compare(Data o1, Data o2) {
            return o2.getDate().compareTo(o1.getDate());

        }
    };

}
