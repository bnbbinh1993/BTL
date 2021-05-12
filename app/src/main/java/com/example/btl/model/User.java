package com.example.btl.model;

public class User {
    private String uid;
    private String name;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User() {
    }


    public User(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }
}
