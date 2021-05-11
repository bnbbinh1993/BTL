package com.example.btl.model;

public class Point implements Comparable<Point> {
    private String uid;
    private String name;
    private String score;

    public Point(String uid, String name, String score) {
        this.uid = uid;
        this.name = name;
        this.score = score;
    }

    public Point() {
    }

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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }


    @Override
    public int compareTo(Point o) {
        return Integer.parseInt(o.getScore()) - Integer.parseInt(score);
    }
}
