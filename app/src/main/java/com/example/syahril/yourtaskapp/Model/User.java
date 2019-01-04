package com.example.syahril.yourtaskapp.Model;



public class User {

    private String name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;
    private String user_id;

    public User(String name, String user_id,String email) {
        this.name = name;
        this.user_id = user_id;
        this.email=email;
    }

    public User() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return name;
    }



}

