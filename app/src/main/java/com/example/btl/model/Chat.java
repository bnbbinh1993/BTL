package com.example.btl.model;

public class Chat {
    private String name;
    private String messenger;
    private String uid;

    public Chat(String name, String messenger, String uid) {
        this.name = name;
        this.messenger = messenger;
        this.uid = uid;
    }

    public Chat() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessenger() {
        return messenger;
    }

    public void setMessenger(String messenger) {
        this.messenger = messenger;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
