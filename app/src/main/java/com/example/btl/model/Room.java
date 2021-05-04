package com.example.btl.model;

public class Room {
    private String name;
    private String password;
    private String topic;
    private String time;
    private String id;
    private String isPlay;
    private String isStop;

    public Room(String name, String password, String topic, String time, String id, String isPlay, String isStop) {
        this.name = name;
        this.password = password;
        this.topic = topic;
        this.time = time;
        this.id = id;
        this.isPlay = isPlay;
        this.isStop = isStop;
    }

    public Room() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsPlay() {
        return isPlay;
    }

    public void setIsPlay(String isPlay) {
        this.isPlay = isPlay;
    }

    public String getIsStop() {
        return isStop;
    }

    public void setIsStop(String isStop) {
        this.isStop = isStop;
    }
}
