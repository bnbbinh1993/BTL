package com.example.btl.model;

public class Chat {
    private String name;
    private String messenger;
    private String uid;
    private String url;

    public Chat(String name, String messenger, String uid, String url) {
        this.name = name;
        this.messenger = messenger;
        this.uid = uid;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
